package Controller;

import Model.User;
import dal.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class UserCreateController extends HttpServlet {

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User u = (session == null) ? null : (User) session.getAttribute("user");
        return u != null && u.getRoleId() == 1;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }
        request.getRequestDispatcher("/View/UserCreate.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        // Lấy thông tin
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirmPassword");
        String roleRaw = request.getParameter("roleId");
        String q = request.getParameter("securityQuestion");
        String a = request.getParameter("securityAnswer");
        String fullName = request.getParameter("fullName");
        String studentCode = request.getParameter("studentCode");
        String dobRaw = request.getParameter("dob");
        String gender = request.getParameter("gender");
        String phone = request.getParameter("phone");

       // Lưu lại dữ liệu người dùng vừa nhập để nếu có lỗi thì điền lại vào form
        request.setAttribute("username", username);
        request.setAttribute("roleId", roleRaw);
        request.setAttribute("securityAnswer", a);
        request.setAttribute("fullName", fullName);
        request.setAttribute("studentCode", studentCode);
        request.setAttribute("dob", dobRaw);
        request.setAttribute("gender", gender);
        request.setAttribute("phone", phone);

        if (username == null || username.isBlank() || password == null || password.isBlank() || confirm == null || confirm.isBlank() || roleRaw == null || roleRaw.isBlank()) {
            request.setAttribute("errorMessage", "Vui lòng nhập đầy đủ thông tin tài khoản cơ bản.");
            request.getRequestDispatcher("/View/UserCreate.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirm)) {
            request.setAttribute("errorMessage", "Mật khẩu nhập lại không khớp.");
            request.getRequestDispatcher("/View/UserCreate.jsp").forward(request, response);
            return;
        }

        int roleId;
        try {
            roleId = Integer.parseInt(roleRaw);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Role không hợp lệ.");
            request.getRequestDispatcher("/View/UserCreate.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();

        if (dao.existsByUsername(username.trim())) {
            request.setAttribute("errorMessage", "Tài khoản đã tồn tại.");
            request.getRequestDispatcher("/View/UserCreate.jsp").forward(request, response);
            return;
        }

        boolean ok = false;

        if (roleId == 2) {
            if (fullName == null || fullName.isBlank() || studentCode == null || studentCode.isBlank()) {
                request.setAttribute("errorMessage", "Tạo tài khoản Student bắt buộc phải nhập Họ và Tên và Mã sinh viên.");
                request.getRequestDispatcher("/View/UserCreate.jsp").forward(request, response);
                return;
            }

            java.sql.Date dob = null;
            if (dobRaw != null && !dobRaw.isEmpty()) {
                try { dob = java.sql.Date.valueOf(dobRaw); } catch (Exception e) { }
            }
            ok = dao.registerWithStudent(username.trim(), password, roleId, q, a, fullName, dob, gender, phone, studentCode);
        } else {
            ok = dao.register(username.trim(), password, roleId, q, a);
        }

        if (!ok) {
            request.setAttribute("errorMessage", "Tạo tài khoản thất bại do lỗi hệ thống.");
            request.getRequestDispatcher("/View/UserCreate.jsp").forward(request, response);
            return;
        }
        request.getSession().setAttribute("successMsg", "Đã tạo tài khoản mới thành công!");

        response.sendRedirect(request.getContextPath() + "/User");
    }
}