package pap.backend.order;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pap.backend.user.User;
import pap.backend.user.UserRepository;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/order")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @Autowired
    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public List<Order> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/get/{orderId}")
    public Order getOrder(@PathVariable("orderId") Long orderId) {
        return orderService.getOrder(orderId);
    }

    @PostMapping("/add") //w ciele Usera podajemy tylko jego ID, reszta zostanie zignorowana, a dane zostana pobrane z tabeli users
    public void addNewOrder(@RequestBody Order order) {

        User user = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        order.setUser(user);

        orderService.addNewOrder(order);
    }

    @DeleteMapping("/delete/{orderId}")
    public void deleteOrder(@PathVariable("orderId") Long orderId) {
        orderService.deleteOrder(orderId);
    }

    @PutMapping("/update/{orderId}")
    public void updateOrder(
            @PathVariable("orderId") Long orderId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) LocalDate date) {
        orderService.updateOrder(orderId, userId, date);
    }
}
