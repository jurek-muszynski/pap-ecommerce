package pap.backend.order;


import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import pap.backend.auth.AuthService;
import pap.backend.cartItem.CartItem;
import pap.backend.cartItem.CartItemRepository;
import pap.backend.mail.EmailService;
import pap.backend.orderItem.OrderItem;
import pap.backend.orderItem.OrderItemRepository;
import pap.backend.product.Product;
import pap.backend.product.ProductService;
import pap.backend.user.User;
import pap.backend.user.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final EmailService emailService;
    private final ProductService productService;
    private final AuthService authService;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, CartItemRepository cartItemRepository, OrderItemRepository orderItemRepository, EmailService emailService, ProductService productService, AuthService authService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.emailService = emailService;
        this.productService = productService;
        this.authService = authService;
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException(
                        "order with id " + orderId + " does not exist"
                ));
    }

    public void placeOrder(@RequestBody PlaceOrderRequest orderRequest) {
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setDate(LocalDate.now());

        orderRepository.save(order);

        List<CartItem> cartItems = cartItemRepository.findCartItemsByUserId(user.getId());
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(cartItem.getProduct(), order);
            orderItemRepository.save(orderItem);
        }

        sendConfirmationEmail(orderRequest);

        cartItemRepository.deleteAll(cartItems);
    }

    private void sendConfirmationEmail(PlaceOrderRequest orderRequest) {
        String email = orderRequest.getEmail();
        String deliveryAddress = orderRequest.getDeliveryAddress();
        String subject = "Order Confirmation";

        // Build the order details for the email
        Long userId = orderRequest.getUserId();
        List<CartItem> cartItems = cartItemRepository.findCartItemsByUserId(userId);
        double totalPrice = 0.0;

        StringBuilder orderDetails = new StringBuilder();
        for (CartItem cartItem : cartItems) {
            Product product = productService.getProduct(cartItem.getProduct().getId());
            String productImageUrl = product.getImageUrl();

            orderDetails.append("<tr style=\"border-bottom: 1px solid #ddd; padding: 10px;\">")
                    .append("<td style=\"padding: 10px;\"><img src=\"")
                    .append(productImageUrl)
                    .append("\" alt=\"Product Image\" style=\"width: 50px; height: 50px; object-fit: cover; border-radius: 5px;\"></td>")
                    .append("<td style=\"padding: 10px; font-size: 16px; color: #333;\">")
                    .append(product.getName())
                    .append("</td>")
                    .append("<td style=\"padding: 10px; font-size: 16px; color: #333;\">$")
                    .append(String.format("%.2f", product.getPrice()))
                    .append("</td>")
                    .append("</tr>");
            totalPrice += product.getPrice();
        }

        // Create the HTML message body
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Thank you for your order!</h2>"
                + "<p style=\"font-size: 16px;\">We are excited to process your order. Here are the details:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Delivery Address:</h3>"
                + "<p style=\"font-size: 16px;\">" + deliveryAddress + "</p>"
                + "<h3 style=\"color: #333;\">Order Details:</h3>"
                + "<table style=\"width: 100%; border-collapse: collapse;\">"
                + "<thead>"
                + "<tr style=\"border-bottom: 2px solid #ddd; background-color: #f9f9f9;\">"
                + "<th style=\"text-align: left; padding: 10px;\">Image</th>"
                + "<th style=\"text-align: left; padding: 10px;\">Product</th>"
                + "<th style=\"text-align: left; padding: 10px;\">Price</th>"
                + "</tr>"
                + "</thead>"
                + "<tbody>"
                + orderDetails
                + "</tbody>"
                + "</table>"
                + "<h3 style=\"color: #333;\">Total Price:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #28a745;\">$" + String.format("%.2f", totalPrice) + "</p>"
                + "</div>"
                + "<p style=\"font-size: 14px; color: #888;\">If you have any questions about your order, please contact our support team.</p>"
                + "<p style=\"font-size: 14px; color: #888;\">Thank you for choosing our service!</p>"
                + "</div>"
                + "</body>"
                + "</html>";
        try {
            emailService.sendEmail(email, subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void deleteOrder(Long orderId) {
        boolean exists = orderRepository.existsById(orderId);
        if (!exists) {
            throw new NoSuchElementException("order with id " + orderId + " does not exist");
        }
        orderRepository.deleteById(orderId);
    }

    @Transactional
    public void updateOrder(Long orderId, Long userId, LocalDate date) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException(
                        "order with id " + orderId + " does not exist"
                ));

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("User with id " + userId + " does not exist"));
            order.setUser(user);
        }

        if (date != null && !order.getDate().equals(date)) {
            order.setDate(date);
        }
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findOrdersByUserId(userId);
    }
}
