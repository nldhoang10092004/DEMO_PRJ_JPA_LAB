package service;

import java.util.Iterator;
import java.util.List;

import model.CartItem;
import model.Order;
import model.Product;
import orderDAO.OrderDAO;

public class CartService implements ICartService {

    private OrderDAO orderDAO;

    public CartService() {
        // ✅ Sửa: Lưu OrderDAO vào biến instance
        orderDAO = new OrderDAO();
    }

    @Override
    public void addToCart(List<CartItem> cart, Product product, int quantity) {
        if (cart == null || product == null || quantity <= 0) {
            return;
        }
        for (CartItem item : cart) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        cart.add(new CartItem(product, quantity));
    }

    @Override
    public void updateCartItem(List<CartItem> cart, int productId, int quantity) {
        if (cart == null) {
            return;
        }
        Iterator<CartItem> iterator = cart.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getProduct().getId() == productId) {
                if (quantity <= 0) {
                    iterator.remove();
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }

    @Override
    public void removeCartItem(List<CartItem> cart, int productId) {
        if (cart == null) {
            return;
        }
        cart.removeIf(item -> item.getProduct().getId() == productId);
    }

    @Override
    public int checkout(List<CartItem> cart, int userId) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng rỗng!");
        }

        ProductService productService = new ProductService();

        for (CartItem item : cart) {
            Product product = productService.getProductById(item.getProduct().getId());
            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("Sản phẩm '" + product.getName() + "' không đủ hàng trong kho!");
            }
        }

        double total = cart.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalPrice(total);
        order.setStatus("PENDING");

        int orderId = orderDAO.createOrder(order);  // ❗️Return value

        for (CartItem item : cart) {
            Product productInDb = productService.getProductById(item.getProduct().getId());
            double unitPrice = productInDb.getPrice();
            orderDAO.addOrderDetail(orderId, item.getProduct().getId(), item.getQuantity(), unitPrice);
            productService.updateStock(productInDb.getId(), productInDb.getStock() - item.getQuantity());
        }

        return orderId;
    }

}
