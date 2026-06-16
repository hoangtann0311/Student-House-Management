package Controller;

import Model.StudentUser;
import Model.User;
import dal.UserDAO;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;

public class ProfileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("user");

        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        UserDAO dao = new UserDAO();
        // Lấy toàn bộ thông tin của user đang đăng nhập
        StudentUser profile = dao.getUsersById(String.valueOf(loginUser.getUserId()));
        
        request.setAttribute("profile", profile);
        RequestDispatcher rd = request.getRequestDispatcher("View/Profile.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            HttpSession session = request.getSession();
            User loginUser = (User) session.getAttribute("user");

            UserDAO dao = new UserDAO();
            // Lấy lại dữ liệu cũ để không làm mất Password, RoleID, IsActive
            StudentUser su = dao.getUsersById(String.valueOf(loginUser.getUserId()));

            // Cập nhật thông tin bảo mật chung
            su.setSecurityQuestion(request.getParameter("SecurityQuestion"));
            su.setSecurityAnswer(request.getParameter("SecurityAnswer"));

            // Nếu là Sinh viên (Role = 2) thì cập nhật thêm thông tin cá nhân
            if (loginUser.getRoleId() == 2) {
                su.setFullName(request.getParameter("FullName"));
                su.setStudentCode(request.getParameter("CCCD"));
                su.setPhone(request.getParameter("Phone"));
                su.setGender(request.getParameter("Gender"));
                String dobStr = request.getParameter("DOB");
                if (dobStr != null && !dobStr.isEmpty()) {
                    su.setDob(Date.valueOf(dobStr));
                }
            }

            // Gọi hàm cập nhật
            dao.updateFullUser(su);

            request.setAttribute("success", "Cập nhật thông tin thành công!");
            doGet(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            doGet(request, response);
        }
    }
}