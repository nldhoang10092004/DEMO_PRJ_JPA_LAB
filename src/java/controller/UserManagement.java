package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import model.User;
import service.IUserService;
import service.UserService;
import userDAO.IUserDAO;
import userDAO.UserDAO;
@WebServlet(name="UserManagement", urlPatterns={"/users"})
public class UserManagement extends HttpServlet {

    private IUserService service;
    private IUserDAO UDAO;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void init() throws ServletException {
        this.service = new UserService();
        this.UDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // Check cookies for auto-login
            Cookie[] cookies = request.getCookies();
            String username = null, password = null;
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("username".equals(c.getName())) {
                        username = c.getValue();
                    }
                    if ("password".equals(c.getName())) {
                        password = c.getValue();
                    }
                }
            }
            if (username != null && password != null) {
                try {
                    User u = UDAO.checkLogin(username, password);
                    if (u != null) {
                        session = request.getSession();
                        session.setAttribute("user", u);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(UserManagement.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (session == null || session.getAttribute("user") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create":
                showCreateForm(request, response);
                break;
            case "update":
                showUpdateForm(request, response);
                break;
            case "delete":
                showDeleteForm(request, response);
                break;
            default:
                listUsers(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create":
                postCreateUser(request, response);
                break;
            case "update":
                postUpdateUser(request, response);
                break;
            case "delete":
                postDeleteUser(request, response);
                break;
            default:
                listUsers(request, response);
                break;
        }
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<User> users = service.getAllUsers();
        request.setAttribute("users", users);
        RequestDispatcher rd = request.getRequestDispatcher("/user/listUser.jsp");
        rd.forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/user/createUser.jsp");
        rd.forward(request, response);
    }

    private void showUpdateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            User u = service.getUserById(id);
            request.setAttribute("user", u);
            RequestDispatcher rd = request.getRequestDispatcher("/user/updateUser.jsp");
            rd.forward(request, response);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/user");
        }
    }

    private void showDeleteForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            User u = service.getUserById(id);
            request.setAttribute("user", u);
            RequestDispatcher rd = request.getRequestDispatcher("/user/deleteUser.jsp");
            rd.forward(request, response);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/user");
        }
    }

    private void postCreateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User u = extractUserFromRequest(request, false);
            service.createUser(u);

            // 3. Sau khi tạo xong, quay về danh sách
            listUsers(request, response);
        } catch (SQLException | IllegalArgumentException ex) {
            // Hiển thị lỗi rồi quay lại form tạo mới
            request.setAttribute("error", ex.getMessage());
            showCreateForm(request, response);
        }
    }

    private void postUpdateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User u = extractUserFromRequest(request, true);
            service.updateUser(u);
            listUsers(request, response);
        } catch (Exception ex) {
            request.setAttribute("error", ex.getMessage());
            showUpdateForm(request, response);
        }
    }

    private void postDeleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            service.deleteUser(id);
            listUsers(request, response);
        } catch (Exception ex) {
            request.setAttribute("error", ex.getMessage());
            listUsers(request, response);
        }
    }

    private User extractUserFromRequest(HttpServletRequest req, boolean includeId) {
        int id = includeId ? Integer.parseInt(req.getParameter("id")) : 0;
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String country = req.getParameter("country");
        String role = req.getParameter("role");
        boolean status = req.getParameter("status") != null;
        String pwd = req.getParameter("password");
        String dobStr = req.getParameter("dob");
        LocalDate dob = null;
        if (dobStr != null && !dobStr.isBlank()) {
            try {
                dob = LocalDate.parse(dobStr, DATE_FMT);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Định dạng ngày sinh không hợp lệ, phải là yyyy-MM-dd!");
            }
        }
        return new User(id, name, email, country, role, status, pwd, dob);
    }

    @Override
    public String getServletInfo() {
        return "User management servlet";
    }
}
