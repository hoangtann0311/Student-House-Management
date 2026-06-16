/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.Invoice;
import Model.User;
import dal.InvoiceDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 *
 * @author HP
 */
public class InvoiceServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet InvoiceServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet InvoiceServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // 1. KIỂM TRA ĐĂNG NHẬP
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        // 2. XỬ LÝ THÔNG BÁO (Success/Error)
        if (session.getAttribute("successMsg") != null) {
            request.setAttribute("successMsg", session.getAttribute("successMsg"));
            session.removeAttribute("successMsg");
        }
        if (session.getAttribute("errorMsg") != null) {
            request.setAttribute("errorMsg", session.getAttribute("errorMsg"));
            session.removeAttribute("errorMsg");
        }

        InvoiceDAO dao = new InvoiceDAO();
        dal.RoomDAO roomDao = new dal.RoomDAO();

        // 3. NHẬN THAM SỐ BỘ LỌC
        String fRoom = request.getParameter("filterRoom");
        String fMonth = request.getParameter("filterMonth");
        String fStatus = request.getParameter("filterStatus");

        Integer roomId = (fRoom != null && !fRoom.isEmpty()) ? Integer.parseInt(fRoom) : null;
        Integer month = (fMonth != null && !fMonth.isEmpty()) ? Integer.parseInt(fMonth) : null;

        // Phân quyền: Admin (role 1) xem hết (targetUserId = 0), Student xem của mình
        int targetUserId = (user.getRoleId() == 1) ? 0 : user.getUserId();

        // 4. LOGIC PHÂN TRANG (10 dòng/trang)
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

        // 5. LẤY DỮ LIỆU TỪ DAO
        List<Invoice> listInvoice = dao.getInvoicesFiltered(roomId, month, fStatus, targetUserId, offset, pageSize);
        int totalRecords = dao.getTotalInvoicesCount(roomId, month, fStatus, targetUserId);
        // Tính tổng số bản ghi để ra tổng số trang
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        // 6. ĐẨY DỮ LIỆU SANG JSP
        request.setAttribute("listInvoice", listInvoice);
        request.setAttribute("listRoom", roomDao.getRentedRooms()); // JSP sẽ tự động ẩn nếu không phải Admin
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);

        // Forward tất cả sang Invoice.jsp
        request.getRequestDispatcher("View/Invoice.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
