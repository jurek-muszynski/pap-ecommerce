package pap.backend.order;


import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import pap.backend.auth.AuthService;
import pap.backend.cartItem.CartItem;
import pap.backend.cartItem.CartItemRepository;
import pap.backend.category.Category;
import pap.backend.category.CategoryService;
import pap.backend.mail.EmailService;
import pap.backend.orderItem.OrderItem;
import pap.backend.orderItem.OrderItemRepository;
import pap.backend.product.Product;
import pap.backend.product.ProductRepository;
import pap.backend.product.ProductService;
import pap.backend.user.User;
import pap.backend.user.UserRepository;
import pap.backend.user.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private final CategoryService categoryService;
    private final AuthService authService;
    private final UserService userService;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, CartItemRepository cartItemRepository, OrderItemRepository orderItemRepository, EmailService emailService, ProductService productService, CategoryService categoryService, CategoryService categoryService1, AuthService authService, UserService userService, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.emailService = emailService;
        this.productService = productService;
        this.categoryService = categoryService1;
        this.authService = authService;
        this.userService = userService;
        this.productRepository = productRepository;
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

        List<CartItem> cartItems = cartItemRepository.findCartItemsByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        orderRepository.save(order);
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(cartItem.getProduct(), order);
            orderItemRepository.save(orderItem);
        }


        sendConfirmationEmail(orderRequest);
        sendRecommendationEmail(orderRequest);

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
                    .append("<td style=\"padding: 10px; font-size: 16px; color: #333;\">")
                    .append(String.format("%d", cartItem.getQuantity()))
                    .append("</td>")
                    .append("</tr>");
            totalPrice += (product.getPrice() * cartItem.getQuantity());
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
                + "<th style=\"text-align: left; padding: 10px;\">Quantity</th>"
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

    private void sendRecommendationEmail(PlaceOrderRequest orderRequest) {
        String email = orderRequest.getEmail();
        User user = userService.getUser(orderRequest.getUserId());
        String subject = ("Hi " + user.getUsername() + ", check out some other products which you might like!");

        List<CartItem> cartItems = cartItemRepository.findCartItemsByUserId(user.getId());
        List<Long> productIdsInOrder = new ArrayList<>();
        List<Long> categoryIdsInOrder = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            productIdsInOrder.add(product.getId());

            if (!categoryIdsInOrder.contains(product.getCategory().getId())) {
                categoryIdsInOrder.add(product.getCategory().getId());
            }
        }

        List<Product> recommendedProducts = productRepository.findOtherProductsByCategoryIdsAndExcludeProducts(
                categoryIdsInOrder, productIdsInOrder);

        // Dodanie warunku, aby nie wysyłać e-maila, jeśli brak rekomendacji
        if (recommendedProducts.isEmpty()) {
            System.out.println("No recommended products found. Skipping email.");
            return;
        }

        StringBuilder recommendationDetails = new StringBuilder();
        for (Product product : recommendedProducts) {
            String productImageUrl = product.getImageUrl();
            recommendationDetails.append("<tr style=\"border-bottom: 1px solid #ddd; padding: 10px;\">")
                .append("<td style=\"padding: 10px;\"><img src=\"")
                .append(productImageUrl)
                .append("\" alt=\"Product Image\" style=\"width: 50px; height: 50px; object-fit: cover; border-radius: 5px;\"></td>")
                .append("<td style=\"padding: 10px; font-size: 16px; color: #333;\">")
                .append(product.getName())
                .append("</td>")
                .append("<td style=\"padding: 10px; font-size: 16px; color: #333;\">$")
                .append(String.format("%.2f", product.getPrice()))
                .append("</tr>");
        }

        // Create the HTML message body
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Thank you for your last order!</h2>"
                + "<p style=\"font-size: 16px;\">Here you can find some other products based on your preferences:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<table style=\"width: 100%; border-collapse: collapse;\">"
                + "<thead>"
                + "<tr style=\"border-bottom: 2px solid #ddd; background-color: #f9f9f9;\">"
                + "<th style=\"text-align: left; padding: 10px;\">Image</th>"
                + "<th style=\"text-align: left; padding: 10px;\">Product</th>"
                + "<th style=\"text-align: left; padding: 10px;\">Price</th>"
                + "</tr>"
                + "</thead>"
                + "<tbody>"
                + recommendationDetails
                + "</tbody>"
                + "</table>"
                + "</div>"
                + "<p style=\"font-size: 14px; color: #888;\">If you have any questions, please contact our support team.</p>"
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
