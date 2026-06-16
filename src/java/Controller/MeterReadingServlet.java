package Controller;

import Model.MeterReading;
import Model.User;
import Model.Room;
import dal.MeterReadingDAO;
import dal.RoomDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

public class MeterReadingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 1) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        // --- XỬ LÝ THÔNG BÁO ---
        if (session.getAttribute("successMsg") != null) {
            request.setAttribute("successMsg", session.getAttribute("successMsg"));
            session.removeAttribute("successMsg");
        }
        if (session.getAttribute("errorMsg") != null) {
            request.setAttribute("errorMsg", session.getAttribute("errorMsg"));
            session.removeAttribute("errorMsg");
        }

        MeterReadingDAO meterDao = new MeterReadingDAO();
        RoomDAO roomDao = new RoomDAO(); 

        List<Room> listRoom = roomDao.getRentedRooms(); 
        request.setAttribute("listRoom", listRoom);

        // BƯỚC 1: XÁC ĐỊNH THÁNG CẦN NHẬP
        int[] latest = meterDao.getLatestPeriodInDB();
        int targetM, targetY;
        int totalActive = meterDao.getTotalActiveRoomsCount();

        if (latest == null) {
            // Nếu DB trống, lấy tháng hiện tại
            targetM = LocalDate.now().getMonthValue();
            targetY = LocalDate.now().getYear();
        } else {
            int lastM = latest[0];
            int lastY = latest[1];
            int filled = meterDao.countFilledRooms(lastM, lastY);

            if (filled >= totalActive && totalActive > 0) {
                // ĐÃ XONG HẾT THÁNG CŨ -> Tăng tháng
                targetM = (lastM == 12) ? 1 : lastM + 1;
                targetY = (lastM == 12) ? lastY + 1 : lastY;
            } else {
                // CHƯA XONG -> Ở lại tháng cũ để nhập nốt
                targetM = lastM;
                targetY = lastY;
            }
        }

        // BƯỚC 2: LẤY DỮ LIỆU THEO THÁNG ĐÃ CHỌN
        List<MeterReading> listActiveRooms = meterDao.getLatestReadingsForAllActiveRooms(targetM, targetY);

        request.setAttribute("batchMonth", targetM);
        request.setAttribute("batchYear", targetY);
        request.setAttribute("listActiveRooms", listActiveRooms);

        // --- PHẦN BỘ LỌC LỊCH SỬ ---
        String fRoom = request.getParameter("filterRoom");
        String fMonth = request.getParameter("filterMonth");
        String fYear = request.getParameter("filterYear");

        Integer roomId = (fRoom != null && !fRoom.isEmpty()) ? Integer.parseInt(fRoom) : null;
        Integer month = (fMonth != null && !fMonth.isEmpty()) ? Integer.parseInt(fMonth) : null;
        Integer year = (fYear != null && !fYear.isEmpty()) ? Integer.parseInt(fYear) : null;

        int pageSize = 10;
        String pageParam = request.getParameter("page");
        int currentPage = (pageParam == null || pageParam.isEmpty()) ? 1 : Integer.parseInt(pageParam);
        int offset = (currentPage - 1) * pageSize;

        // Gọi hàm có bộ lọc trong DAO mà bạn đã viết trước đó
        List<MeterReading> listMeter = meterDao.getReadingsFiltered(roomId, month, year, offset, pageSize);
        int totalRecords = meterDao.getTotalReadingsFiltered(roomId, month, year);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        request.setAttribute("listMeter", listMeter);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", currentPage);

        request.getRequestDispatcher("View/MeterReading.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || user.getRoleId() != 1) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        try {
            String[] roomIds = request.getParameterValues("roomId[]");
            String[] roomNums = request.getParameterValues("roomNum[]");
            String[] electricIndices = request.getParameterValues("electricityIndex[]");
            String[] waterIndices = request.getParameterValues("waterIndex[]");

            int month = Integer.parseInt(request.getParameter("month"));
            int year = Integer.parseInt(request.getParameter("year"));

            if (roomIds != null) {
                MeterReadingDAO meterDao = new MeterReadingDAO();
                int successCount = 0;
                int errorCount = 0;
                StringBuilder errorDetail = new StringBuilder();

                for (int i = 0; i < roomIds.length; i++) {
                    String elecStr = electricIndices[i];
                    String waterStr = waterIndices[i];

                    // Chỉ xử lý khi có nhập đủ số
                    if (elecStr != null && !elecStr.trim().isEmpty() && waterStr != null && !waterStr.trim().isEmpty()) {
                        int rId = Integer.parseInt(roomIds[i]);
                        String rNum = roomNums[i];

                        // --- 1. KIỂM TRA TRÙNG LẶP ---
                        if (meterDao.checkReadingExists(rId, month, year)) {
                            errorCount++;
                            errorDetail.append(rNum).append(" (Đã có dữ liệu tháng ").append(month).append("); ");
                            continue; // Bỏ qua, sang phòng tiếp theo
                        }

                        // --- 2. KIỂM TRA SỐ MỚI < SỐ CŨ ---
                        int newElec = Integer.parseInt(elecStr);
                        int newWater = Integer.parseInt(waterStr);
                        MeterReading last = meterDao.getLatestReadingByRoom(rId);
                        if (last != null && (newElec < last.getElectricityIndex() || newWater < last.getWaterIndex())) {
                            errorCount++;
                            errorDetail.append(rNum).append(" (Số mới nhỏ hơn số cũ); ");
                            continue;
                        }

                        // --- 3. LƯU DỮ LIỆU ---
                        if (meterDao.insertMeterReading(rId, month, year, newElec, newWater)) {
                            successCount++;
                        }
                    }
                }

                // Gửi thông báo chi tiết về cho Admin
                if (errorCount > 0) {
                    session.setAttribute("errorMsg", "Thành công " + successCount + " phòng. Lỗi " + errorCount + " phòng: " + errorDetail.toString());
                } else if (successCount > 0) {
                    session.setAttribute("successMsg", "Đã lưu thành công cho toàn bộ " + successCount + " phòng!");
                }
            }
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Lỗi hệ thống: " + e.getMessage());
        }
        response.sendRedirect("MeterReading");
    }
}
