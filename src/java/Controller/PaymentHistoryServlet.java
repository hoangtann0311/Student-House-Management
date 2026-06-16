package Controller;

import Model.Payment;
import Model.User;
import dal.InvoiceDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public class PaymentHistoryServlet extends HttpServlet {

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User u = (session == null) ? null : (User) session.getAttribute("user");
        return u != null && u.getRoleId() == 1;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CHẶN BẢO MẬT: Sinh viên tò mò doanh thu -> Đá về Home
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        String monthStr = request.getParameter("month");
        String yearStr = request.getParameter("year");

        Integer month = (monthStr != null && !monthStr.isEmpty()) ? Integer.parseInt(monthStr) : null;
        Integer year = (yearStr != null && !yearStr.isEmpty()) ? Integer.parseInt(yearStr) : null;

        InvoiceDAO dao = new InvoiceDAO();

        double totalCash = dao.getTotalRevenue("Tiền mặt", month, year);
        double totalTransfer = dao.getTotalRevenue("Chuyển khoản", month, year);
        double grandTotal = dao.getTotalRevenue(null, month, year);

        List<Payment> listPayment = dao.getPayments(month, year); 

        request.setAttribute("totalCash", totalCash);
        request.setAttribute("totalTransfer", totalTransfer);
        request.setAttribute("grandTotal", grandTotal);
        request.setAttribute("listPayment", listPayment);

        request.getRequestDispatcher("View/PaymentHistory.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}