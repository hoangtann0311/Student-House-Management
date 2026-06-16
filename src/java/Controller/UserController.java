/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.User;
import dal.UserDAO;
import jakarta.servlet.RequestDispatcher;
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
 * @author HP
 */public class UserController extends HttpServlet {

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
            out.println("<title>Servlet UserController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UserController at " + request.getContextPath() + "</h1>");
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

        HttpSession session = request.getSession(false);
        User loginUser = (session == null) ? null : (User) session.getAttribute("user");

        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        if (loginUser.getRoleId() != 1) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        String searchText = request.getParameter("searchText");
        String roleId = request.getParameter("roleId");

        UserDAO userDao = new UserDAO();
        List<User> users;

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

        if (searchText != null && !searchText.isBlank()) {
            totalRecords = userDao.getTotalUsersBySearch(searchText.trim());
            users = userDao.getUsersBySearchAndPage(searchText.trim(), offset, recordsPerPage);
            request.setAttribute("searchText", searchText);
        } else if (roleId != null && !roleId.isBlank()) {
            totalRecords = userDao.getTotalUsersByRole(Integer.parseInt(roleId));
            users = userDao.getUsersByRoleAndPage(Integer.parseInt(roleId), offset, recordsPerPage);
            request.setAttribute("roleId", roleId);
        } else {
            totalRecords = userDao.getTotalUsers();
            users = userDao.getUsersByPage(offset, recordsPerPage);
        }

        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        // --- KẾT THÚC PHÂN TRANG ---

        for (User u : users) {
            if (u.getRoleId() == 2) {
                boolean renting = userDao.isRentingRoom(u.getUserId());
                u.setRenting(renting);
            }
        }

        request.setAttribute("users", users);
        request.getRequestDispatcher("/View/Users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/User");
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
