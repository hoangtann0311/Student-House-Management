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

public class EditRoomServlet extends HttpServlet {

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

        String idRaw = request.getParameter("id");
        try {
            int id = Integer.parseInt(idRaw);
            Room r = dao.getRoomById(id);

            if (r == null) {
                response.sendRedirect(request.getContextPath() + "/Room");
                return;
            }

            request.setAttribute("electricPrice", dao.getElectricPrice(id));
            request.setAttribute("waterPrice", dao.getWaterPrice(id));
            request.setAttribute("room", r);
            
            request.getRequestDispatcher("/View/RoomEdit.jsp").forward(request, response);

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/Room");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String idRaw = request.getParameter("roomID");
        String roomNumber = request.getParameter("roomNumber");
        String status = request.getParameter("status");
        String capRaw = request.getParameter("capacity");
        String rentRaw = request.getParameter("monthlyRent");
        String electricRaw = request.getParameter("electricPrice");
        String waterRaw = request.getParameter("waterPrice");

        try {
            int id = Integer.parseInt(idRaw);
            int capacity = Integer.parseInt(capRaw);
            int monthlyRent = Integer.parseInt(rentRaw);
            int electricPrice = Integer.parseInt(electricRaw);
            int waterPrice = Integer.parseInt(waterRaw);

            if (roomNumber == null || roomNumber.trim().isEmpty()) {
                request.setAttribute("error", "Số phòng không được để trống.");
                forwardBackWithData(request, response, id, electricPrice, waterPrice);
                return;
            }

            if (dao.checkRoomExist(roomNumber.trim(), id)) {
                request.setAttribute("error", "Tên phòng đã tồn tại. Vui lòng chọn tên khác!");
                forwardBackWithData(request, response, id, electricPrice, waterPrice);
                return;
            }

            if (capacity <= 0 || monthlyRent < 0 || electricPrice < 0 || waterPrice < 0) {
                request.setAttribute("error", "Các giá trị tiền và sức chứa không được âm hoặc bằng 0.");
                forwardBackWithData(request, response, id, electricPrice, waterPrice);
                return;
            }

            int currentOccupants = dao.getCurrentOccupants(id);
            if (capacity < currentOccupants) {
                request.setAttribute("error", "Không thể giảm sức chứa! Phòng này hiện đang có " + currentOccupants + " người ở.");
                forwardBackWithData(request, response, id, electricPrice, waterPrice);
                return;
            }

            if (status == null || status.trim().isEmpty()) status = "Trống";

            Room r = new Room();
            r.setRoomID(id);
            r.setRoomNumber(roomNumber.trim());
            r.setCapacity(capacity);
            r.setMonthlyRent(monthlyRent);
            r.setStatus(status.trim());

            boolean ok = dao.updateRoom(r);

            if (ok) {
                dao.updateServicePrices(id, electricPrice, waterPrice);
                request.getSession().setAttribute("successMsg", "Đã cập nhật phòng thành công!");
                response.sendRedirect(request.getContextPath() + "/Room");
            } else {
                request.setAttribute("error", "Cập nhật thất bại do lỗi database.");
                forwardBackWithData(request, response, id, electricPrice, waterPrice);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/Room");
        }
    }

    // Hàm hỗ trợ để set lại data tránh form JSP bị trắng khi có lỗi
    private void forwardBackWithData(HttpServletRequest request, HttpServletResponse response, int roomId, int ePrice, int wPrice) throws ServletException, IOException {
        request.setAttribute("room", dao.getRoomById(roomId));
        request.setAttribute("electricPrice", ePrice);
        request.setAttribute("waterPrice", wPrice);
        request.getRequestDispatcher("/View/RoomEdit.jsp").forward(request, response);
    }
}