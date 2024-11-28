package pap.backend.order;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pap.backend.user.User;
import pap.backend.user.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException(
                        "order with id " + orderId + " does not exist"
                ));
    }

    public void addNewOrder(Order order) {
        orderRepository.save(order);
    }

    public void deleteOrder(Long orderId) {
        boolean exists = orderRepository.existsById(orderId);
        if (!exists) {
            throw new IllegalStateException("order with id " + orderId + " does not exist");
        }
        orderRepository.deleteById(orderId);
    }

    @Transactional
    public void updateOrder(Long orderId, Long userId, LocalDate date) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException(
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

    public List<Order> getOrdersForUser(Long userId) {
        return orderRepository.findOrdersByUserId(userId);
    }
}
