/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.Contract;
import Model.Invoice;
import Model.InvoiceItem;
import Model.Room;
import Model.StudentUser;
import Model.User;
import dal.ContractDAO;
import dal.InvoiceDAO;
import dal.RoomDAO;
import dal.UserDAO;
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
public class RoomInfoController extends HttpServlet {

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
            out.println("<title>Servlet RoomInfoController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RoomInfoController at " + request.getContextPath() + "</h1>");
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
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        String invoiceIdStr = request.getParameter("invoiceId");
        String roomIdStr = request.getParameter("roomId");

        InvoiceDAO invoiceDao = new InvoiceDAO();
        UserDAO userDao = new UserDAO();
        RoomDAO roomDao = new RoomDAO();
        ContractDAO contractDao = new ContractDAO();

        int resolvedUserId = -1;
        int roomId = -1;

        // 1. Xác định RoomID và UserID dựa trên tham số truyền vào
        if (invoiceIdStr != null) {
            int invId = Integer.parseInt(invoiceIdStr);
            Invoice inv = invoiceDao.getInvoiceById(invId);
            if (inv != null) {
                roomId = roomDao.getRoomIdByContractId(inv.getContractId());
                resolvedUserId = invoiceDao.getUserIdByInvoiceId(invId);

                request.setAttribute("viewInvoiceDetail", true);
                request.setAttribute("invoiceDetail", inv);
                request.setAttribute("items", invoiceDao.getInvoiceItems(invId));
            }
        } else if (roomIdStr != null) {
            roomId = Integer.parseInt(roomIdStr);
            resolvedUserId = invoiceDao.getUserIdByRoomId(roomId);
        }

        // ==============================================================
        // BƯỚC BẢO MẬT: CHẶN SINH VIÊN XEM TRỘM PHÒNG/HÓA ĐƠN NGƯỜI KHÁC
        // ==============================================================
        if (user.getRoleId() != 1) { // Nếu không phải là Admin
            StudentUser studentInfo = userDao.getStudentDetailByUserId(String.valueOf(user.getUserId()));
            boolean isAuthorized = false;

            if (studentInfo != null && studentInfo.getStudentId() > 0) {
                // Lấy phòng hiện tại sinh viên đang thuê
                Room currentRoom = roomDao.findCurrentRoomByStudentId(studentInfo.getStudentId());
                
                // Kiểm tra: ID phòng đang muốn xem CÓ KHỚP với ID phòng đang thuê không?
                if (currentRoom != null && currentRoom.getRoomID() == roomId) {
                    isAuthorized = true;
                }
            }

            if (!isAuthorized) {
                // Nếu cố tình đổi ID trên URL để xem trộm -> Báo lỗi và đá về trang Invoice
                session.setAttribute("errorMsg", "Lỗi: Bạn không có quyền xem thông tin hoặc hóa đơn của phòng này!");
                response.sendRedirect(request.getContextPath() + "/Invoice");
                return; // Dừng xử lý ngay lập tức
            }
        }
        // ==============================================================

        // 2. Lấy thông tin phòng và Bảng giá
        Room room = roomDao.getRoomById(roomId);
        List<InvoiceItem> unitPrices = roomDao.getLatestUnitPrices(roomId);

        // Nếu phòng mới chưa có hóa đơn, lấy giá mặc định
        if (unitPrices == null || unitPrices.isEmpty()) {
            unitPrices = roomDao.getDefaultFeeTypes();
        }

        request.setAttribute("roomInfo", room);
        request.setAttribute("unitPrices", unitPrices);
        request.setAttribute("isRoomEmpty", (resolvedUserId == -1));

        // 3. Lấy danh sách TẤT CẢ người thuê hiện tại trong phòng
        List<StudentUser> listStudents = userDao.getStudentsByRoomId(roomId);

        request.setAttribute("isRoomEmpty", listStudents.isEmpty());
        request.setAttribute("listStudents", listStudents);

        request.getRequestDispatcher("View/RoomInfo.jsp").forward(request, response);
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
