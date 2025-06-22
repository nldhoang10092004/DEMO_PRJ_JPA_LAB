package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.User;

@WebServlet(name = "ProductListCartServlet", urlPatterns = {"/productListCart"})
public class ProductListCartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); // dùng false để không tạo session mới

        User user = (User) (session != null ? session.getAttribute("user") : null);

        if (user == null) {
            // Nếu chưa login, redirect về login.jsp
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Nếu đã login, forward đến JSP
        request.getRequestDispatcher("/productListCart.jsp").forward(request, response);
    }
}
