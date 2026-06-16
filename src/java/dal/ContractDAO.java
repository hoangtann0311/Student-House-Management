package dal;

import Model.Contract;
import Model.Student;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ContractDAO extends DBContext {

    public List<Contract> findActiveContractsByStudentId(int studentId) {
        String sql = """
            SELECT
                c.ContractID, c.RoomID, c.StartDate, c.EndDate, c.DepositAmount, c.Status,
                r.RoomNumber, s.FullName AS TenantName
            FROM DR_ContractStudents cs
            JOIN DR_Contracts c ON c.ContractID = cs.ContractID
            JOIN DR_Rooms r ON c.RoomID = r.RoomID
            JOIN DR_Students s ON cs.StudentID = s.StudentID
            WHERE cs.StudentID = ?
              AND c.Status = 'Active'
            ORDER BY c.StartDate DESC, c.ContractID DESC
        """;

        List<Contract> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contract c = new Contract();
                    c.setContractID(rs.getInt("ContractID"));
                    c.setRoomID(rs.getInt("RoomID"));
                    c.setStartDate(rs.getDate("StartDate"));
                    c.setEndDate(rs.getDate("EndDate"));
                    c.setDepositAmount(rs.getBigDecimal("DepositAmount"));
                    c.setStatus(rs.getString("Status"));
                    c.setRoomNumber(rs.getString("RoomNumber"));
                    c.setTenantName(rs.getString("TenantName"));
                    list.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean createContractForExistingStudent(int userId, int roomId, int months, double depositAmount, double electricPrice, double waterPrice, double wifiPrice, double garbagePrice) {
        try {
            connection.setAutoCommit(false); 

            // 1. Tìm StudentID
            int studentId = -1;
            String checkSql = "SELECT StudentID FROM DR_Students WHERE UserID = ?";
            PreparedStatement psCheck = connection.prepareStatement(checkSql);
            psCheck.setInt(1, userId);
            ResultSet rsCheck = psCheck.executeQuery();
            if (rsCheck.next()) {
                studentId = rsCheck.getInt("StudentID");
            } else {
                return false;
            }

            // 2. KIỂM TRA PHÒNG ĐÃ CÓ HỢP ĐỒNG NÀO ACTIVE CHƯA?
            int existingContractId = -1;
            String checkRoomSql = "SELECT ContractID FROM DR_Contracts WHERE RoomID = ? AND Status = 'Active'";
            PreparedStatement psCheckRoom = connection.prepareStatement(checkRoomSql);
            psCheckRoom.setInt(1, roomId);
            ResultSet rsRoom = psCheckRoom.executeQuery();
            if (rsRoom.next()) {
                existingContractId = rsRoom.getInt("ContractID");
            }

            int targetContractId = 0;

            if (existingContractId != -1) {
                // --- KỊCH BẢN Ở GHÉP ---
                targetContractId = existingContractId;
                
                // Nếu Admin có nhập tiền cọc của người mới, thì cộng dồn vào tổng cọc của phòng
                if (depositAmount > 0) {
                    String updateDeposit = "UPDATE DR_Contracts SET DepositAmount = ISNULL(DepositAmount, 0) + ? WHERE ContractID = ?";
                    PreparedStatement psUpdateDep = connection.prepareStatement(updateDeposit);
                    psUpdateDep.setDouble(1, depositAmount);
                    psUpdateDep.setInt(2, targetContractId);
                    psUpdateDep.executeUpdate();
                }
            } else {
                // --- KỊCH BẢN PHÒNG TRỐNG ---
                String insertContract = """
                INSERT INTO DR_Contracts (RoomID, StartDate, EndDate, DepositAmount, Status, ElectricPrice, WaterPrice, WifiPrice, GarbagePrice) 
                VALUES (?, GETDATE(), DATEADD(month, ?, GETDATE()), ?, 'Active', ?, ?, ?, ?)
                """;
                PreparedStatement psContract = connection.prepareStatement(insertContract, PreparedStatement.RETURN_GENERATED_KEYS);
                psContract.setInt(1, roomId);
                psContract.setInt(2, months);
                psContract.setDouble(3, depositAmount);
                psContract.setDouble(4, electricPrice);
                psContract.setDouble(5, waterPrice);
                psContract.setDouble(6, wifiPrice);
                psContract.setDouble(7, garbagePrice);
                psContract.executeUpdate();

                ResultSet rsKeys = psContract.getGeneratedKeys();
                if (rsKeys.next()) {
                    targetContractId = rsKeys.getInt(1);
                }
            }

            // 3. Chèn sinh viên vào bảng cầu nối DR_ContractStudents
            String insertCS = "INSERT INTO DR_ContractStudents (ContractID, StudentID) VALUES (?, ?)";
            PreparedStatement psCS = connection.prepareStatement(insertCS);
            psCS.setInt(1, targetContractId);
            psCS.setInt(2, studentId);
            psCS.executeUpdate();

            // 4. Cập nhật trạng thái phòng
            String updateRoomSql = "UPDATE DR_Rooms SET Status = N'Đang thuê' WHERE RoomID = ?";
            PreparedStatement psUpdateRoom = connection.prepareStatement(updateRoomSql);
            psUpdateRoom.setInt(1, roomId);
            psUpdateRoom.executeUpdate();

            connection.commit(); 
            return true;

        } catch (Exception e) {
            try { connection.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
        } finally {
            try { connection.setAutoCommit(true); } catch (Exception ex) {}
        }
        return false;
    }

    public double[] getActiveContractPricesByRoom(int roomId) {
        String sql = "SELECT TOP 1 ElectricPrice, WaterPrice, WifiPrice, GarbagePrice FROM DR_Contracts WHERE RoomID = ? AND Status = 'Active' ORDER BY ContractID DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new double[]{
                    rs.getDouble("ElectricPrice"),
                    rs.getDouble("WaterPrice"),
                    rs.getDouble("WifiPrice"),
                    rs.getDouble("GarbagePrice")
                };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean transferContract(int contractId, int oldUserId, int newUserId) {
        String sql = """
        UPDATE DR_ContractStudents 
        SET StudentID = (SELECT StudentID FROM DR_Students WHERE UserID = ?)
        WHERE ContractID = ? 
        AND StudentID = (SELECT StudentID FROM DR_Students WHERE UserID = ?)
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newUserId);
            ps.setInt(2, contractId);
            ps.setInt(3, oldUserId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean terminateContract(int contractId, int roomId, boolean isEarly) {
        try {
            connection.setAutoCommit(false); 

            String statusHĐ = isEarly ? "Broken" : "Expired";
            String sqlHĐ = "UPDATE DR_Contracts SET Status = ? WHERE ContractID = ?";
            try (PreparedStatement ps1 = connection.prepareStatement(sqlHĐ)) {
                ps1.setString(1, statusHĐ);
                ps1.setInt(2, contractId);
                ps1.executeUpdate();
            }

            int remainingOccupants = 0;
            String sqlCheckCount = """
            SELECT COUNT(*) FROM DR_ContractStudents cs 
            JOIN DR_Contracts c ON cs.ContractID = c.ContractID 
            WHERE c.RoomID = ? AND c.Status = 'Active'
            """;
            try (PreparedStatement psCount = connection.prepareStatement(sqlCheckCount)) {
                psCount.setInt(1, roomId);
                try (ResultSet rs = psCount.executeQuery()) {
                    if (rs.next()) {
                        remainingOccupants = rs.getInt(1);
                    }
                }
            }

            String newRoomStatus = (remainingOccupants == 0) ? "Trống" : "Đang thuê";
            String sqlPhòng = "UPDATE DR_Rooms SET Status = ? WHERE RoomID = ?";
            try (PreparedStatement ps2 = connection.prepareStatement(sqlPhòng)) {
                ps2.setNString(1, newRoomStatus);
                ps2.setInt(2, roomId);
                ps2.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            try { connection.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
        } finally {
            try { connection.setAutoCommit(true); } catch (Exception ex) {}
        }
        return false;
    }

    public Contract getContractById(int contractId) {
        String sql = """
        SELECT c.*, r.RoomNumber, s.FullName, s.UserID as TenantUserID
        FROM DR_Contracts c
        JOIN DR_Rooms r ON c.RoomID = r.RoomID
        JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID
        JOIN DR_Students s ON cs.StudentID = s.StudentID
        WHERE c.ContractID = ?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract c = new Contract();
                    c.setContractID(rs.getInt("ContractID"));
                    c.setRoomID(rs.getInt("RoomID"));
                    c.setRoomNumber(rs.getString("RoomNumber"));
                    c.setTenantName(rs.getString("FullName"));
                    c.setTenantUserId(rs.getInt("TenantUserID"));
                    c.setStartDate(rs.getDate("StartDate"));
                    c.setEndDate(rs.getDate("EndDate"));
                    c.setDepositAmount(rs.getBigDecimal("DepositAmount"));
                    c.setStatus(rs.getString("Status"));
                    c.setElectricPrice(rs.getDouble("ElectricPrice"));
                    c.setWaterPrice(rs.getDouble("WaterPrice"));
                    c.setWifiPrice(rs.getDouble("WifiPrice"));
                    c.setGarbagePrice(rs.getDouble("GarbagePrice"));
                    return c;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getTotalContracts() {
        String sql = "SELECT COUNT(*) FROM DR_Contracts WHERE Status = 'Active'";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public List<Contract> getContractsByPage(int offset, int limit) {
        List<Contract> list = new ArrayList<>();
        String sql = """
        SELECT c.*, r.RoomNumber, s.FullName, s.UserID as TenantUserID
        FROM DR_Contracts c
        JOIN DR_Rooms r ON c.RoomID = r.RoomID
        JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID
        JOIN DR_Students s ON cs.StudentID = s.StudentID
        WHERE c.Status = 'Active' 
        ORDER BY c.ContractID ASC
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Contract c = new Contract();
                c.setContractID(rs.getInt("ContractID"));
                c.setRoomNumber(rs.getString("RoomNumber"));
                c.setTenantName(rs.getString("FullName"));
                c.setTenantUserId(rs.getInt("TenantUserID"));
                c.setStartDate(rs.getDate("StartDate"));
                c.setEndDate(rs.getDate("EndDate"));
                c.setDepositAmount(rs.getBigDecimal("DepositAmount"));
                c.setStatus(rs.getString("Status"));
                c.setRoomID(rs.getInt("RoomID"));
                list.add(c);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Contract> searchContracts(String keyword, int offset, int limit) {
        List<Contract> list = new ArrayList<>();
        // Dùng STRING_AGG để gom tên, MIN(s.UserID) để giữ lại 1 ID tạm cho nút Đổi người
        String sql = """
            SELECT c.ContractID, c.RoomID, c.StartDate, c.EndDate, c.DepositAmount, c.Status,
                   r.RoomNumber,
                   STRING_AGG(s.FullName, ', ') AS FullName, 
                   MIN(s.UserID) as TenantUserID
            FROM DR_Contracts c
            JOIN DR_Rooms r ON c.RoomID = r.RoomID
            JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID
            JOIN DR_Students s ON cs.StudentID = s.StudentID
            WHERE c.Status = 'Active' 
        """;

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += "AND (r.RoomNumber LIKE ? OR s.FullName LIKE ?) ";
        }
        
        // Bắt buộc phải có GROUP BY khi dùng STRING_AGG
        sql += """
            GROUP BY c.ContractID, c.RoomID, c.StartDate, c.EndDate, c.DepositAmount, c.Status, r.RoomNumber
            ORDER BY c.ContractID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Contract c = new Contract();
                c.setContractID(rs.getInt("ContractID"));
                c.setRoomNumber(rs.getString("RoomNumber"));
                
                // Cột FullName giờ đã là 1 chuỗi dài gom các tên
                c.setTenantName(rs.getString("FullName")); 
                c.setTenantUserId(rs.getInt("TenantUserID"));
                c.setStartDate(rs.getDate("StartDate"));
                c.setEndDate(rs.getDate("EndDate"));
                c.setDepositAmount(rs.getBigDecimal("DepositAmount"));
                c.setStatus(rs.getString("Status"));
                c.setRoomID(rs.getInt("RoomID"));
                list.add(c);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getTotalSearchContracts(String keyword) {
        String sql = """
            SELECT COUNT(*) 
            FROM DR_Contracts c
            JOIN DR_Rooms r ON c.RoomID = r.RoomID
            JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID
            JOIN DR_Students s ON cs.StudentID = s.StudentID
            WHERE c.Status = 'Active' 
        """;

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += "AND (r.RoomNumber LIKE ? OR s.FullName LIKE ?)";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim() + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    
    // Lấy danh sách những người đang chung 1 hợp đồng
    public List<Student> getStudentsInContract(int contractId) {
        List<Student> list = new ArrayList<>();
        String sql = """
            SELECT s.UserID, s.FullName, s.StudentCode
            FROM DR_ContractStudents cs
            JOIN DR_Students s ON cs.StudentID = s.StudentID
            WHERE cs.ContractID = ?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Student s = new Student();
                s.setUserId(rs.getInt("UserID"));
                s.setFullName(rs.getString("FullName"));
                s.setStudentCode(rs.getString("StudentCode"));
                list.add(s);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}