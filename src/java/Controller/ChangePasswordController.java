package Controller;

import Model.User;
import dal.UserDAO;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ChangePasswordController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect("Login");
            return;
        }
        RequestDispatcher rd = request.getRequestDispatcher("View/ChangePassword.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("user");

        // Đảm bảo tên tham số (oldPassword, newPassword...) phải khớp với thuộc tính 'name' trong file JSP
        String oldPass = request.getParameter("oldPassword");
        String newPass = request.getParameter("newPassword");
        String confirmPass = request.getParameter("confirmPassword");

        UserDAO dao = new UserDAO();
        
        // 1. Kiểm tra mật khẩu cũ xem có khớp với tài khoản đang đăng nhập không
        User checkUser = dao.checkLogin(loginUser.getUsername(), oldPass);

        if (checkUser == null) {
            request.setAttribute("error", "Mật khẩu hiện tại không chính xác!");
        } 
        // 2. CHẶN TRÙNG MẬT KHẨU CŨ (Phần bạn đang cần)
        else if (newPass.equals(oldPass)) {
            request.setAttribute("error", "Mật khẩu mới không được trùng với mật khẩu hiện tại!");
        } 
        // 3. Kiểm tra xác nhận mật khẩu có khớp không
        else if (!newPass.equals(confirmPass)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp với mật khẩu mới!");
        } 
        else {
            // 4. Cập nhật mật khẩu mới vào Database
            dao.updatePassword(loginUser.getUsername(), newPass);
            
            // Cập nhật lại đối tượng user trong session để đồng bộ
            loginUser.setPassword(newPass); 
            session.setAttribute("user", loginUser);
            
            request.setAttribute("success", "Đổi mật khẩu thành công!");
        }

        // Quay lại trang đổi mật khẩu để hiển thị thông báo
        RequestDispatcher rd = request.getRequestDispatcher("View/ChangePassword.jsp");
        rd.forward(request, response);
    }
}