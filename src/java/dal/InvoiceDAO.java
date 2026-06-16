package dal;

import Model.Invoice;
import Model.InvoiceItem;
import Model.Payment;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public List<Invoice> getAllInvoices() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, r.RoomNumber FROM DR_Invoices i "
                + "JOIN DR_Contracts c ON i.ContractID = c.ContractID "
                + "JOIN DR_Rooms r ON c.RoomID = r.RoomID "
                + "ORDER BY i.InvoiceYear DESC, i.InvoiceMonth DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setInvoiceId(rs.getInt("InvoiceID"));
                inv.setContractId(rs.getInt("ContractID"));
                inv.setRoomNumber(rs.getString("RoomNumber"));
                inv.setInvoiceMonth(rs.getInt("InvoiceMonth"));
                inv.setInvoiceYear(rs.getInt("InvoiceYear"));
                inv.setIssueDate(rs.getDate("IssueDate"));
                inv.setDueDate(rs.getDate("DueDate"));
                inv.setTotalAmount(rs.getDouble("TotalAmount"));
                inv.setStatus(rs.getString("Status"));
                list.add(inv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh sách hóa đơn theo UserID (Dành cho Student)
    public List<Invoice> getInvoicesByUserId(int userId) {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, r.RoomNumber FROM DR_Invoices i "
                + "JOIN DR_Contracts c ON i.ContractID = c.ContractID "
                + "JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID "
                + "JOIN DR_Students s ON cs.StudentID = s.StudentID "
                + "JOIN DR_Rooms r ON c.RoomID = r.RoomID "
                + "WHERE s.UserID = ? "
                + "ORDER BY i.InvoiceYear DESC, i.InvoiceMonth DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Invoice inv = new Invoice();
                    inv.setInvoiceId(rs.getInt("InvoiceID"));
                    inv.setContractId(rs.getInt("ContractID"));
                    inv.setRoomNumber(rs.getString("RoomNumber"));
                    inv.setInvoiceMonth(rs.getInt("InvoiceMonth"));
                    inv.setInvoiceYear(rs.getInt("InvoiceYear"));
                    inv.setIssueDate(rs.getDate("IssueDate"));
                    inv.setDueDate(rs.getDate("DueDate"));
                    inv.setTotalAmount(rs.getDouble("TotalAmount"));
                    inv.setStatus(rs.getString("Status"));
                    list.add(inv);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Xác nhận thanh toán (Chuyển Unpaid -> Paid và lưu vào DR_Payments)
    public boolean payInvoice(int invoiceId) {
        boolean result = false;
        try {
            connection.setAutoCommit(false); // Dùng transaction để đảm bảo an toàn

            // 1. Lấy tổng tiền của Hóa đơn
            double amount = 0;
            String sqlGetAmount = "SELECT TotalAmount FROM DR_Invoices WHERE InvoiceID = ?";
            try (PreparedStatement ps = connection.prepareStatement(sqlGetAmount)) {
                ps.setInt(1, invoiceId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        amount = rs.getDouble("TotalAmount");
                    }
                }
            }

            // 2. Thêm lịch sử vào bảng DR_Payments (Đã bổ sung PaymentDate kèm thời gian thực)
            String sqlPayment = "INSERT INTO DR_Payments (InvoiceID, Amount, PaymentDate, PaymentMethod) VALUES (?, ?, ?, 'Direct Payment')";
            try (PreparedStatement ps = connection.prepareStatement(sqlPayment)) {
                ps.setInt(1, invoiceId);
                ps.setDouble(2, amount);
                // Truyền thời gian hiện tại bao gồm cả ngày, giờ, phút, giây
                ps.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
            }

            // 3. Cập nhật trạng thái thành Paid trong DR_Invoices
            String sqlUpdate = "UPDATE DR_Invoices SET Status = 'Paid' WHERE InvoiceID = ?";
            try (PreparedStatement ps = connection.prepareStatement(sqlUpdate)) {
                ps.setInt(1, invoiceId);
                ps.executeUpdate();
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

    // 1. Lấy thông tin cơ bản của 1 Hóa đơn
    public Invoice getInvoiceById(int invoiceId) {
        String sql = "SELECT i.*, r.RoomNumber FROM DR_Invoices i "
                + "JOIN DR_Contracts c ON i.ContractID = c.ContractID "
                + "JOIN DR_Rooms r ON c.RoomID = r.RoomID "
                + "WHERE i.InvoiceID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Invoice inv = new Invoice();
                    inv.setInvoiceId(rs.getInt("InvoiceID"));
                    inv.setContractId(rs.getInt("ContractID"));
                    inv.setRoomNumber(rs.getString("RoomNumber"));
                    inv.setInvoiceMonth(rs.getInt("InvoiceMonth"));
                    inv.setInvoiceYear(rs.getInt("InvoiceYear"));
                    inv.setIssueDate(rs.getDate("IssueDate"));
                    inv.setDueDate(rs.getDate("DueDate"));
                    inv.setTotalAmount(rs.getDouble("TotalAmount"));
                    inv.setStatus(rs.getString("Status"));
                    return inv;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. Lấy danh sách các khoản phí (Tiền phòng, điện, nước...) của 1 hóa đơn
    public List<InvoiceItem> getInvoiceItems(int invoiceId) {
        List<InvoiceItem> list = new ArrayList<>();
        String sql = "SELECT * FROM DR_InvoiceItems WHERE InvoiceID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceItem item = new InvoiceItem();
                    item.setItemId(rs.getInt("ItemID"));
                    item.setInvoiceId(rs.getInt("InvoiceID"));
                    item.setDescription(rs.getString("Description"));
                    item.setQuantity(rs.getInt("Quantity"));
                    item.setUnitPrice(rs.getDouble("UnitPrice"));
                    item.setAmount(rs.getDouble("Amount"));
                    list.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. Kiểm tra xem Hóa đơn này có đúng là của Sinh viên đang đăng nhập không
    public boolean checkInvoiceBelongsToUser(int invoiceId, int userId) {
        String sql = "SELECT 1 FROM DR_Invoices i "
                + "JOIN DR_ContractStudents cs ON i.ContractID = cs.ContractID "
                + "JOIN DR_Students s ON cs.StudentID = s.StudentID "
                + "WHERE i.InvoiceID = ? AND s.UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. Lấy UserID của sinh viên thông qua ID Hóa đơn
    public int getUserIdByInvoiceId(int invoiceId) {
        String sql = "SELECT s.UserID FROM DR_Invoices i "
                + "JOIN DR_ContractStudents cs ON i.ContractID = cs.ContractID "
                + "JOIN DR_Students s ON cs.StudentID = s.StudentID "
                + "WHERE i.InvoiceID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UserID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 5. Lấy UserID đang thuê thông qua ID Phòng (chỉ xét hợp đồng đang Active)
    public int getUserIdByRoomId(int roomId) {
        String sql = "SELECT s.UserID FROM DR_Contracts c "
                + "JOIN DR_ContractStudents cs ON c.ContractID = cs.ContractID "
                + "JOIN DR_Students s ON cs.StudentID = s.StudentID "
                + "WHERE c.RoomID = ? AND c.Status = 'Active'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UserID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Model.MeterReading getReading(int roomId, int month, int year) {
        String sql = "SELECT * FROM DR_MeterReadings WHERE RoomID = ? AND ReadingMonth = ? AND ReadingYear = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Model.MeterReading m = new Model.MeterReading();
                    m.setReadingId(rs.getInt("ReadingID"));
                    m.setRoomId(rs.getInt("RoomID"));
                    m.setReadingMonth(rs.getInt("ReadingMonth"));
                    m.setReadingYear(rs.getInt("ReadingYear"));
                    m.setElectricityIndex(rs.getInt("ElectricityIndex"));
                    m.setWaterIndex(rs.getInt("WaterIndex"));
                    return m;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy danh sách giao dịch thanh toán (Lọc theo KỲ HÓA ĐƠN thay vì ngày thanh toán)
    public List<Payment> getPayments(Integer month, Integer year) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.PaymentID, p.InvoiceID, p.Amount, p.PaymentDate, p.PaymentMethod, "
                + "r.RoomNumber, i.InvoiceMonth, i.InvoiceYear "
                + "FROM DR_Payments p "
                + "JOIN DR_Invoices i ON p.InvoiceID = i.InvoiceID "
                + "JOIN DR_Contracts c ON i.ContractID = c.ContractID "
                + "JOIN DR_Rooms r ON c.RoomID = r.RoomID "
                + "WHERE 1=1";

        // ĐÃ SỬA: Lọc theo cột của bảng Invoice (i.InvoiceMonth/Year)
        if (month != null) {
            sql += " AND i.InvoiceMonth = ?";
        }
        if (year != null) {
            sql += " AND i.InvoiceYear = ?";
        }

        sql += " ORDER BY p.PaymentDate DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            if (month != null) {
                ps.setInt(index++, month);
            }
            if (year != null) {
                ps.setInt(index++, year);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payment p = new Payment();
                    p.setPaymentId(rs.getInt("PaymentID"));
                    p.setAmount(rs.getDouble("Amount"));
                    p.setPaymentDate(rs.getTimestamp("PaymentDate"));
                    p.setRoomNumber(rs.getString("RoomNumber"));
                    p.setMonth(rs.getInt("InvoiceMonth"));
                    p.setYear(rs.getInt("InvoiceYear"));
                    p.setPaymentMethod(rs.getString("PaymentMethod"));
                    list.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean payInvoice(int invoiceId, String paymentMethod) {
        boolean result = false;
        try {
            connection.setAutoCommit(false);
            double amount = 0;
            String sqlGetAmount = "SELECT TotalAmount FROM DR_Invoices WHERE InvoiceID = ?";
            try (PreparedStatement ps = connection.prepareStatement(sqlGetAmount)) {
                ps.setInt(1, invoiceId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        amount = rs.getDouble("TotalAmount");
                    }
                }
            }

            // Lưu phương thức thanh toán người dùng chọn
            String sqlPayment = "INSERT INTO DR_Payments (InvoiceID, Amount, PaymentDate, PaymentMethod) VALUES (?, ?, GETDATE(), ?)";
            try (PreparedStatement ps = connection.prepareStatement(sqlPayment)) {
                ps.setInt(1, invoiceId);
                ps.setDouble(2, amount);
                ps.setString(3, paymentMethod); // 'Tiền mặt' hoặc 'Chuyển khoản'
                ps.executeUpdate();
            }

            String sqlUpdate = "UPDATE DR_Invoices SET Status = 'Paid' WHERE InvoiceID = ?";
            try (PreparedStatement ps = connection.prepareStatement(sqlUpdate)) {
                ps.setInt(1, invoiceId);
                ps.executeUpdate();
            }
            connection.commit();
            result = true;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception ex) {
            }
        }
        return result;
    }

    public List<Invoice> getInvoicesFiltered(Integer roomId, Integer month, String status, int userId, int offset, int limit) {
        List<Invoice> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT i.*, r.RoomNumber FROM DR_Invoices i "
                + "JOIN DR_Contracts c ON i.ContractID = c.ContractID "
                + "JOIN DR_Rooms r ON c.RoomID = r.RoomID "
                + "WHERE 1=1 "
        );

        if (userId > 0) {
            // CHỈ LẤY RoomID của hợp đồng đang 'Active' (Đang ở)
            sql.append(" AND r.RoomID IN (SELECT RoomID FROM DR_Contracts c2 ")
                    .append(" JOIN DR_ContractStudents cs ON c2.ContractID = cs.ContractID ")
                    .append(" JOIN DR_Students s ON cs.StudentID = s.StudentID ")
                    .append(" WHERE s.UserID = ? AND c2.Status = 'Active') ");
        }

        if (roomId != null) {
            sql.append(" AND r.RoomID = ? ");
        }
        if (month != null) {
            sql.append(" AND i.InvoiceMonth = ? ");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND i.Status = ? ");
        }

        sql.append(" ORDER BY i.InvoiceYear DESC, i.InvoiceMonth DESC, i.InvoiceID DESC ");
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");

        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int paramIdx = 1;

            if (userId > 0) {
                ps.setInt(paramIdx++, userId);
            }
            if (roomId != null) {
                ps.setInt(paramIdx++, roomId);
            }
            if (month != null) {
                ps.setInt(paramIdx++, month);
            }
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIdx++, status);
            }

            ps.setInt(paramIdx++, offset);
            ps.setInt(paramIdx++, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setInvoiceId(rs.getInt("InvoiceID"));
                inv.setContractId(rs.getInt("ContractID"));
                inv.setRoomNumber(rs.getString("RoomNumber"));
                inv.setInvoiceMonth(rs.getInt("InvoiceMonth"));
                inv.setInvoiceYear(rs.getInt("InvoiceYear"));
                inv.setIssueDate(rs.getDate("IssueDate"));
                inv.setDueDate(rs.getDate("DueDate"));
                inv.setTotalAmount(rs.getDouble("TotalAmount"));
                inv.setStatus(rs.getString("Status"));
                list.add(inv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

// Đếm tổng số bản ghi để chia trang
    public int getTotalInvoicesCount(Integer roomId, Integer month, String status, int userId) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM DR_Invoices i "
                + "JOIN DR_Contracts c ON i.ContractID = c.ContractID "
                + "JOIN DR_Rooms r ON c.RoomID = r.RoomID "
                + "WHERE 1=1 "
        );

        if (userId > 0) {
            // ĐỒNG BỘ: Chỉ đếm hóa đơn của phòng đang 'Active'
            sql.append(" AND r.RoomID IN (SELECT RoomID FROM DR_Contracts c2 ")
                    .append(" JOIN DR_ContractStudents cs ON c2.ContractID = cs.ContractID ")
                    .append(" JOIN DR_Students s ON cs.StudentID = s.StudentID ")
                    .append(" WHERE s.UserID = ? AND c2.Status = 'Active') ");
        }

        if (roomId != null) {
            sql.append(" AND r.RoomID = ? ");
        }
        if (month != null) {
            sql.append(" AND i.InvoiceMonth = ? ");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND i.Status = ? ");
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int paramIdx = 1;

            if (userId > 0) {
                ps.setInt(paramIdx++, userId);
            }
            if (roomId != null) {
                ps.setInt(paramIdx++, roomId);
            }
            if (month != null) {
                ps.setInt(paramIdx++, month);
            }
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIdx++, status);
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

    // Lấy tổng doanh thu (Có thể lọc theo phương thức)
    public double getTotalRevenue(String method) {
        double total = 0;
        String sql = "SELECT SUM(Amount) FROM DR_Payments";
        if (method != null && !method.isEmpty()) {
            sql += " WHERE PaymentMethod = ?";
        }
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (method != null && !method.isEmpty()) {
                ps.setString(1, method);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    // Lấy tổng doanh thu (ĐÃ SỬA: Lọc theo KỲ HÓA ĐƠN i.InvoiceMonth/Year)
    public double getTotalRevenue(String method, Integer month, Integer year) {
        double total = 0;
        // Cần JOIN với bảng DR_Invoices để lấy được tháng/năm của kỳ hóa đơn
        String sql = "SELECT SUM(p.Amount) FROM DR_Payments p "
                + "JOIN DR_Invoices i ON p.InvoiceID = i.InvoiceID WHERE 1=1";

        if (method != null && !method.isEmpty()) {
            sql += " AND p.PaymentMethod = ?";
        }
        if (month != null) {
            sql += " AND i.InvoiceMonth = ?";
        }
        if (year != null) {
            sql += " AND i.InvoiceYear = ?";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            if (method != null && !method.isEmpty()) {
                ps.setString(index++, method);
            }
            if (month != null) {
                ps.setInt(index++, month);
            }
            if (year != null) {
                ps.setInt(index++, year);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
    
    // 1. Lấy lịch sử thanh toán (CÓ PHÂN TRANG: OFFSET & LIMIT)
    public List<Payment> getPaymentsByUserId(int userId, Integer month, Integer year, int offset, int limit) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.PaymentID, p.InvoiceID, p.Amount, p.PaymentDate, p.PaymentMethod, "
                   + "r.RoomNumber, i.InvoiceMonth, i.InvoiceYear "
                   + "FROM DR_Payments p "
                   + "JOIN DR_Invoices i ON p.InvoiceID = i.InvoiceID "
                   + "JOIN DR_Contracts c ON i.ContractID = c.ContractID "
                   + "JOIN DR_Rooms r ON c.RoomID = r.RoomID "
                   + "WHERE r.RoomID IN ("
                   + "    SELECT c2.RoomID FROM DR_Contracts c2 "
                   + "    JOIN DR_ContractStudents cs ON c2.ContractID = cs.ContractID "
                   + "    JOIN DR_Students s ON cs.StudentID = s.StudentID "
                   + "    WHERE s.UserID = ? AND c2.Status = 'Active'"
                   + ") ";

        if (month != null) sql += " AND i.InvoiceMonth = ? ";
        if (year != null) sql += " AND i.InvoiceYear = ? ";

        sql += " ORDER BY p.PaymentDate DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            ps.setInt(index++, userId);
            if (month != null) ps.setInt(index++, month);
            if (year != null) ps.setInt(index++, year);
            ps.setInt(index++, offset);
            ps.setInt(index++, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payment p = new Payment();
                    p.setPaymentId(rs.getInt("PaymentID"));
                    p.setInvoiceId(rs.getInt("InvoiceID"));
                    p.setAmount(rs.getDouble("Amount"));
                    p.setPaymentDate(rs.getTimestamp("PaymentDate"));
                    p.setPaymentMethod(rs.getString("PaymentMethod"));
                    p.setRoomNumber(rs.getString("RoomNumber"));
                    p.setMonth(rs.getInt("InvoiceMonth"));
                    p.setYear(rs.getInt("InvoiceYear"));
                    list.add(p);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. Đếm tổng số giao dịch để chia trang
    public int getTotalPaymentsCountByUserId(int userId, Integer month, Integer year) {
        String sql = "SELECT COUNT(*) FROM DR_Payments p "
                   + "JOIN DR_Invoices i ON p.InvoiceID = i.InvoiceID "
                   + "JOIN DR_Contracts c ON i.ContractID = c.ContractID "
                   + "JOIN DR_Rooms r ON c.RoomID = r.RoomID "
                   + "WHERE r.RoomID IN ("
                   + "    SELECT c2.RoomID FROM DR_Contracts c2 "
                   + "    JOIN DR_ContractStudents cs ON c2.ContractID = cs.ContractID "
                   + "    JOIN DR_Students s ON cs.StudentID = s.StudentID "
                   + "    WHERE s.UserID = ? AND c2.Status = 'Active'"
                   + ") ";
        if (month != null) sql += " AND i.InvoiceMonth = ? ";
        if (year != null) sql += " AND i.InvoiceYear = ? ";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            ps.setInt(index++, userId);
            if (month != null) ps.setInt(index++, month);
            if (year != null) ps.setInt(index++, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // 3. Tính tổng tiền đã thanh toán (Tính toán trên Database để không bị sai khi sang trang 2, 3...)
    public double getTotalPaidAmountByUserId(int userId, Integer month, Integer year) {
        String sql = "SELECT SUM(p.Amount) FROM DR_Payments p "
                   + "JOIN DR_Invoices i ON p.InvoiceID = i.InvoiceID "
                   + "JOIN DR_Contracts c ON i.ContractID = c.ContractID "
                   + "JOIN DR_Rooms r ON c.RoomID = r.RoomID "
                   + "WHERE r.RoomID IN ("
                   + "    SELECT c2.RoomID FROM DR_Contracts c2 "
                   + "    JOIN DR_ContractStudents cs ON c2.ContractID = cs.ContractID "
                   + "    JOIN DR_Students s ON cs.StudentID = s.StudentID "
                   + "    WHERE s.UserID = ? AND c2.Status = 'Active'"
                   + ") ";
        if (month != null) sql += " AND i.InvoiceMonth = ? ";
        if (year != null) sql += " AND i.InvoiceYear = ? ";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            ps.setInt(index++, userId);
            if (month != null) ps.setInt(index++, month);
            if (year != null) ps.setInt(index++, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}
