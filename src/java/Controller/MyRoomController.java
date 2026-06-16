/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

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
 * @author admin
 */
public class MyRoomController extends HttpServlet {

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
            out.println("<title>Servlet MyRoomController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MyRoomController at " + request.getContextPath() + "</h1>");
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
    
    // Nếu chưa đăng nhập thì đá về trang chủ
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

    // 1. TÙY VÀO CHỨC NĂNG ĐỂ XÁC ĐỊNH ROOM ID
    
    if (invoiceIdStr != null) {
        // Chức năng A: Xem thông tin phòng thông qua Hóa Đơn (Invoice)
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
        // Chức năng B: Xem thông tin phòng cụ thể (Thường dùng cho Admin truyền thẳng roomId)
        roomId = Integer.parseInt(roomIdStr);
        resolvedUserId = invoiceDao.getUserIdByRoomId(roomId);
        
    } else {
        // Chức năng C: "My Room" - Không truyền tham số gì, tự lấy phòng của sinh viên đang đăng nhập
        if (user.getRoleId() != 1) { // Giả sử RoleID != 1 là sinh viên
            StudentUser studentInfo = userDao.getStudentDetailByUserId(String.valueOf(user.getUserId()));
            
            if (studentInfo != null && studentInfo.getStudentId() > 0) {
                Room myRoom = roomDao.findCurrentRoomByStudentId(studentInfo.getStudentId());
                if (myRoom != null) {
                    roomId = myRoom.getRoomID(); // Tìm thấy phòng của user
                }
            }
        }
    }

    // Kiểm tra nếu sau cả 3 trường hợp trên vẫn không có roomId (Ví dụ: Sinh viên chưa được xếp phòng)
    if (roomId == -1) {
        request.setAttribute("message", "Bạn hiện chưa được xếp vào phòng nào hoặc không tìm thấy dữ liệu.");
        request.getRequestDispatcher("View/MyRoom.jsp").forward(request, response); 
        // Lời khuyên: Bạn có thể tạo 1 trang View/Error.jsp hoặc View/NoRoom.jsp riêng để hiển thị lỗi này
        return;
    }

    // 2. Lấy thông tin phòng và Bảng giá theo roomId đã tìm được
    Room room = roomDao.getRoomById(roomId);
    List<InvoiceItem> unitPrices = roomDao.getLatestUnitPrices(roomId);

    // Nếu phòng mới chưa có hóa đơn, lấy giá mặc định
    if (unitPrices == null || unitPrices.isEmpty()) {
        unitPrices = roomDao.getDefaultFeeTypes();
    }

    request.setAttribute("roomInfo", room);
    request.setAttribute("unitPrices", unitPrices);
    request.setAttribute("isRoomEmpty", (resolvedUserId == -1));

    // 3. Lấy danh sách người thuê hiện tại trong phòng
    List<StudentUser> listStudents = userDao.getStudentsByRoomId(roomId);

    // Cập nhật lại trạng thái phòng trống
    request.setAttribute("isRoomEmpty", listStudents.isEmpty());
    request.setAttribute("listStudents", listStudents);

    // Forward dữ liệu sang trang giao diện chung
    request.getRequestDispatcher("View/RoomInfo.jsp").forward(request, response);
}

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        response.sendRedirect(request.getContextPath() + "/MyRoom");

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
