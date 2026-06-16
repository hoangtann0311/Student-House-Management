package dal;

import Model.MeterReading;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MeterReadingDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public List<MeterReading> getAll() {
        List<MeterReading> list = new ArrayList<>();
        String sql = "SELECT m.*, r.RoomNumber FROM DR_MeterReadings m "
                + "JOIN DR_Rooms r ON m.RoomID = r.RoomID "
                + "ORDER BY m.ReadingYear DESC, m.ReadingMonth DESC, r.RoomNumber ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MeterReading m = new MeterReading(
                        rs.getInt("ReadingID"),
                        rs.getInt("RoomID"),
                        rs.getString("RoomNumber"),
                        rs.getInt("ReadingMonth"),
                        rs.getInt("ReadingYear"),
                        rs.getInt("ElectricityIndex"),
                        rs.getInt("WaterIndex")
                );
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm chỉ số mới VÀ tự động tạo Hóa đơn
    public boolean insertMeterReading(int roomId, int month, int year, int electric, int water) {
        boolean result = false;
        try {
            connection.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Lưu chỉ số vào bảng DR_MeterReadings
            String sqlInsertMeter = "INSERT INTO DR_MeterReadings (RoomID, ReadingMonth, ReadingYear, ElectricityIndex, WaterIndex) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psMeter = connection.prepareStatement(sqlInsertMeter)) {
                psMeter.setInt(1, roomId);
                psMeter.setInt(2, month);
                psMeter.setInt(3, year);
                psMeter.setInt(4, electric);
                psMeter.setInt(5, water);
                psMeter.executeUpdate();
            }

            // 2. Tìm chỉ số cũ để tính tiêu thụ
            int prevElectric = 0, prevWater = 0;
            String sqlPrev = "SELECT TOP 1 ElectricityIndex, WaterIndex FROM DR_MeterReadings "
                    + "WHERE RoomID = ? AND (ReadingYear < ? OR (ReadingYear = ? AND ReadingMonth < ?)) "
                    + "ORDER BY ReadingYear DESC, ReadingMonth DESC";
            try (PreparedStatement psPrev = connection.prepareStatement(sqlPrev)) {
                psPrev.setInt(1, roomId);
                psPrev.setInt(2, year);
                psPrev.setInt(3, year);
                psPrev.setInt(4, month);
                try (ResultSet rsPrev = psPrev.executeQuery()) {
                    if (rsPrev.next()) {
                        prevElectric = rsPrev.getInt("ElectricityIndex");
                        prevWater = rsPrev.getInt("WaterIndex");
                    }
                }
            }

            // Đã sửa logic tính toán: Nếu là tháng đầu (prev = 0) thì tiêu thụ chính là chỉ số mới
            int electricUsage = Math.max(0, electric - prevElectric);
            int waterUsage = Math.max(0, water - prevWater);

            // 3. Lấy thông tin Hợp đồng và đơn giá
            int contractId = -1;
            double monthlyRent = 0, priceE = 0, priceW = 0, priceWifi = 0, priceGar = 0;

            String sqlContract = """
    SELECT TOP 1 c.ContractID, c.ElectricPrice, c.WaterPrice, c.WifiPrice, c.GarbagePrice,
                 r.MonthlyRent
    FROM DR_Contracts c
    JOIN DR_Rooms r ON c.RoomID = r.RoomID
    WHERE c.RoomID = ? AND c.Status = 'Active' 
    ORDER BY c.ContractID DESC""";

            try (PreparedStatement psContract = connection.prepareStatement(sqlContract)) {
                psContract.setInt(1, roomId);
                try (ResultSet rsContract = psContract.executeQuery()) {
                    if (rsContract.next()) {
                        contractId = rsContract.getInt("ContractID");
                        monthlyRent = rsContract.getDouble("MonthlyRent");
                        priceE = rsContract.getDouble("ElectricPrice");
                        priceW = rsContract.getDouble("WaterPrice");
                        priceWifi = rsContract.getDouble("WifiPrice");
                        priceGar = rsContract.getDouble("GarbagePrice");
                    }
                }
            }

            // 4. Tạo Hóa đơn
            if (contractId != -1) {
                int invoiceId = -1;
                String dueDateStr = String.format("%04d-%02d-05", (month == 12 ? year + 1 : year), (month == 12 ? 1 : month + 1));

                String sqlInvoice = "INSERT INTO DR_Invoices (ContractID, InvoiceMonth, InvoiceYear, IssueDate, DueDate, Status, TotalAmount) "
                        + "VALUES (?, ?, ?, GETDATE(), ?, 'Unpaid', 0)";

                try (PreparedStatement psInvoice = connection.prepareStatement(sqlInvoice, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    psInvoice.setInt(1, contractId);
                    psInvoice.setInt(2, month);
                    psInvoice.setInt(3, year);
                    psInvoice.setString(4, dueDateStr);
                    psInvoice.executeUpdate();
                    try (ResultSet rsKeys = psInvoice.getGeneratedKeys()) {
                        if (rsKeys.next()) {
                            invoiceId = rsKeys.getInt(1);
                        }
                    }
                }

                if (invoiceId != -1) {
                    // 5. Thêm chi tiết phí
                    String sqlItem = "INSERT INTO DR_InvoiceItems (InvoiceID, FeeTypeID, Description, Quantity, UnitPrice) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement psItem = connection.prepareStatement(sqlItem)) {
                        Object[][] items = {
                            {1, "Tiền phòng", 1, monthlyRent},
                            {2, "Tiền điện", electricUsage, priceE},
                            {3, "Tiền nước", waterUsage, priceW},
                            {4, "Internet", 1, priceWifi},
                            {5, "Rác & Dịch vụ", 1, priceGar}
                        };
                        for (Object[] item : items) {
                            int qty = Integer.parseInt(item[2].toString());
                            double up = Double.parseDouble(item[3].toString());

                            psItem.setInt(1, invoiceId);
                            psItem.setInt(2, (int) item[0]);
                            psItem.setString(3, (String) item[1]);
                            psItem.setInt(4, qty);
                            psItem.setDouble(5, up);
                            // SỬA LẠI: Xóa hẳn dòng psItem.setDouble(6, qty * up); này đi
                            psItem.addBatch();
                        }
                        psItem.executeBatch();
                    }

                    // 6. Cập nhật tổng tiền cuối cùng
                    String sqlUpdateTotal = "UPDATE DR_Invoices SET TotalAmount = (SELECT SUM(Amount) FROM DR_InvoiceItems WHERE InvoiceID = ?) WHERE InvoiceID = ?";
                    try (PreparedStatement psUpdate = connection.prepareStatement(sqlUpdateTotal)) {
                        psUpdate.setInt(1, invoiceId);
                        psUpdate.setInt(2, invoiceId);
                        psUpdate.executeUpdate();
                    }
                }
            }

            connection.commit();
            result = true;
        } catch (Exception e) {
            System.out.println("LỖI insertMeterReading: " + e.getMessage());
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public MeterReading getLatestReadingByRoom(int roomId) {
        String sql = "SELECT TOP 1 * FROM DR_MeterReadings WHERE RoomID = ? ORDER BY ReadingYear DESC, ReadingMonth DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MeterReading(
                            rs.getInt("ReadingID"),
                            rs.getInt("RoomID"),
                            "",
                            rs.getInt("ReadingMonth"),
                            rs.getInt("ReadingYear"),
                            rs.getInt("ElectricityIndex"),
                            rs.getInt("WaterIndex")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteMeterReadingSafely(int readingId) {
        boolean result = false;
        try {
            connection.setAutoCommit(false);

            int roomId = -1, month = -1, year = -1;
            String sqlGetInfo = "SELECT RoomID, ReadingMonth, ReadingYear FROM DR_MeterReadings WHERE ReadingID = ?";
            try (PreparedStatement psInfo = connection.prepareStatement(sqlGetInfo)) {
                psInfo.setInt(1, readingId);
                try (ResultSet rsInfo = psInfo.executeQuery()) {
                    if (rsInfo.next()) {
                        roomId = rsInfo.getInt("RoomID");
                        month = rsInfo.getInt("ReadingMonth");
                        year = rsInfo.getInt("ReadingYear");
                    }
                }
            }

            if (roomId != -1) {
                int invoiceId = -1;
                String sqlGetInvoice = "SELECT i.InvoiceID FROM DR_Invoices i "
                        + "JOIN DR_Contracts c ON i.ContractID = c.ContractID "
                        + "WHERE c.RoomID = ? AND i.InvoiceMonth = ? AND i.InvoiceYear = ?";
                try (PreparedStatement psInv = connection.prepareStatement(sqlGetInvoice)) {
                    psInv.setInt(1, roomId);
                    psInv.setInt(2, month);
                    psInv.setInt(3, year);
                    try (ResultSet rsInv = psInv.executeQuery()) {
                        if (rsInv.next()) {
                            invoiceId = rsInv.getInt("InvoiceID");
                        }
                    }
                }

                if (invoiceId != -1) {
                    try (PreparedStatement psDelPay = connection.prepareStatement("DELETE FROM DR_Payments WHERE InvoiceID = ?")) {
                        psDelPay.setInt(1, invoiceId);
                        psDelPay.executeUpdate();
                    }
                    try (PreparedStatement psDelItems = connection.prepareStatement("DELETE FROM DR_InvoiceItems WHERE InvoiceID = ?")) {
                        psDelItems.setInt(1, invoiceId);
                        psDelItems.executeUpdate();
                    }
                    try (PreparedStatement psDelInv = connection.prepareStatement("DELETE FROM DR_Invoices WHERE InvoiceID = ?")) {
                        psDelInv.setInt(1, invoiceId);
                        psDelInv.executeUpdate();
                    }
                }
            }

            String sqlDelMeter = "DELETE FROM DR_MeterReadings WHERE ReadingID = ?";
            try (PreparedStatement psDelMeter = connection.prepareStatement(sqlDelMeter)) {
                psDelMeter.setInt(1, readingId);
                psDelMeter.executeUpdate();
            }

            connection.commit();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception ex) {
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (Exception ex) {
            }
        }
        return result;
    }

    public int getTotalReadings() {
        String sql = "SELECT COUNT(*) FROM DR_MeterReadings";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<MeterReading> getReadingsByPage(int offset, int limit) {
        List<MeterReading> list = new ArrayList<>();
        String sql = """
        SELECT m.*, r.RoomNumber FROM DR_MeterReadings m 
        JOIN DR_Rooms r ON m.RoomID = r.RoomID 
        ORDER BY m.ReadingYear DESC, m.ReadingMonth DESC, r.RoomNumber ASC
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new MeterReading(
                        rs.getInt("ReadingID"), rs.getInt("RoomID"), rs.getString("RoomNumber"),
                        rs.getInt("ReadingMonth"), rs.getInt("ReadingYear"),
                        rs.getInt("ElectricityIndex"), rs.getInt("WaterIndex")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isInvoicePaid(int readingId) {
        String sql = """
        SELECT i.Status FROM DR_Invoices i
        JOIN DR_Contracts c ON i.ContractID = c.ContractID
        JOIN DR_MeterReadings m ON c.RoomID = m.RoomID 
             AND i.InvoiceMonth = m.ReadingMonth 
             AND i.InvoiceYear = m.ReadingYear
        WHERE m.ReadingID = ?
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, readingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "Paid".equalsIgnoreCase(rs.getString("Status"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<MeterReading> getReadingsFiltered(Integer roomId, Integer month, Integer year, int offset, int limit) {
        List<MeterReading> list = new ArrayList<>();

        // Sử dụng OUTER APPLY (của SQL Server) để lấy chỉ số tháng trước và tự động trừ đi
        String sql = "SELECT \n"
                + "    m.ReadingID, m.RoomID, r.RoomNumber, m.ReadingMonth, m.ReadingYear,\n"
                + "    ISNULL(m.ElectricityIndex - prev.ElectricityIndex, m.ElectricityIndex) AS ElectricityIndex,\n"
                + "    ISNULL(m.WaterIndex - prev.WaterIndex, m.WaterIndex) AS WaterIndex\n"
                + "FROM DR_MeterReadings m \n"
                + "JOIN DR_Rooms r ON m.RoomID = r.RoomID \n"
                + "OUTER APPLY (\n"
                + "    SELECT TOP 1 ElectricityIndex, WaterIndex \n"
                + "    FROM DR_MeterReadings p \n"
                + "    WHERE p.RoomID = m.RoomID \n"
                + "      AND (p.ReadingYear < m.ReadingYear OR (p.ReadingYear = m.ReadingYear AND p.ReadingMonth < m.ReadingMonth))\n"
                + "    ORDER BY p.ReadingYear DESC, p.ReadingMonth DESC\n"
                + ") prev \n"
                + "WHERE (? IS NULL OR m.RoomID = ?) \n"
                + "  AND (? IS NULL OR m.ReadingMonth = ?) \n"
                + "  AND (? IS NULL OR m.ReadingYear = ?) \n"
                + "ORDER BY m.ReadingYear DESC, m.ReadingMonth DESC, r.RoomNumber ASC \n"
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, roomId);
            ps.setObject(2, roomId);
            ps.setObject(3, month);
            ps.setObject(4, month);
            ps.setObject(5, year);
            ps.setObject(6, year);
            ps.setInt(7, offset);
            ps.setInt(8, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Biến ElectricityIndex và WaterIndex lúc này ĐÃ LÀ SỐ TIÊU THỤ THỰC TẾ
                list.add(new MeterReading(
                        rs.getInt("ReadingID"), rs.getInt("RoomID"), rs.getString("RoomNumber"),
                        rs.getInt("ReadingMonth"), rs.getInt("ReadingYear"),
                        rs.getInt("ElectricityIndex"), rs.getInt("WaterIndex")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Hàm đếm tổng bản ghi có bộ lọc
    public int getTotalReadingsFiltered(Integer roomId, Integer month, Integer year) {
        String sql = "SELECT COUNT(*) FROM DR_MeterReadings WHERE "
                + "(? IS NULL OR RoomID = ?) AND (? IS NULL OR ReadingMonth = ?) AND (? IS NULL OR ReadingYear = ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, roomId);
            ps.setObject(2, roomId);
            ps.setObject(3, month);
            ps.setObject(4, month);
            ps.setObject(5, year);
            ps.setObject(6, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Hàm lấy danh sách Lịch sử (có tìm kiếm) và CHỈ HIỆN SỐ TIÊU THỤ THỰC TẾ
    public List<MeterReading> searchReadings(String keyword, int offset, int limit) {
        List<MeterReading> list = new ArrayList<>();

        // SQL sử dụng OUTER APPLY để tìm chỉ số của tháng liền trước của cùng 1 phòng
        String sql = "SELECT \n"
                + "    m.ReadingID, m.RoomID, r.RoomNumber, m.ReadingMonth, m.ReadingYear,\n"
                + "    -- Tính toán: Lấy số hiện tại trừ đi số tháng trước. Nếu không có tháng trước thì coi như dùng từ 0.\n"
                + "    CASE WHEN prev.ElectricityIndex IS NULL THEN m.ElectricityIndex \n"
                + "         ELSE (m.ElectricityIndex - prev.ElectricityIndex) END AS ElectricityUsage,\n"
                + "    CASE WHEN prev.WaterIndex IS NULL THEN m.WaterIndex \n"
                + "         ELSE (m.WaterIndex - prev.WaterIndex) END AS WaterUsage\n"
                + "FROM DR_MeterReadings m \n"
                + "JOIN DR_Rooms r ON m.RoomID = r.RoomID \n"
                + "OUTER APPLY (\n"
                + "    SELECT TOP 1 ElectricityIndex, WaterIndex \n"
                + "    FROM DR_MeterReadings p \n"
                + "    WHERE p.RoomID = m.RoomID \n"
                + "      AND (p.ReadingYear < m.ReadingYear OR (p.ReadingYear = m.ReadingYear AND p.ReadingMonth < m.ReadingMonth))\n"
                + "    ORDER BY p.ReadingYear DESC, p.ReadingMonth DESC\n"
                + ") prev \n"
                + "WHERE 1=1 ";

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += "AND r.RoomNumber LIKE ? ";
        }

        sql += "ORDER BY m.ReadingYear DESC, m.ReadingMonth DESC, r.RoomNumber ASC \n"
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword.trim() + "%");
            }
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Chúng ta gán kết quả đã trừ (Usage) vào chính thuộc tính Index của đối tượng MeterReading
                // để không phải sửa lại file JSP
                list.add(new MeterReading(
                        rs.getInt("ReadingID"), rs.getInt("RoomID"), rs.getString("RoomNumber"),
                        rs.getInt("ReadingMonth"), rs.getInt("ReadingYear"),
                        rs.getInt("ElectricityUsage"), // <--- Đây là số đã dùng
                        rs.getInt("WaterUsage") // <--- Đây là số đã dùng
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Hàm đếm tổng số bản ghi (dùng để chia trang khi đang tìm kiếm)
    public int getTotalSearchReadings(String keyword) {
        String sql = "SELECT COUNT(*) FROM DR_MeterReadings m JOIN DR_Rooms r ON m.RoomID = r.RoomID WHERE 1=1 ";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += "AND r.RoomNumber LIKE ?";
        }
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(1, "%" + keyword.trim() + "%");
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 1. Lấy Năm lớn nhất có trong dữ liệu
    public int getMaxYear() {
        String sql = "SELECT MAX(ReadingYear) FROM DR_MeterReadings";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return 0;
    }

// 2. Lấy Tháng lớn nhất của Năm lớn nhất đó
    public int getMaxMonth(int year) {
        String sql = "SELECT MAX(ReadingMonth) FROM DR_MeterReadings WHERE ReadingYear = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }

// 3. Đếm xem tháng/năm đó đã có bao nhiêu phòng được nhập chỉ số
    public int countFilledRooms(int month, int year) {
        String sql = "SELECT COUNT(DISTINCT RoomID) FROM DR_MeterReadings WHERE ReadingMonth = ? AND ReadingYear = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }

    // Kiểm tra xem phòng này đã có chỉ số của tháng/năm đó chưa
    public boolean checkReadingExists(int roomId, int month, int year) {
        String sql = "SELECT 1 FROM DR_MeterReadings WHERE RoomID = ? AND ReadingMonth = ? AND ReadingYear = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Nếu tìm thấy thì trả về true (đã tồn tại)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hàm đếm tổng số phòng ĐANG CÓ HỢP ĐỒNG (Active)
    public int getTotalActiveRoomsCount() {
        // Chúng ta đếm DISTINCT RoomID để trường hợp 1 phòng có nhiều người ở ghép 
        // thì vẫn chỉ tính là 1 phòng cần ghi điện nước.
        String sql = "SELECT COUNT(DISTINCT RoomID) FROM DR_Contracts WHERE Status = 'Active'";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

// 3. Lấy tháng/năm mới nhất đã có trong dữ liệu
    public int[] getLatestPeriodInDB() {
        String sql = "SELECT TOP 1 ReadingMonth, ReadingYear FROM DR_MeterReadings ORDER BY ReadingYear DESC, ReadingMonth DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new int[]{rs.getInt(1), rs.getInt(2)};
            }
        } catch (Exception e) {
        }
        return null;
    }

// 4. Lấy danh sách phòng kèm trạng thái Đã nhập (IsFilled)
    public List<MeterReading> getLatestReadingsForAllActiveRooms(int targetMonth, int targetYear) {
        List<MeterReading> list = new ArrayList<>();

        // Đã thay thế INNER JOIN bằng WHERE EXISTS để chống lặp dòng với các phòng ghép
        String sql = """
        SELECT r.RoomID, r.RoomNumber, 
               m.ReadingMonth as LastMonth, m.ReadingYear as LastYear,
               ISNULL(m.ElectricityIndex, 0) as ElectricityIndex,
               ISNULL(m.WaterIndex, 0) as WaterIndex,
               (SELECT COUNT(*) FROM DR_MeterReadings 
                WHERE RoomID = r.RoomID AND ReadingMonth = ? AND ReadingYear = ?) as IsFilled
        FROM DR_Rooms r
        OUTER APPLY (
            SELECT TOP 1 ReadingMonth, ReadingYear, ElectricityIndex, WaterIndex 
            FROM DR_MeterReadings 
            WHERE RoomID = r.RoomID 
            ORDER BY ReadingYear DESC, ReadingMonth DESC
        ) m
        WHERE EXISTS (
            SELECT 1 FROM DR_Contracts c 
            WHERE c.RoomID = r.RoomID AND c.Status = 'Active'
        )
        ORDER BY r.RoomNumber ASC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, targetMonth);
            ps.setInt(2, targetYear);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MeterReading mr = new MeterReading(
                        rs.getInt("IsFilled"), // Dùng ReadingID để lưu trạng thái 0/1
                        rs.getInt("RoomID"), rs.getString("RoomNumber"),
                        rs.getInt("LastMonth"), rs.getInt("LastYear"),
                        rs.getInt("ElectricityIndex"), rs.getInt("WaterIndex")
                );
                list.add(mr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
