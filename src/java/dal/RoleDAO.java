package dal;

import Model.Roles;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RoleDAO extends DBContext {

    public List<Roles> getRoles() {
        List<Roles> roles = new ArrayList<>();
        String sql = "SELECT * FROM DR_Roles";
        // Sử dụng try-with-resources để tự động đóng kết nối
        try (PreparedStatement st = connection.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                int roleId = rs.getInt("RoleID");
                String roleName = rs.getString("RoleName");
                Roles role = new Roles(roleId, roleName);
                roles.add(role);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roles;
    }
}