package Controller;

import Model.User;
import dal.MeterReadingDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class DeleteMeterServlet extends HttpServlet {

    // Hàm check Admin
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User u = (session == null) ? null : (User) session.getAttribute("user");
        return u != null && u.getRoleId() == 1;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();

        // CHẶN BẢO MẬT
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        try {
            int readingId = Integer.parseInt(request.getParameter("id"));
            MeterReadingDAO dao = new MeterReadingDAO();

            if (dao.isInvoicePaid(readingId)) {
                // Đã thanh toán, không cho xóa
                session.setAttribute("errorMsg", "Không thể xóa! Hóa đơn tháng này đã được thanh toán.");
            } else {
                // Chưa thanh toán, tiến hành xóa an toàn
                if (dao.deleteMeterReadingSafely(readingId)) {
                    session.setAttribute("successMsg", "Đã xóa chỉ số và hóa đơn chưa thanh toán thành công.");
                } else {
                    session.setAttribute("errorMsg", "Lỗi hệ thống khi xóa dữ liệu.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "ID không hợp lệ.");
        }
        
        response.sendRedirect("MeterReading");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // JSP không gọi POST tới file này, nếu có ai cố tình gọi thì đá về trang quản lý điện nước
        response.sendRedirect(request.getContextPath() + "/MeterReading");
    }
}