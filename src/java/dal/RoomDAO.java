package dal;

import Model.InvoiceItem;
import Model.Room;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RoomDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public List<Room> getRoom() {
        List<Room> rooms = new ArrayList<>();
        try {
            String sql = """
                SELECT r.*, 
                       (SELECT COUNT(*) 
                        FROM DR_Contracts c 
                        JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID 
                        WHERE c.RoomID = r.RoomID AND c.Status = 'Active') AS CurrentOccupants
                FROM DR_Rooms r
            """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                int roomId = rs.getInt("RoomID");
                String roomNumber = rs.getString("RoomNumber");
                int capaCity = rs.getInt("Capacity");
                int monthlyRent = rs.getInt("MonthlyRent");
                String status = rs.getString("Status");
                int currentOccupants = rs.getInt("CurrentOccupants");

                Room room = new Room(roomId, roomNumber, capaCity, monthlyRent, status, currentOccupants);
                rooms.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public Room findCurrentRoomByStudentId(int studentId) {
        String sql = """
            SELECT TOP 1
                r.RoomID, r.RoomNumber, r.Capacity, r.MonthlyRent, r.Status
            FROM DR_ContractStudents cs
            JOIN DR_Contracts c  ON c.ContractID = cs.ContractID
            JOIN DR_Rooms r      ON r.RoomID = c.RoomID
            WHERE cs.StudentID = ?
              AND c.Status = 'Active'
            ORDER BY c.StartDate DESC, c.ContractID DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Room r = new Room();
                    r.setRoomID(rs.getInt("RoomID"));
                    r.setRoomNumber(rs.getString("RoomNumber"));
                    r.setCapacity(rs.getInt("Capacity"));
                    r.setMonthlyRent(rs.getInt("MonthlyRent"));
                    r.setStatus(rs.getString("Status"));
                    return r;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertRoom(Room r) {
        String sql = "INSERT INTO DR_Rooms(RoomNumber, Capacity, MonthlyRent, Status) VALUES (?,?,?,?)";
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, r.getRoomNumber());
            st.setInt(2, r.getCapacity());
            st.setInt(3, r.getMonthlyRent());
            st.setString(4, r.getStatus()); // Sẽ lưu "Trống", "Đang thuê", "Đã cọc"...
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // =========================================================================
    // HÀM MỚI: KIỂM TRA PHÒNG CÓ ĐANG ĐƯỢC THUÊ KHÔNG TRƯỚC KHI XÓA
    // =========================================================================
    public boolean isRoomHasActiveContract(int roomId) {
        String sql = "SELECT 1 FROM DR_Contracts WHERE RoomID = ? AND Status = 'Active'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Nếu có dữ liệu trả về true (có người thuê)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteRoom(int roomId) {
        try {
            connection.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Xóa lịch sử Thanh toán (Payments) và Chi tiết hóa đơn (InvoiceItems)
            String delPayments = "DELETE FROM DR_Payments WHERE InvoiceID IN (SELECT InvoiceID FROM DR_Invoices WHERE ContractID IN (SELECT ContractID FROM DR_Contracts WHERE RoomID = ?))";
            try (PreparedStatement ps = connection.prepareStatement(delPayments)) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
            }

            String delInvoiceItems = "DELETE FROM DR_InvoiceItems WHERE InvoiceID IN (SELECT InvoiceID FROM DR_Invoices WHERE ContractID IN (SELECT ContractID FROM DR_Contracts WHERE RoomID = ?))";
            try (PreparedStatement ps = connection.prepareStatement(delInvoiceItems)) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
            }

            // 2. Xóa Hóa đơn (Invoices)
            String delInvoices = "DELETE FROM DR_Invoices WHERE ContractID IN (SELECT ContractID FROM DR_Contracts WHERE RoomID = ?)";
            try (PreparedStatement ps = connection.prepareStatement(delInvoices)) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
            }

            // 3. Xóa liên kết Sinh viên - Hợp đồng (ContractStudents)
            String delContractStudents = "DELETE FROM DR_ContractStudents WHERE ContractID IN (SELECT ContractID FROM DR_Contracts WHERE RoomID = ?)";
            try (PreparedStatement ps = connection.prepareStatement(delContractStudents)) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
            }

            // 4. Xóa Hợp đồng (Contracts)
            String delContracts = "DELETE FROM DR_Contracts WHERE RoomID = ?";
            try (PreparedStatement ps = connection.prepareStatement(delContracts)) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
            }

            // 5. Xóa Chỉ số điện nước (MeterReadings)
            String delMeters = "DELETE FROM DR_MeterReadings WHERE RoomID = ?";
            try (PreparedStatement ps = connection.prepareStatement(delMeters)) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
            }

            // 6. Cuối cùng, xóa Phòng (Rooms)
            String delRoom = "DELETE FROM DR_Rooms WHERE RoomID = ?";
            boolean isDeleted = false;
            try (PreparedStatement ps = connection.prepareStatement(delRoom)) {
                ps.setInt(1, roomId);
                isDeleted = ps.executeUpdate() > 0;
            }

            connection.commit(); // Thành công tất cả thì xác nhận lưu thay đổi
            return isDeleted;

        } catch (Exception e) {
            try {
                connection.rollback(); // Nếu có bất kỳ lỗi nào xảy ra thì hoàn tác toàn bộ
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true); // Trả lại trạng thái commit mặc định
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public Room getRoomById(int id) {
        String sql = "SELECT * FROM DR_Rooms WHERE RoomID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Room(
                            rs.getInt("RoomID"),
                            rs.getString("RoomNumber"),
                            rs.getInt("Capacity"),
                            rs.getInt("MonthlyRent"),
                            rs.getString("Status"),
                            0 // Truyền số 0 vào vì bảng gốc không có cột này
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateRoom(Room r) {
        String sql = "UPDATE DR_Rooms SET RoomNumber=?, Capacity=?, MonthlyRent=?, Status=? WHERE RoomID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, r.getRoomNumber());
            ps.setInt(2, r.getCapacity());
            ps.setInt(3, r.getMonthlyRent());
            ps.setString(4, r.getStatus());
            ps.setInt(5, r.getRoomID());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();

        String sql = """
    SELECT * FROM (
        SELECT r.*, 
               (SELECT COUNT(*) 
                FROM DR_Contracts c 
                JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID 
                WHERE c.RoomID = r.RoomID AND c.Status = 'Active') AS CurrentOccupants
        FROM DR_Rooms r 
    ) AS TempRoom
    WHERE CurrentOccupants < Capacity
""";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("RoomID"),
                        rs.getString("RoomNumber"),
                        rs.getInt("Capacity"),
                        rs.getInt("MonthlyRent"),
                        rs.getString("Status"),
                        rs.getInt("CurrentOccupants")
                );
                rooms.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public List<InvoiceItem> getLatestUnitPrices(int roomId) {
        List<InvoiceItem> list = new ArrayList<>();

        String sql = """
            SELECT TOP 1 ElectricPrice, WaterPrice, WifiPrice, GarbagePrice 
            FROM DR_Contracts 
            WHERE RoomID = ? AND Status = 'Active'
            ORDER BY ContractID DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                InvoiceItem electric = new InvoiceItem();
                electric.setDescription("Electricity");
                electric.setUnitPrice(rs.getDouble("ElectricPrice"));
                list.add(electric);

                InvoiceItem water = new InvoiceItem();
                water.setDescription("Water");
                water.setUnitPrice(rs.getDouble("WaterPrice"));
                list.add(water);

                InvoiceItem wifi = new InvoiceItem();
                wifi.setDescription("Wifi");
                wifi.setUnitPrice(rs.getDouble("WifiPrice"));
                list.add(wifi);

                InvoiceItem garbage = new InvoiceItem();
                garbage.setDescription("Garbage Fee");
                garbage.setUnitPrice(rs.getDouble("GarbagePrice"));
                list.add(garbage);
            } else {
                return getDefaultFeeTypes();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<InvoiceItem> getDefaultFeeTypes() {
        List<InvoiceItem> list = new ArrayList<>();
        String sql = "SELECT FeeName as Description, DefaultPrice as UnitPrice FROM DR_FeeTypes";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                InvoiceItem item = new InvoiceItem();
                item.setDescription(rs.getString("Description"));
                item.setUnitPrice(rs.getDouble("UnitPrice"));
                list.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getRoomIdByContractId(int contractId) {
        String sql = "SELECT RoomID FROM DR_Contracts WHERE ContractID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("RoomID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Room> searchRooms(String txt) {
        List<Room> rooms = new ArrayList<>();
        try {
            String sql = """
                SELECT r.*, 
                       (SELECT COUNT(*) 
                        FROM DR_Contracts c 
                        JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID 
                        WHERE c.RoomID = r.RoomID AND c.Status = 'Active') AS CurrentOccupants
                FROM DR_Rooms r
                WHERE r.RoomNumber LIKE ? OR r.Status LIKE ?
            """;
            st = connection.prepareStatement(sql);
            st.setString(1, "%" + txt + "%");
            // Thêm chữ N phía trước để tìm kiếm tiếng Việt có dấu (NĐang thuê, NTrống...)
            st.setString(2, "%" + txt + "%");
            rs = st.executeQuery();
            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("RoomID"),
                        rs.getString("RoomNumber"),
                        rs.getInt("Capacity"),
                        rs.getInt("MonthlyRent"),
                        rs.getString("Status"),
                        rs.getInt("CurrentOccupants"));
                rooms.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public int getTotalRooms() {
        try {
            String sql = "SELECT COUNT(*) FROM DR_Rooms";
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Room> getRoomsByPage(int offset, int limit) {
        List<Room> rooms = new ArrayList<>();
        try {
            String sql = """
                SELECT r.*, 
                       (SELECT COUNT(*) FROM DR_Contracts c JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID WHERE c.RoomID = r.RoomID AND c.Status = 'Active') AS CurrentOccupants
                FROM DR_Rooms r 
                ORDER BY r.RoomID 
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;
            st = connection.prepareStatement(sql);
            st.setInt(1, offset);
            st.setInt(2, limit);
            rs = st.executeQuery();
            while (rs.next()) {
                rooms.add(new Room(rs.getInt("RoomID"), rs.getString("RoomNumber"), rs.getInt("Capacity"), rs.getInt("MonthlyRent"), rs.getString("Status"), rs.getInt("CurrentOccupants")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public int getTotalRoomsBySearch(String txt) {
        try {
            String sql = "SELECT COUNT(*) FROM DR_Rooms WHERE RoomNumber LIKE ? OR Status LIKE ?";
            st = connection.prepareStatement(sql);
            st.setString(1, "%" + txt + "%");
            st.setString(2, "%" + txt + "%");
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Room> searchRoomsByPage(String txt, int offset, int limit) {
        List<Room> rooms = new ArrayList<>();
        try {
            String sql = """
                SELECT r.*, 
                       (SELECT COUNT(*) FROM DR_Contracts c JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID WHERE c.RoomID = r.RoomID AND c.Status = 'Active') AS CurrentOccupants
                FROM DR_Rooms r 
                WHERE r.RoomNumber LIKE ? OR r.Status LIKE ?
                ORDER BY r.RoomID 
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;
            st = connection.prepareStatement(sql);
            st.setString(1, "%" + txt + "%");
            st.setString(2, "%" + txt + "%");
            st.setInt(3, offset);
            st.setInt(4, limit);
            rs = st.executeQuery();
            while (rs.next()) {
                rooms.add(new Room(rs.getInt("RoomID"), rs.getString("RoomNumber"), rs.getInt("Capacity"), rs.getInt("MonthlyRent"), rs.getString("Status"), rs.getInt("CurrentOccupants")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    // =========================================================================
    // LẤY DANH SÁCH CÁC PHÒNG ĐANG CÓ NGƯỜI THUÊ (Dùng EXISTS để chống trùng lặp)
    // =========================================================================
    public List<Room> getRentedRooms() {
        List<Room> list = new ArrayList<>();

        // Dùng EXISTS: Mỗi phòng chỉ xuất hiện 1 lần duy nhất trên Dropdown 
        // cho dù có 10 người đang ở ghép (10 hợp đồng) đi chăng nữa.
        String sql = "SELECT RoomID, RoomNumber "
                + "FROM DR_Rooms r "
                + "WHERE EXISTS ("
                + "    SELECT 1 FROM DR_Contracts c "
                + "    WHERE c.RoomID = r.RoomID AND c.Status = 'Active'"
                + ") "
                + "ORDER BY RoomNumber ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Room r = new Room();
                r.setRoomID(rs.getInt("RoomID"));
                r.setRoomNumber(rs.getString("RoomNumber"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================================
    // CÁC HÀM CẬP NHẬT GIÁ ĐIỆN NƯỚC (LƯU TRONG BẢNG DR_Contracts)
    // =========================================================================
    public int getElectricPrice(int roomId) {
        int price = 3500; // Giá mặc định nếu phòng trống chưa có hợp đồng

        // Lấy giá điện từ hợp đồng đang Active của phòng
        String sql = "SELECT TOP 1 ElectricPrice FROM DR_Contracts WHERE RoomID = ? AND Status = 'Active' ORDER BY ContractID DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    price = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return price;
    }

    public int getWaterPrice(int roomId) {
        int price = 15000; // Giá mặc định nếu phòng trống chưa có hợp đồng

        // Lấy giá nước từ hợp đồng đang Active của phòng
        String sql = "SELECT TOP 1 WaterPrice FROM DR_Contracts WHERE RoomID = ? AND Status = 'Active' ORDER BY ContractID DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    price = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return price;
    }

    public void updateServicePrices(int roomId, int electricPrice, int waterPrice) {
        // Cập nhật trực tiếp giá điện nước vào Hợp đồng đang hoạt động của phòng này
        String sql = "UPDATE DR_Contracts SET ElectricPrice = ?, WaterPrice = ? WHERE RoomID = ? AND Status = 'Active'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, electricPrice);
            ps.setInt(2, waterPrice);
            ps.setInt(3, roomId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================================
    // 2 HÀM MỚI ĐỂ LỌC THEO NHIỀU TIÊU CHÍ (ROOM, CAPACITY, PRICE, STATUS)
    // ==========================================================
    public int getTotalRoomsAdvanced(String roomNumber, String capacity, String minPrice, String maxPrice, String status) {
        String sql = "SELECT COUNT(*) FROM DR_Rooms WHERE 1=1 ";

        if (roomNumber != null && !roomNumber.trim().isEmpty()) {
            sql += " AND RoomNumber LIKE ? ";
        }
        if (capacity != null && !capacity.trim().isEmpty()) {
            sql += " AND Capacity = ? ";
        }
        if (minPrice != null && !minPrice.trim().isEmpty()) {
            sql += " AND MonthlyRent >= ? ";
        }
        if (maxPrice != null && !maxPrice.trim().isEmpty()) {
            sql += " AND MonthlyRent <= ? ";
        }
        if (status != null && !status.trim().isEmpty()) {
            sql += " AND Status = ? ";
        }

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            int paramIndex = 1;

            if (roomNumber != null && !roomNumber.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + roomNumber.trim() + "%");
            }
            if (capacity != null && !capacity.trim().isEmpty()) {
                st.setInt(paramIndex++, Integer.parseInt(capacity.trim()));
            }
            if (minPrice != null && !minPrice.trim().isEmpty()) {
                st.setInt(paramIndex++, Integer.parseInt(minPrice.trim()));
            }
            if (maxPrice != null && !maxPrice.trim().isEmpty()) {
                st.setInt(paramIndex++, Integer.parseInt(maxPrice.trim()));
            }
            if (status != null && !status.trim().isEmpty()) {
                st.setString(paramIndex++, status.trim());
            }

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Room> searchRoomsAdvanced(String roomNumber, String capacity, String minPrice, String maxPrice, String status, int offset, int limit) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.*, "
                + "(SELECT COUNT(*) FROM DR_Contracts c JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID WHERE c.RoomID = r.RoomID AND c.Status = 'Active') AS CurrentOccupants "
                + "FROM DR_Rooms r WHERE 1=1 ";

        if (roomNumber != null && !roomNumber.trim().isEmpty()) {
            sql += " AND r.RoomNumber LIKE ? ";
        }
        if (capacity != null && !capacity.trim().isEmpty()) {
            sql += " AND r.Capacity = ? ";
        }
        if (minPrice != null && !minPrice.trim().isEmpty()) {
            sql += " AND r.MonthlyRent >= ? ";
        }
        if (maxPrice != null && !maxPrice.trim().isEmpty()) {
            sql += " AND r.MonthlyRent <= ? ";
        }
        if (status != null && !status.trim().isEmpty()) {
            sql += " AND r.Status = ? ";
        }

        sql += " ORDER BY r.RoomID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            int paramIndex = 1;

            if (roomNumber != null && !roomNumber.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + roomNumber.trim() + "%");
            }
            if (capacity != null && !capacity.trim().isEmpty()) {
                st.setInt(paramIndex++, Integer.parseInt(capacity.trim()));
            }
            if (minPrice != null && !minPrice.trim().isEmpty()) {
                st.setInt(paramIndex++, Integer.parseInt(minPrice.trim()));
            }
            if (maxPrice != null && !maxPrice.trim().isEmpty()) {
                st.setInt(paramIndex++, Integer.parseInt(maxPrice.trim()));
            }
            if (status != null && !status.trim().isEmpty()) {
                st.setString(paramIndex++, status.trim());
            }

            st.setInt(paramIndex++, offset);
            st.setInt(paramIndex++, limit);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(new Room(
                        rs.getInt("RoomID"),
                        rs.getString("RoomNumber"),
                        rs.getInt("Capacity"),
                        rs.getInt("MonthlyRent"),
                        rs.getString("Status"),
                        rs.getInt("CurrentOccupants")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean checkRoomExist(String roomNumber, int currentRoomId) {
        String sql = "SELECT 1 FROM DR_Rooms WHERE RoomNumber = ? AND RoomID != ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            ps.setInt(2, currentRoomId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getCurrentOccupants(int roomId) {
        String sql = "SELECT COUNT(*) FROM DR_Contracts c JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID WHERE c.RoomID = ? AND c.Status = 'Active'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
