package Controller;

import dal.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ForgotPasswordController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("View/ForgotPassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String username = request.getParameter("username");
        UserDAO dao = new UserDAO();

        if ("checkUser".equals(action)) {
            String question = dao.getSecurityQuestionByUsername(username);
            if (question != null) {
                request.setAttribute("step", 2);
                request.setAttribute("question", question);
                request.setAttribute("targetUser", username);
            } else {
                request.setAttribute("errorMessage", "Tài khoản không tồn tại hoặc chưa thiết lập bảo mật.");
            }
        } else if ("verifyAnswer".equals(action)) {
            String answer = request.getParameter("answer");
            if (dao.verifySecurityAnswer(username, answer)) {
                request.setAttribute("step", 3);
                request.setAttribute("targetUser", username);
            } else {
                request.setAttribute("step", 2);
                request.setAttribute("question", dao.getSecurityQuestionByUsername(username));
                request.setAttribute("targetUser", username);
                request.setAttribute("errorMessage", "Câu trả lời bí mật không chính xác.");
            }
        } else if ("resetPass".equals(action)) {
            String newPass = request.getParameter("newPass");
            String confirmPass = request.getParameter("confirmPass"); 

            // 1. Kiểm tra khớp mật khẩu xác nhận
            if (newPass == null || !newPass.equals(confirmPass)) {
                request.setAttribute("step", 3);
                request.setAttribute("targetUser", username);
                request.setAttribute("errorMessage", "Xác nhận mật khẩu mới không khớp!");
                request.getRequestDispatcher("View/ForgotPassword.jsp").forward(request, response);
                return;
            }

            // 2. Chặn trùng mật khẩu cũ trong Database
            String oldPassInDB = dao.getPasswordByUsername(username); 
            if (newPass.equals(oldPassInDB)) {
                request.setAttribute("step", 3);
                request.setAttribute("targetUser", username);
                request.setAttribute("errorMessage", "Mật khẩu mới không được trùng với mật khẩu hiện tại!");
                request.getRequestDispatcher("View/ForgotPassword.jsp").forward(request, response);
                return;
            }

            // 3. Cập nhật mật khẩu
            dao.updatePassword(username, newPass);
            request.setAttribute("successMessage", "Đã đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }
        request.getRequestDispatcher("View/ForgotPassword.jsp").forward(request, response);
    }
}