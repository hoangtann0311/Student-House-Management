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

public class DeleteUser extends HttpServlet {

    // Hàm check Admin
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User u = (session == null) ? null : (User) session.getAttribute("user");
        return u != null && u.getRoleId() == 1;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CHẶN BẢO MẬT: Sinh viên truy cập trái phép -> Đá về Home
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        String id = request.getParameter("id");
        request.setAttribute("id", id);
        RequestDispatcher rd = request.getRequestDispatcher("View/DeleteUser.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CHẶN BẢO MẬT LẦN 2 Ở POST
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        String userId = request.getParameter("id");
        UserDAO dao = new UserDAO();
        HttpSession session = request.getSession();

        if (userId != null && !userId.isEmpty()) {
            try {
                // Gọi hàm check logic đã viết trong DAO
                String result = dao.deleteAccountWithCheck(userId);

                if ("SUCCESS".equals(result)) {
                    session.setAttribute("successMsg", "Đã xóa tài khoản thành công.");
                } else {
                    session.setAttribute("errorMsg", result); 
                }
            } catch (Exception e) {
                session.setAttribute("errorMsg", "Lỗi hệ thống: " + e.getMessage());
            }
        }

        response.sendRedirect("User"); 
    }
}