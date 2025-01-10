package pap.backend.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pap.backend.user.User;
import pap.backend.user.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @Autowired
    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Cart>> getCarts() {
        return new ResponseEntity<List<Cart>>(cartService.getCarts(), HttpStatus.OK);
    }

    @GetMapping("/get/{cartId}")
    public ResponseEntity<Cart> getCart(@PathVariable("cartId") Long cartId) {
        return new ResponseEntity<Cart>(cartService.getCart(cartId), HttpStatus.OK);
    }

    @GetMapping("/byUser/{userId}")
    public ResponseEntity<Long> getCartIdByUserId(@PathVariable Long userId) {
        return new ResponseEntity<Long>(cartService.getCartIdByUserId(userId), HttpStatus.OK);
    }

    @PostMapping("/add") // W ciele Usera podajemy tylko jego ID
    public ResponseEntity<String> addNewCart(@RequestBody Cart cart) {
        try {
            cartService.addNewCart(cart);
            return new ResponseEntity<String>("Cart added", HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error adding cart", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{cartId}")
    public ResponseEntity<String> deleteCart(@PathVariable("cartId") Long cartId) {
        try {
            cartService.deleteCart(cartId);
            return new ResponseEntity<String>("Cart deleted", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error deleting cart", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{cartId}")
    public ResponseEntity<String> updateCart(
            @PathVariable("cartId") Long cartId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) LocalDate lastUpdate) {
        try {
            cartService.updateCart(cartId, userId, lastUpdate);
            return new ResponseEntity<String>("Cart updated", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error updating cart", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
