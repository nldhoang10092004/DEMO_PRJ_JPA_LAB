package controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import userDAO.IUserDAO;
import userDAO.UserDAO;

@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    // Dùng instance, không static
    private final IUserDAO userDao = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Lấy cookie nếu có để pre-fill form
        String savedUsername = "", savedPassword = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("username".equals(c.getName())) {
                    savedUsername = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                }
                if ("password".equals(c.getName())) {
                    savedPassword = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                }
            }
        }
        req.setAttribute("savedUsername", savedUsername);
        req.setAttribute("savedPassword", savedPassword);
        // Forward về login.jsp
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username   = req.getParameter("username");
        String password   = req.getParameter("password");
        String rememberMe = req.getParameter("rememberMe");

        // 1) Validate đầu vào
        if (username == null || username.isBlank()
         || password == null || password.isBlank()) {
            req.setAttribute("error", "Tên đăng nhập và mật khẩu không được để trống!");
            req.setAttribute("savedUsername", username);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            // 2) Gọi instance method
            User partial = userDao.checkLogin(username, password);

            if (partial != null) {
                // 3) Lấy đầy đủ thông tin user
                User fullUser = userDao.selectUser(partial.getId());
                // Ghi thông tin user vào session để các request sau xác thực
                req.getSession().setAttribute("user", fullUser);
                // Đưa vào request để JSP có thể in ra
                req.setAttribute("user", fullUser);

                // 4) Xử lý Remember Me
                if ("on".equals(rememberMe)) {
                    Cookie cu = new Cookie("username",
                        URLEncoder.encode(username, StandardCharsets.UTF_8));
                    Cookie cp = new Cookie("password",
                        URLEncoder.encode(password, StandardCharsets.UTF_8));
                    cu.setMaxAge(7 * 24 * 3600);
                    cp.setMaxAge(7 * 24 * 3600);
                    cu.setPath(req.getContextPath());
                    cp.setPath(req.getContextPath());
                    cu.setHttpOnly(true);
                    cp.setHttpOnly(true);
                    resp.addCookie(cu);
                    resp.addCookie(cp);
                } else {
                    clearCookies(req, resp);
                }

                // 5) Forward về login.jsp để hiển thị thông tin
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            // 6) Login thất bại
            req.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            req.setAttribute("savedUsername", username);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);

        } catch (SQLException e) {
            req.setAttribute("error", "Lỗi đăng nhập: " + e.getMessage());
            req.setAttribute("savedUsername", username);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private void clearCookies(HttpServletRequest req, HttpServletResponse resp) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return;
        for (Cookie c : cookies) {
            if ("username".equals(c.getName()) || "password".equals(c.getName())) {
                c.setMaxAge(0);
                c.setPath(req.getContextPath());
                resp.addCookie(c);
            }
        }
    }
    
}
