package pap.backend.orderItem;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pap.backend.order.Order;
import pap.backend.order.OrderRepository;
import pap.backend.product.Product;
import pap.backend.product.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<OrderItem> getOrderItems() {
        return orderItemRepository.findAll();
    }

    public OrderItem getOrderItem(Long orderItemId) {
        return orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalStateException(
                        "OrderItem with id " + orderItemId + " does not exist"
                ));
    }

    public void addNewOrderItem(OrderItem orderItem) {
        Optional<Order> orderOptional = orderRepository.findById(orderItem.getOrder().getId());
        if (orderOptional.isEmpty()) {
            throw new IllegalStateException("Order with id " + orderItem.getOrder().getId() + " does not exist");
        }

        Optional<Product> productOptional = productRepository.findById(orderItem.getProduct().getId());
        if (productOptional.isEmpty()) {
            throw new IllegalStateException("Product with id " + orderItem.getProduct().getId() + " does not exist");
        }

        orderItemRepository.save(orderItem);
    }

    public void deleteOrderItem(Long orderItemId) {
        boolean exists = orderItemRepository.existsById(orderItemId);
        if (!exists) {
            throw new IllegalStateException("OrderItem with id " + orderItemId + " does not exist");
        }
        orderItemRepository.deleteById(orderItemId);
    }

    @Transactional
    public void updateOrderItem(Long orderItemId, Long productId, Long orderId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalStateException(
                        "OrderItem with id " + orderItemId + " does not exist"
                ));

        if (productId != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalStateException("Product with id " + productId + " does not exist"));
            orderItem.setProduct(product);
        }

        if (orderId != null) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalStateException("Order with id " + orderId + " does not exist"));
            orderItem.setOrder(order);
        }
    }

    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order with id " + orderId + " does not exist"));

        return orderItemRepository.findOrderItemsByOrderId(orderId);
    }


    public List<OrderItem> getOrderItemsByProductId(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product with id " + productId + " does not exist"));

        return orderItemRepository.findOrderItemsByProductId(productId);
    }
}
