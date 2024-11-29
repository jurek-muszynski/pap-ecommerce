package pap.backend.orderItem;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pap.backend.order.Order;
import pap.backend.order.OrderRepository;
import pap.backend.product.Product;
import pap.backend.product.ProductRepository;

import java.util.List;

@RestController
@RequestMapping("api/v1/orderItem")
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderItemController(OrderItemService orderItemService, ProductRepository productRepository, OrderRepository orderRepository) {
        this.orderItemService = orderItemService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/all")
    public List<OrderItem> getOrderItems() {
        return orderItemService.getOrderItems();
    }

    @GetMapping("/get/{orderItemId}")
    public OrderItem getOrderItem(@PathVariable("orderItemId") Long orderItemId) {
        return orderItemService.getOrderItem(orderItemId);
    }

    @GetMapping("/allWithOrderId/{orderId}")
    public List<OrderItem> getOrderItemsByOrderId(@PathVariable("orderId") Long orderId) {
        return orderItemService.getOrderItemsByOrderId(orderId);
    }

    @GetMapping("/allWithProductId/{productId}")
    public List<OrderItem> getOrderItemsByProductId(@PathVariable("productId") Long productId) {
        return orderItemService.getOrderItemsByProductId(productId);
    }

    @PostMapping("/add") // W ciele żądania podajemy tylko id produktu i zamówienia, reszta zostanie pobrana z encji Product i Order
    public void addNewOrderItem(@RequestBody OrderItem orderItem) {

        Product product = productRepository.findById(orderItem.getProduct().getId())
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        Order order = orderRepository.findById(orderItem.getOrder().getId())
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        orderItem.setProduct(product);
        orderItem.setOrder(order);

        orderItemService.addNewOrderItem(orderItem);
    }

    @DeleteMapping("/delete/{orderItemId}")
    public void deleteOrderItem(@PathVariable("orderItemId") Long orderItemId) {
        orderItemService.deleteOrderItem(orderItemId);
    }

    @PutMapping("/update/{orderItemId}")
    public void updateOrderItem(
            @PathVariable("orderItemId") Long orderItemId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long orderId) {
        orderItemService.updateOrderItem(orderItemId, productId, orderId);
    }
}
