package Controller;

import Model.Room;
import Model.User;
import dal.RoomDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public class RoomController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session == null) ? null : (User) session.getAttribute("user");

        // Bảo mật: Chặn người chưa đăng nhập hoặc sinh viên truy cập trái phép
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        if (currentUser.getRoleId() != 1) {
            response.sendRedirect(request.getContextPath() + "/MyRoom");
            return;
        }

        RoomDAO roomDao = new RoomDAO();
        List<Room> rooms;

        int recordsPerPage = 10; 
        int currentPage = 1;
        
        if (request.getParameter("page") != null) {
            try {
                currentPage = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        int offset = (currentPage - 1) * recordsPerPage;
        int totalRecords = 0;

        // Lấy 5 tham số từ form lọc trên JSP
        String roomNumber = request.getParameter("roomNumber");
        String capacity = request.getParameter("capacity");
        String minPrice = request.getParameter("minPrice");
        String maxPrice = request.getParameter("maxPrice");
        String status = request.getParameter("status");

        boolean isSearching = (roomNumber != null && !roomNumber.trim().isEmpty()) || 
                              (capacity != null && !capacity.trim().isEmpty()) || 
                              (minPrice != null && !minPrice.trim().isEmpty()) || 
                              (maxPrice != null && !maxPrice.trim().isEmpty()) || 
                              (status != null && !status.trim().isEmpty());

        if (isSearching) {
            totalRecords = roomDao.getTotalRoomsAdvanced(roomNumber, capacity, minPrice, maxPrice, status);
            rooms = roomDao.searchRoomsAdvanced(roomNumber, capacity, minPrice, maxPrice, status, offset, recordsPerPage);
        } else {
            totalRecords = roomDao.getTotalRooms();
            rooms = roomDao.getRoomsByPage(offset, recordsPerPage);
        }

        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
        
        // Đẩy dữ liệu ra JSP
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRoomsCount", totalRecords); 
        request.setAttribute("rooms", rooms);
        
        request.getRequestDispatcher("/View/Rooms.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Giao diện Rooms.jsp gọi form tìm kiếm bằng GET, nếu có ai gọi POST thì đẩy về GET xử lý
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Controller quản lý danh sách phòng cho Admin";
    }
}