package pap.backend.cartItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1/cartItem")
public class CartItemController {

    private final CartItemService cartItemService;

    @Autowired
    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CartItem>> getCartItems() {
        return new ResponseEntity<List<CartItem>>(cartItemService.getCartItems(), HttpStatus.OK);
    }

    @GetMapping("/get/{cartItemId}")
    public ResponseEntity<CartItem> getCartItem(@PathVariable("cartItemId") Long cartItemId) {
        return new ResponseEntity<CartItem>(cartItemService.getCartItem(cartItemId), HttpStatus.OK);
    }

    @GetMapping("/allWithCartId/{cartId}")
    public ResponseEntity<List<CartItem>> getCartItemsByCartId(@PathVariable("cartId") Long cartId) {
        return new ResponseEntity<List<CartItem>>(cartItemService.getCartItemsByCartId(cartId), HttpStatus.OK);
    }

    @GetMapping("/allWithProductId/{productId}")
    public ResponseEntity<List<CartItem>> getCartItemsByProductId(@PathVariable("productId") Long productId) {
        return new ResponseEntity<List<CartItem>>(cartItemService.getCartItemsByProductId(productId), HttpStatus.OK);
    }

    @PostMapping("/add") // W ciele żądania podajemy tylko id produktu i koszyka, reszta zostanie pobrana z encji Product i Cart
    public ResponseEntity<String> addNewCartItem(@RequestBody CartItem cartItem) {
        try {
            cartItemService.addNewCartItem(cartItem);
            return new ResponseEntity<String>("CartItem added successfully", HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error adding cartItem", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable("cartItemId") Long cartItemId) {
        try {
            cartItemService.deleteCartItem(cartItemId);
            return new ResponseEntity<String>("CartItem deleted successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error deleting cartItem", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/deleteAllByCartId/{cartId}")
    public ResponseEntity<String> deleteAllCartItemsByCartId(@PathVariable("cartId") Long cartId) {
        try {
            cartItemService.deleteAllCartItemsByCartId(cartId);
            return new ResponseEntity<>("All CartItems for Cart with id " + cartId + " have been deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting CartItems", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/deleteAllByProductId/{productId}")
    public ResponseEntity<String> deleteAllCartItemsByProductId(@PathVariable("productId") Long productId) {
        try {
            cartItemService.deleteAllCartItemsByProductId(productId);
            return new ResponseEntity<>("All CartItems for Product with id " + productId + " have been deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting CartItems", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<String> updateCartItem(
            @PathVariable("cartItemId") Long cartItemId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long cartId) {

        try {
            cartItemService.updateCartItem(cartItemId, productId, cartId);
            return new ResponseEntity<String>("CartItem updated successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error updating cartItem", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
