package Controller;

import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import Model.User;

public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Nếu đã login rồi thì về Home luôn
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        // Hiển thị form login
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validate đơn giản
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("errorMessage", "Vui lòng nhập tài khoản và mật khẩu.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.checkLogin(username.trim(), password);

        if (user != null) {
            // Khởi tạo Session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);

            // ==========================================================
            // THÊM: TỰ ĐỘNG ĐĂNG XUẤT SAU 5 PHÚT KHÔNG HOẠT ĐỘNG
            // Đơn vị tính bằng GIÂY (300 giây = 5 phút)
            // ==========================================================
            session.setMaxInactiveInterval(300); 

            // Redirect chuẩn
            response.sendRedirect(request.getContextPath() + "/Home");
        } else {
            request.setAttribute("errorMessage", "Sai tài khoản hoặc mật khẩu.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}