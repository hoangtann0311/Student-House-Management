package Controller;

import Model.Roles;
import Model.StudentUser;
import dal.RoleDAO;
import dal.UserDAO;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.sql.Date;

public class EditUserController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        RoleDAO roleDao = new RoleDAO();
        List<Roles> roles = roleDao.getRoles();
        request.setAttribute("roles", roles);

        UserDAO udao = new UserDAO();
        StudentUser editUser = udao.getUsersById(id);

        request.setAttribute("editUser", editUser);
        RequestDispatcher rd = request.getRequestDispatcher("View/EditUser.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setCharacterEncoding("UTF-8");

            int userId = Integer.parseInt(request.getParameter("UserID"));
            String username = request.getParameter("Username");
            String password = request.getParameter("Password");
            int roleId = Integer.parseInt(request.getParameter("RoleID"));
            int isActive = Integer.parseInt(request.getParameter("IsActive"));

            String q = request.getParameter("SecurityQuestion");
            if (q == null) q = request.getParameter("securityQuestion");

            String a = request.getParameter("SecurityAnswer");
            if (a == null) a = request.getParameter("securityAnswer");

            String fullName = request.getParameter("FullName");
            String dobStr = request.getParameter("DOB");
            String gender = request.getParameter("Gender");
            String phone = request.getParameter("Phone");
            
            String studentCode = request.getParameter("CCCD");
            if (studentCode == null) studentCode = request.getParameter("StudentCode");

            Date dob = null;
            if (dobStr != null && !dobStr.trim().isEmpty()) {
                dob = Date.valueOf(dobStr.trim());
            }

            UserDAO dao = new UserDAO();
            
            // Lấy role cũ trong DB để kiểm tra
            StudentUser oldData = dao.getUsersById(String.valueOf(userId));
            if (oldData != null && oldData.getRoleId() == 2 && roleId == 1) {
                if (dao.isRentingRoom(userId)) {
                    request.getSession().setAttribute("errorMsg", "Không thể chuyển Sinh viên này thành Admin vì họ đang có hợp đồng thuê phòng!");
                    response.sendRedirect("EditUser?id=" + userId);
                    return;
                }
            }

            StudentUser su = new StudentUser(userId, username, password, roleId, isActive, 
                    0, fullName, dob, gender, phone, studentCode, q, a);

            boolean ok = dao.updateFullUser(su);

            if (ok) {
                request.getSession().setAttribute("successMsg", "Cập nhật tài khoản thành công!");
                response.sendRedirect("User");
            } else {
                request.getSession().setAttribute("errorMsg", "Lưu thất bại do lỗi dữ liệu hoặc ràng buộc SQL.");
                response.sendRedirect("EditUser?id=" + userId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMsg", "Lỗi hệ thống: " + e.getMessage());
            response.sendRedirect("User");
        }
    }
}