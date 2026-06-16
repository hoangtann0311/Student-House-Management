package Controller;

import Model.Invoice;
import Model.User;
import dal.InvoiceDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class PayInvoiceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null && user.getRoleId() == 1) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                InvoiceDAO dao = new InvoiceDAO();
                Invoice inv = dao.getInvoiceById(id); // Lấy thông tin hóa đơn để hiện lên trang xác nhận

                if (inv != null) {
                    request.setAttribute("invoice", inv);
                    // Chuyển sang trang chọn Tiền mặt/Chuyển khoản
                    request.getRequestDispatcher("View/PayInvoice.jsp").forward(request, response);
                } else {
                    response.sendRedirect("Invoice");
                }
            } catch (Exception e) {
                response.sendRedirect("Invoice");
            }
        } else {
            response.sendRedirect("Home");
        }
    }

    // BƯỚC 2: Khi nhấn "Xác nhận thu tiền" ở trang PayConfirm (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null && user.getRoleId() == 1) {
            try {
                int invoiceId = Integer.parseInt(request.getParameter("invoiceId"));
                String paymentMethod = request.getParameter("paymentMethod");

                InvoiceDAO dao = new InvoiceDAO();
                if (dao.payInvoice(invoiceId, paymentMethod)) {
                    session.setAttribute("successMsg", "Đã thu tiền hóa đơn #" + invoiceId + " bằng " + paymentMethod);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        response.sendRedirect("Invoice");
    }
}