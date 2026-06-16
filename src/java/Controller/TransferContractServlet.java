package Controller;

import Model.Student;
import Model.User;
import dal.ContractDAO;
import dal.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public class TransferContractServlet extends HttpServlet {

    // Hàm check Admin
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User u = (session == null) ? null : (User) session.getAttribute("user");
        return u != null && u.getRoleId() == 1;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CHẶN BẢO MẬT
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        try {
            int contractId = Integer.parseInt(request.getParameter("id"));

            UserDAO uDao = new UserDAO();
            ContractDAO cDao = new ContractDAO();
            
            List<Student> currentTenants = cDao.getStudentsInContract(contractId);
            List<Student> listStudent = uDao.getAvailableStudentsForTransfer();

            request.setAttribute("contractId", contractId);
            request.setAttribute("currentTenants", currentTenants);
            request.setAttribute("listStudent", listStudent);
            request.getRequestDispatcher("View/TransferContract.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect("Contract");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CHẶN BẢO MẬT
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        try {
            int contractId = Integer.parseInt(request.getParameter("contractId"));
            int oldUserId = Integer.parseInt(request.getParameter("oldUserId"));
            int newUserId = Integer.parseInt(request.getParameter("newUserId"));

            ContractDAO cDao = new ContractDAO();
            if (cDao.transferContract(contractId, oldUserId, newUserId)) {
                request.getSession().setAttribute("successMsg", "Đã thay thế người thuê thành công!");
            } else {
                request.getSession().setAttribute("errorMsg", "Lỗi: Không thể thực hiện chuyển nhượng.");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi dữ liệu đầu vào.");
        }
        response.sendRedirect("Contract");
    }
}