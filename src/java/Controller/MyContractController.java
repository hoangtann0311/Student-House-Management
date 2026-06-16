package Controller;

import Model.Contract;
import Model.StudentUser;
import Model.User;
import dal.ContractDAO;
import dal.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public class MyContractController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
            
        User user = (User) request.getSession().getAttribute("user");
        
        // 1. Kiểm tra phân quyền: Chưa đăng nhập hoặc là Admin thì đá về Home
        if (user == null || user.getRoleId() == 1) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        UserDAO userDAO = new UserDAO();
        ContractDAO contractDAO = new ContractDAO();

        // 2. Lấy thông tin chi tiết của sinh viên từ UserID
        StudentUser studentInfo = userDAO.getStudentDetailByUserId(String.valueOf(user.getUserId()));

        if (studentInfo != null) {
            // 3. Lấy danh sách hợp đồng của riêng sinh viên này
            // Lưu ý: Nếu DAO của bạn có hàm lấy TẤT CẢ hợp đồng (cả hết hạn) thì nên dùng để sinh viên xem được lịch sử.
            // Ở đây mình tạm dùng hàm findActiveContractsByStudentId như code cũ của bạn.
            List<Contract> myContracts = contractDAO.findActiveContractsByStudentId(studentInfo.getStudentId());
            
            // Gửi dữ liệu sang JSP
            request.setAttribute("myContracts", myContracts);
        }
        
        // 4. Chuyển hướng sang giao diện
        request.getRequestDispatcher("View/MyContracts.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Sinh viên không có quyền thao tác POST (tạo/sửa) ở trang này, nên cứ đá về trang GET
        response.sendRedirect(request.getContextPath() + "/MyContract");
    }

    @Override
    public String getServletInfo() {
        return "Controller quản lý hợp đồng của sinh viên";
    }
}