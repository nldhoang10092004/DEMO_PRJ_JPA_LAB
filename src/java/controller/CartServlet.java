package controller;

import service.MailSendingService;
import jakarta.mail.MessagingException;
import java.io.*;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import model.*;
import service.*;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    private IProductService productService;
    private ICartService cartService;

    @Override
    public void init() {
        productService = new ProductService();
        cartService = new CartService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "remove":
                getRemove(request, response);
                break;
            default:
                getViewCart(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "add":
                postAdd(request, response);
                break;
            case "update":
                postUpdate(request, response);
                break;
            case "checkout":
                postCheckout(request, response);
                break;
            default:
                response.sendRedirect("cart/cart.jsp");
        }
    }

    // ============================= GET HANDLERS =============================
    private void getViewCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<CartItem> cart = getCartFromSession(session);
        request.setAttribute("cart", cart);
        request.getRequestDispatcher("cart/cart.jsp").forward(request, response);
    }

    private void getRemove(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        List<CartItem> cart = getCartFromSession(session);
        int productId = Integer.parseInt(request.getParameter("productId"));
        cartService.removeCartItem(cart, productId);
        session.setAttribute("cart", cart);
        response.sendRedirect("cart");
    }

    // ============================= POST HANDLERS =============================
    private void postAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        List<CartItem> cart = getCartFromSession(session);

        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        Product product = productService.getProductById(productId);

        if (product != null) {
            cartService.addToCart(cart, product, quantity);
        }

        session.setAttribute("cart", cart);
        response.sendRedirect("cart");
    }

    private void postUpdate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        List<CartItem> cart = getCartFromSession(session);

        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        cartService.updateCartItem(cart, productId, quantity);
        session.setAttribute("cart", cart);
        response.sendRedirect("cart");
    }

    private void postCheckout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        List<CartItem> cart = getCartFromSession(session);
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            session.setAttribute("message", "Vui lòng đăng nhập trước khi thanh toán.");
            response.sendRedirect("login.jsp");
            return;
        }

        if (cart == null || cart.isEmpty()) {
            session.setAttribute("message", "Giỏ hàng đang trống!");
            response.sendRedirect("cart");
            return;
        }

        try {
             int orderId = cartService.checkout(cart, currentUser.getId());
            cart.clear();
            session.setAttribute("cart", cart);
            session.setAttribute("message", "Đặt hàng thành công!");

            try {
                MailSendingService mailService = new MailSendingService();
                mailService.sendOrderConfirmation(request.getSession(), orderId);
            } catch (MessagingException e) {
                session.setAttribute("message", "Đặt hàng thành công nhưng gửi email thất bại: " + e.getMessage());
            }

        } catch (IllegalStateException ex) {
            session.setAttribute("message", "❌ " + ex.getMessage());  // sản phẩm không đủ hàng
        } catch (Exception e) {
            session.setAttribute("message", "❌ Lỗi trong quá trình đặt hàng: " + e.getMessage());
        }

        response.sendRedirect("cart");
    }

    // ============================= UTILITY =============================
    @SuppressWarnings("unchecked")
    private List<CartItem> getCartFromSession(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        return cart;
    }
}
