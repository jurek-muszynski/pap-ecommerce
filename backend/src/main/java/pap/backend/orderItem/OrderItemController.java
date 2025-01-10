package pap.backend.orderItem;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1/orderItem")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderItem>> getOrderItems() {
        return new ResponseEntity<List<OrderItem>>(orderItemService.getOrderItems(), HttpStatus.OK);
    }

    @GetMapping("/get/{orderItemId}")
    public ResponseEntity<OrderItem> getOrderItem(@PathVariable("orderItemId") Long orderItemId) {
        return new ResponseEntity<OrderItem>(orderItemService.getOrderItem(orderItemId), HttpStatus.OK);
    }

    @GetMapping("/allWithOrderId/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrderId(@PathVariable("orderId") Long orderId) {
        return new ResponseEntity<List<OrderItem>>(orderItemService.getOrderItemsByOrderId(orderId), HttpStatus.OK);
    }

    @GetMapping("/allWithProductId/{productId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByProductId(@PathVariable("productId") Long productId) {
        return new ResponseEntity<List<OrderItem>>(orderItemService.getOrderItemsByProductId(productId), HttpStatus.OK);
    }

    @GetMapping("/allWithUserId/{userId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByUserId(@PathVariable("userId") Long userId) {
        return new ResponseEntity<List<OrderItem>>(orderItemService.getOrderItemsByUserId(userId), HttpStatus.OK);
    }

    @PostMapping("/add") // W ciele żądania podajemy tylko id produktu i zamówienia, reszta zostanie pobrana z encji Product i Order
    public ResponseEntity<String> addNewOrderItem(@RequestBody OrderItem orderItem) {
        try {
            orderItemService.addNewOrderItem(orderItem);
            return new ResponseEntity<String>("OrderItem added successfully", HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error adding orderItem", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{orderItemId}")
    public ResponseEntity<String> deleteOrderItem(@PathVariable("orderItemId") Long orderItemId) {
        try {
            orderItemService.deleteOrderItem(orderItemId);
            return new ResponseEntity<String>("OrderItem deleted successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<String>("Error deleting orderItem", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{orderItemId}")
    public ResponseEntity<String> updateOrderItem(
            @PathVariable("orderItemId") Long orderItemId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long orderId) {

        try {
            orderItemService.updateOrderItem(orderItemId, productId, orderId);
            return new ResponseEntity<String>("OrderItem updated successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error updating orderItem", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
