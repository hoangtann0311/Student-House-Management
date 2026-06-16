package Controller;

import Model.Payment;
import Model.User;
import dal.InvoiceDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class MyPaymentHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("Login");
            return;
        }
        
        if (user.getRoleId() == 1) {
            response.sendRedirect(request.getContextPath() + "/PaymentHistory");
            return;
        }

        String monthStr = request.getParameter("month");
        String yearStr = request.getParameter("year");
        Integer month = (monthStr != null && !monthStr.isEmpty()) ? Integer.parseInt(monthStr) : null;
        Integer year = (yearStr != null && !yearStr.isEmpty()) ? Integer.parseInt(yearStr) : null;

        // 1. LOGIC PHÂN TRANG (10 dòng/trang)
        int pageSize = 10;
        String pageParam = request.getParameter("page");
        int currentPage = 1;
        try {
            if (pageParam != null && !pageParam.isEmpty()) {
                currentPage = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            currentPage = 1;
        }
        int offset = (currentPage - 1) * pageSize;

        InvoiceDAO dao = new InvoiceDAO();

        // 2. GỌI CÁC HÀM DAO MỚI ĐỂ LẤY DỮ LIỆU
        List<Payment> listPayment = dao.getPaymentsByUserId(user.getUserId(), month, year, offset, pageSize);
        int totalRecords = dao.getTotalPaymentsCountByUserId(user.getUserId(), month, year);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        double totalPaid = dao.getTotalPaidAmountByUserId(user.getUserId(), month, year);

        // 3. ĐẨY DỮ LIỆU SANG JSP
        request.setAttribute("listPayment", listPayment);
        request.setAttribute("totalPaid", totalPaid);
        request.setAttribute("selectedMonth", month);
        request.setAttribute("selectedYear", year);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("View/MyPaymentHistory.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
