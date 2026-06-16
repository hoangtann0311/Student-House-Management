package Controller;

import Model.Room;
import Model.User;
import dal.RoomDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class AddRoomServlet extends HttpServlet {

    private final RoomDAO dao = new RoomDAO();

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User u = (session == null) ? null : (User) session.getAttribute("user");
        return u != null && u.getRoleId() == 1;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }
        request.getRequestDispatcher("/View/RoomAdd.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String roomNumber = request.getParameter("roomNumber");
        String status = request.getParameter("status");
        String capRaw = request.getParameter("capacity");
        String rentRaw = request.getParameter("monthlyRent");

        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            request.setAttribute("error", "Số phòng không được để trống.");
            request.getRequestDispatcher("/View/RoomAdd.jsp").forward(request, response);
            return;
        }

        if (dao.checkRoomExist(roomNumber.trim(), 0)) {
            request.setAttribute("error", "Tên phòng đã tồn tại. Vui lòng chọn tên khác!");
            request.getRequestDispatcher("/View/RoomAdd.jsp").forward(request, response);
            return;
        }

        int capacity, monthlyRent;
        try {
            capacity = Integer.parseInt(capRaw);
            monthlyRent = Integer.parseInt(rentRaw);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Sức chứa và Giá thuê phải là số hợp lệ.");
            request.getRequestDispatcher("/View/RoomAdd.jsp").forward(request, response);
            return;
        }

        if (capacity <= 0 || monthlyRent < 0) {
            request.setAttribute("error", "Sức chứa > 0 và Giá thuê >= 0.");
            request.getRequestDispatcher("/View/RoomAdd.jsp").forward(request, response);
            return;
        }

        if (status == null || status.trim().isEmpty()) status = "Trống";

        Room r = new Room();
        r.setRoomNumber(roomNumber.trim());
        r.setCapacity(capacity);
        r.setMonthlyRent(monthlyRent);
        r.setStatus(status.trim());

        boolean ok = dao.insertRoom(r);

        if (ok) {
            request.getSession().setAttribute("successMsg", "Thêm phòng mới thành công!");
            response.sendRedirect(request.getContextPath() + "/Room");
        } else {
            request.setAttribute("error", "Thêm phòng thất bại do lỗi hệ thống.");
            request.getRequestDispatcher("/View/RoomAdd.jsp").forward(request, response);
        }
    }
}