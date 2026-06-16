package dal;

import Model.Student;
import Model.StudentUser;
import Model.User;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 

public class UserDAO extends DBContext {

    public User checkLogin(String username, String password) {
        try {
            String sql = "SELECT * FROM DR_Users WHERE Username = ? AND Password = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, username);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return new User(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"), rs.getInt("RoleID"), rs.getInt("IsActive"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public StudentUser getUsersById(String id) {
        try {
            String sql = "SELECT * FROM DR_Users u LEFT JOIN DR_Students s ON u.UserID = s.UserID WHERE u.UserID = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new StudentUser(
                        rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"),
                        rs.getInt("RoleID"), rs.getInt("IsActive"),
                        rs.getInt("StudentID"), rs.getString("FullName"), rs.getDate("DOB"),
                        rs.getString("Gender"), rs.getString("Phone"), rs.getString("StudentCode"),
                        rs.getString("SecurityQuestion"), rs.getString("SecurityAnswer")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String deleteAccountWithCheck(String userIdStr) {
        try {
            int userId = Integer.parseInt(userIdStr);
            if (isRentingRoom(userId)) {
                return "KHÔNG THỂ XÓA: Tài khoản này đang có hợp đồng thuê phòng còn hiệu lực. Vui lòng thanh lý hợp đồng trước!";
            }

            connection.setAutoCommit(false); 

            int studentId = -1;
            String findStudentSql = "SELECT StudentID FROM DR_Students WHERE UserID = ?";
            try (PreparedStatement psFind = connection.prepareStatement(findStudentSql)) {
                psFind.setInt(1, userId);
                try (ResultSet rs = psFind.executeQuery()) {
                    if (rs.next()) studentId = rs.getInt("StudentID");
                }
            }

            if (studentId != -1) {
                String delContractStudentSql = "DELETE FROM DR_ContractStudents WHERE StudentID = ?";
                try (PreparedStatement psCS = connection.prepareStatement(delContractStudentSql)) {
                    psCS.setInt(1, studentId);
                    psCS.executeUpdate();
                }

                String delStudentSql = "DELETE FROM DR_Students WHERE StudentID = ?";
                try (PreparedStatement psS = connection.prepareStatement(delStudentSql)) {
                    psS.setInt(1, studentId);
                    psS.executeUpdate();
                }
            }

            String delUserSql = "DELETE FROM DR_Users WHERE UserID = ?";
            try (PreparedStatement psU = connection.prepareStatement(delUserSql)) {
                psU.setInt(1, userId);
                psU.executeUpdate();
            }

            connection.commit();
            return "SUCCESS";

        } catch (Exception e) {
            try { if (connection != null) connection.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return "LỖI HỆ THỐNG: " + e.getMessage();
        } finally {
            try { if (connection != null) connection.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM DR_Users WHERE Username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean register(String username, String password, int roleId, String q, String a) {
        String sql = "INSERT INTO DR_Users (Username, Password, RoleID, IsActive, SecurityQuestion, SecurityAnswer) VALUES (?, ?, ?, 1, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, roleId);
            ps.setString(4, q);
            ps.setString(5, a);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Đã gộp hàm registerWithStudent để chỉ giữ lại 1 bản chuẩn nhất
    public boolean registerWithStudent(String username, String password, int roleId, String q, String a, String fullName, java.sql.Date dob, String gender, String phone, String studentCode) {
        try {
            connection.setAutoCommit(false);
            String sqlUser = "INSERT INTO DR_Users (Username, Password, RoleID, IsActive, SecurityQuestion, SecurityAnswer) VALUES (?, ?, ?, 1, ?, ?)";
            PreparedStatement psUser = connection.prepareStatement(sqlUser, PreparedStatement.RETURN_GENERATED_KEYS);
            psUser.setString(1, username);
            psUser.setString(2, password);
            psUser.setInt(3, roleId);
            psUser.setString(4, q);
            psUser.setString(5, a);
            psUser.executeUpdate();

            ResultSet rs = psUser.getGeneratedKeys();
            int newId = rs.next() ? rs.getInt(1) : -1;

            if (roleId == 2 && newId != -1) {
                String sqlS = "INSERT INTO DR_Students (UserID, FullName, DOB, Gender, Phone, StudentCode) VALUES (?,?,?,?,?,?)";
                PreparedStatement psS = connection.prepareStatement(sqlS);
                psS.setInt(1, newId);
                psS.setString(2, fullName);
                psS.setDate(3, dob);
                psS.setString(4, gender);
                psS.setString(5, phone);
                psS.setString(6, studentCode);
                psS.executeUpdate();
            }
            connection.commit();
            return true;
        } catch (Exception e) {
            try { connection.rollback(); } catch (Exception ex) { }
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (Exception ex) { }
        }
    }

    public StudentUser getStudentDetailByUserId(String id) {
        return getUsersById(id); // Dùng chung logic với getUsersById cho gọn
    }

    public boolean updateFullUser(StudentUser u) {
        try {
            connection.setAutoCommit(false);

            String sqlUser = "UPDATE DR_Users SET Password = ?, RoleID = ?, IsActive = ?, SecurityQuestion = ?, SecurityAnswer = ? WHERE UserID = ?";
            PreparedStatement psUser = connection.prepareStatement(sqlUser);
            psUser.setString(1, u.getPassword());
            psUser.setInt(2, u.getRoleId());
            psUser.setInt(3, u.getIsActive());
            psUser.setString(4, u.getSecurityQuestion()); 
            psUser.setString(5, u.getSecurityAnswer());   
            psUser.setInt(6, u.getUserId());
            psUser.executeUpdate();

            if (u.getRoleId() == 2) {
                String checkSql = "SELECT 1 FROM DR_Students WHERE UserID = ?";
                PreparedStatement checkSt = connection.prepareStatement(checkSql);
                checkSt.setInt(1, u.getUserId());
                ResultSet checkRs = checkSt.executeQuery();

                if (checkRs.next()) {
                    String sqlUpdate = "UPDATE DR_Students SET FullName = ?, DOB = ?, Gender = ?, Phone = ?, StudentCode = ? WHERE UserID = ?";
                    PreparedStatement ps2 = connection.prepareStatement(sqlUpdate);
                    ps2.setString(1, u.getFullName());
                    ps2.setDate(2, u.getDob());
                    ps2.setString(3, u.getGender());
                    ps2.setString(4, u.getPhone());
                    ps2.setString(5, u.getStudentCode());
                    ps2.setInt(6, u.getUserId());
                    ps2.executeUpdate();
                } else {
                    String sqlInsert = "INSERT INTO DR_Students (FullName, DOB, Gender, Phone, StudentCode, UserID) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement ps3 = connection.prepareStatement(sqlInsert);
                    ps3.setString(1, u.getFullName());
                    ps3.setDate(2, u.getDob());
                    ps3.setString(3, u.getGender());
                    ps3.setString(4, u.getPhone());
                    ps3.setString(5, u.getStudentCode());
                    ps3.setInt(6, u.getUserId());
                    ps3.executeUpdate();
                }
            } else {
                String deleteSql = "DELETE FROM DR_Students WHERE UserID = ?";
                PreparedStatement psDel = connection.prepareStatement(deleteSql);
                psDel.setInt(1, u.getUserId());
                psDel.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            try { if (connection != null) connection.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (connection != null) connection.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    public boolean isRentingRoom(int userId) {
        String sql = "SELECT 1 FROM DR_Students s JOIN DR_ContractStudents cs ON s.StudentID = cs.StudentID JOIN DR_Contracts c ON cs.ContractID = c.ContractID WHERE s.UserID = ? AND c.Status = 'Active'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Model.Student> getStudentsNotRenting() {
        List<Model.Student> list = new ArrayList<>();
        String sql = "SELECT u.UserID, u.Username, s.FullName, s.StudentCode FROM DR_Users u JOIN DR_Students s ON u.UserID = s.UserID WHERE u.RoleID = 2 AND u.UserID NOT IN (SELECT s2.UserID FROM DR_Students s2 JOIN DR_ContractStudents cs ON s2.StudentID = cs.StudentID JOIN DR_Contracts c ON cs.ContractID = c.ContractID WHERE c.Status = 'Active')";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Model.Student s = new Model.Student();
                s.setUserId(rs.getInt("UserID"));
                s.setUsername(rs.getString("Username")); 
                s.setFullName(rs.getString("FullName")); 
                s.setStudentCode(rs.getString("StudentCode")); 
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<Student> getAvailableStudentsForTransfer() {
        return getStudentsNotRenting(); // Hàm này logic y chang hàm getStudentsNotRenting, gọi chung cho gọn
    }

    public List<StudentUser> getStudentsByRoomId(int roomId) {
        List<StudentUser> list = new ArrayList<>();
        String sql = "SELECT u.*, s.* FROM DR_ContractStudents cs JOIN DR_Contracts c ON cs.ContractID = c.ContractID JOIN DR_Students s ON cs.StudentID = s.StudentID JOIN DR_Users u ON s.UserID = u.UserID WHERE c.RoomID = ? AND c.Status = 'Active'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StudentUser su = new StudentUser(
                        rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"),
                        rs.getInt("RoleID"), rs.getInt("IsActive"), rs.getInt("StudentID"),
                        rs.getString("FullName"), rs.getDate("DOB"), rs.getString("Gender"),
                        rs.getString("Phone"), rs.getString("StudentCode"),
                        rs.getString("SecurityQuestion"), rs.getString("SecurityAnswer")
                );
                su.setUsername(rs.getString("Username")); 
                list.add(su);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM DR_Users";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public List<User> getUsersByPage(int offset, int limit) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, s.FullName, (SELECT COUNT(*) FROM DR_Contracts c JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID JOIN DR_Students s2 ON cs.StudentID = s2.StudentID WHERE s2.UserID = u.UserID AND c.Status = 'Active') as IsRenting FROM DR_Users u LEFT JOIN DR_Students s ON u.UserID = s.UserID ORDER BY u.UserID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"), rs.getInt("RoleID"), rs.getInt("IsActive"));
                u.setRenting(rs.getInt("IsRenting") > 0);
                u.setFullName(rs.getString("FullName")); 
                list.add(u);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getTotalUsersBySearch(String searchText) {
        String sql = "SELECT COUNT(*) FROM DR_Users WHERE Username LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + searchText + "%");
            try(ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public List<User> getUsersBySearchAndPage(String searchText, int offset, int limit) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, (SELECT COUNT(*) FROM DR_Contracts c JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID JOIN DR_Students s ON cs.StudentID = s.StudentID WHERE s.UserID = u.UserID AND c.Status = 'Active') as IsRenting FROM DR_Users u WHERE u.Username LIKE ? ORDER BY u.UserID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + searchText + "%");
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"), rs.getInt("RoleID"), rs.getInt("IsActive"));
                u.setRenting(rs.getInt("IsRenting") > 0);
                list.add(u);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getTotalUsersByRole(int roleIdSearch) {
        String sql = "SELECT COUNT(*) FROM DR_Users WHERE RoleID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleIdSearch);
            try(ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public List<User> getUsersByRoleAndPage(int roleIdSearch, int offset, int limit) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, s.FullName, (SELECT COUNT(*) FROM DR_Contracts c JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID JOIN DR_Students s2 ON cs.StudentID = s2.StudentID WHERE s2.UserID = u.UserID AND c.Status = 'Active') as IsRenting FROM DR_Users u LEFT JOIN DR_Students s ON u.UserID = s.UserID WHERE u.RoleID = ? ORDER BY u.UserID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleIdSearch);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"), rs.getInt("RoleID"), rs.getInt("IsActive"));
                u.setRenting(rs.getInt("IsRenting") > 0);
                u.setFullName(rs.getString("FullName"));
                list.add(u);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public String getSecurityQuestionByUsername(String username) {
        String sql = "SELECT SecurityQuestion FROM DR_Users WHERE Username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try(ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getString("SecurityQuestion"); }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean verifySecurityAnswer(String username, String answer) {
        String sql = "SELECT SecurityAnswer FROM DR_Users WHERE Username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String dbAnswer = rs.getString("SecurityAnswer");
                    return dbAnswer != null && dbAnswer.trim().equalsIgnoreCase(answer.trim());
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public void updatePassword(String username, String newPass) {
        String sql = "UPDATE DR_Users SET Password = ? WHERE Username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newPass);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public String getPasswordByUsername(String username) {
        String sql = "SELECT Password FROM DR_Users WHERE Username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try(ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getString("Password"); }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}