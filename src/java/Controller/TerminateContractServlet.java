package Controller;

import Model.Contract;
import Model.User;
import dal.ContractDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class TerminateContractServlet extends HttpServlet {

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

        try {
            int contractId = Integer.parseInt(request.getParameter("id"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));

            ContractDAO dao = new ContractDAO();
            Contract c = dao.getContractById(contractId);

            if (c != null) {
                java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
                boolean isEarly = c.getEndDate().after(today);

                request.setAttribute("contract", c);
                request.setAttribute("isEarly", isEarly);
                request.setAttribute("roomId", roomId);

                request.getRequestDispatcher("View/TerminateContract.jsp").forward(request, response);
            } else {
                response.sendRedirect("Contract");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("Contract");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        try {
            int contractId = Integer.parseInt(request.getParameter("contractId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            boolean isEarly = Boolean.parseBoolean(request.getParameter("isEarly"));

            ContractDAO dao = new ContractDAO();

            if (dao.terminateContract(contractId, roomId, isEarly)) {
                String msg = isEarly ? "Đã phá hợp đồng sớm và thu hồi tiền cọc!" : "Đã thanh lý hợp đồng đúng hạn!";
                request.getSession().setAttribute("successMsg", msg);
            } else {
                request.getSession().setAttribute("errorMsg", "Có lỗi xảy ra khi thanh lý.");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi dữ liệu thanh lý.");
        }

        response.sendRedirect(request.getContextPath() + "/Contract");
    }
}