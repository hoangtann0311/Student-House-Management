package Controller;

import Model.Room;
import Model.Student;
import Model.User;
import dal.ContractDAO;
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

public class CreateContractServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || user.getRoleId() != 1) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        UserDAO userDAO = new UserDAO();
        RoomDAO roomDAO = new RoomDAO();

        List<Student> listStudent = userDAO.getStudentsNotRenting();
        List<Room> listRoom = roomDAO.getAvailableRooms();

        request.setAttribute("listStudent", listStudent);
        request.setAttribute("listRoom", listRoom);
        request.getRequestDispatcher("View/CreateContract.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || user.getRoleId() != 1) {
            return;
        }
        request.setCharacterEncoding("UTF-8");

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));

            ContractDAO contractDAO = new ContractDAO();
            
            // Phân luồng logic: Ở ghép hay Phòng trống?
            double[] existingPrices = contractDAO.getActiveContractPricesByRoom(roomId);
            boolean isSharedRoom = (existingPrices != null);

            int months = 0;
            double depositAmount = 0, electricPrice = 3500, waterPrice = 15000, wifiPrice = 100000, garbagePrice = 50000;

            // XỬ LÝ TIỀN CỌC (Áp dụng chung): Lấy dữ liệu nếu có nhập, để trống thì mặc định là 0đ
            String depositStr = request.getParameter("depositAmount");
            if (depositStr != null && !depositStr.trim().isEmpty()) {
                try { depositAmount = Double.parseDouble(depositStr); } catch (NumberFormatException ignored) {}
            }

            if (isSharedRoom) {
                // --- LUỒNG Ở GHÉP ---
                // Chỉ lấy lại giá trị cũ của hợp đồng, KHÔNG đọc thông số người dùng nhập ở giao diện
                electricPrice = existingPrices[0];
                waterPrice = existingPrices[1];
                wifiPrice = existingPrices[2];
                garbagePrice = existingPrices[3];
                // Biến 'months' cứ giữ là 0 vì DAO sẽ không dùng đến nó
                
            } else {
                // --- LUỒNG PHÒNG TRỐNG ---
                // Bắt buộc ép người dùng phải điền đủ Số tháng và Giá điện nước
                try {
                    months = Integer.parseInt(request.getParameter("months"));
                    electricPrice = Double.parseDouble(request.getParameter("electricPrice"));
                    waterPrice = Double.parseDouble(request.getParameter("waterPrice"));
                    
                    String wifiStr = request.getParameter("wifiPrice");
                    if (wifiStr != null && !wifiStr.isEmpty()) wifiPrice = Double.parseDouble(wifiStr);
                    
                    String garbageStr = request.getParameter("garbagePrice");
                    if (garbageStr != null && !garbageStr.isEmpty()) garbagePrice = Double.parseDouble(garbageStr);

                } catch (NumberFormatException e) {
                    session.setAttribute("errorMsg", "Lỗi: Bạn đang tạo hợp đồng cho phòng trống. Vui lòng điền ĐẦY ĐỦ Số tháng và Giá dịch vụ!");
                    response.sendRedirect(request.getContextPath() + "/CreateContract");
                    return;
                }
            }

            // Gọi hàm DAO thực thi
            boolean isSuccess = contractDAO.createContractForExistingStudent(userId, roomId, months, depositAmount, electricPrice, waterPrice, wifiPrice, garbagePrice);

            if (isSuccess) {
                session.setAttribute("successMsg", "Tạo hợp đồng thành công! Sinh viên đã được xếp vào phòng.");
            } else {
                session.setAttribute("errorMsg", "Lỗi: Xử lý hợp đồng thất bại. Vui lòng thử lại!");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("errorMsg", "Lỗi: Vui lòng chọn Sinh viên và Phòng hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/CreateContract");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/Contract");
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}