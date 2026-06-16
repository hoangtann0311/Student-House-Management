package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class DashboardDAO extends DBContext {

    public int getTotalRooms() {
        try (PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM DR_Rooms"); 
             ResultSet rs = ps.executeQuery()) {
            if(rs.next()) return rs.getInt(1);
        } catch(Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getAvailableRooms() {
        try (PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM DR_Rooms WHERE Status = N'Trống'"); 
             ResultSet rs = ps.executeQuery()) {
            if(rs.next()) return rs.getInt(1);
        } catch(Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getTotalStudents() {
        try (PreparedStatement ps = connection.prepareStatement("SELECT COUNT(DISTINCT StudentID) FROM DR_ContractStudents cs JOIN DR_Contracts c ON cs.ContractID = c.ContractID WHERE c.Status = 'Active'"); 
             ResultSet rs = ps.executeQuery()) {
            if(rs.next()) return rs.getInt(1);
        } catch(Exception e) { e.printStackTrace(); }
        return 0;
    }

    public double getCurrentMonthRevenue() {
        LocalDate now = LocalDate.now();
        String sql = "SELECT SUM(Amount) FROM DR_Payments WHERE MONTH(PaymentDate) = ? AND YEAR(PaymentDate) = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, now.getMonthValue()); 
            ps.setInt(2, now.getYear());
            try(ResultSet rs = ps.executeQuery()) { 
                if(rs.next()) return rs.getDouble(1); 
            }
        } catch(Exception e) { e.printStackTrace(); }
        return 0;
    }
    
    public double getMonthAvailablePaid() {
        LocalDate now = LocalDate.now();
        String sql = "SELECT SUM(Amount) FROM DR_Payments WHERE MONTH(PaymentDate) = ? AND YEAR(PaymentDate) = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, now.getMonthValue()); 
            ps.setInt(2, now.getYear());
            try(ResultSet rs = ps.executeQuery()) { 
                if(rs.next()) return rs.getDouble(1); 
            }
        } catch(Exception e) { e.printStackTrace(); }
        return 0;
    }
    
    public int getUnpaidInvoices() {
        try (PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM DR_Invoices WHERE Status = 'Unpaid'"); 
             ResultSet rs = ps.executeQuery()) {
            if(rs.next()) return rs.getInt(1);
        } catch(Exception e) { e.printStackTrace(); }
        return 0;
    }
}