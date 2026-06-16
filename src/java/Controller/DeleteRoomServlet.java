package Controller;

import Model.User;
import dal.RoomDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class DeleteRoomServlet extends HttpServlet {

    private final RoomDAO dao = new RoomDAO();

    // Hàm check Admin dùng chung
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User u = (session == null) ? null : (User) session.getAttribute("user");
        return u != null && u.getRoleId() == 1;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // GET chỉ dùng để chuyển hướng, không thực thi xóa
        response.sendRedirect(request.getContextPath() + "/Room");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. CHẶN BẢO MẬT: Không phải Admin -> Bật ra Trang chủ
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        String idRaw = request.getParameter("id");
        try {
            int id = Integer.parseInt(idRaw);
            
            // 2. LOGIC NGHIỆP VỤ: Đang có hợp đồng thì không được xóa
            if (dao.isRoomHasActiveContract(id)) {
                request.getSession().setAttribute("errorMsg", "Không thể xóa! Phòng này đang có người thuê.");
            } else {
                boolean ok = dao.deleteRoom(id);
                if (ok) {
                    request.getSession().setAttribute("successMsg", "Đã xóa phòng thành công!");
                } else {
                    request.getSession().setAttribute("errorMsg", "Lỗi: Không thể xóa do ràng buộc dữ liệu.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMsg", "Lỗi hệ thống khi xóa phòng.");
        }

        // Xóa xong thì quay về trang danh sách phòng
        response.sendRedirect(request.getContextPath() + "/Room");
    }
}