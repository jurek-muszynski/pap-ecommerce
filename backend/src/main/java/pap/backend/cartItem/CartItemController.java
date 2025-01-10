package pap.backend.cartItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pap.backend.cart.Cart;
import pap.backend.cart.CartService;
import pap.backend.product.Product;
import pap.backend.product.ProductService;


import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1/cartItem")
public class CartItemController {

    private final CartItemService cartItemService;
    private final ProductService productService;
    private final CartService cartService;

    @Autowired
    public CartItemController(CartItemService cartItemService, ProductService productService, CartService cartService) {
        this.cartItemService = cartItemService;
        this.productService = productService;
        this.cartService = cartService;
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

    @GetMapping("/allWithUserId/{userId}")
    public ResponseEntity<List<CartItemDTO>> getCartItemsByUserId(@PathVariable("userId") Long userId) {
        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);

        // Convert to DTOs
        List<CartItemDTO> cartItemDTOs = cartItems.stream()
                .map(cartItem -> new CartItemDTO(
                        cartItem.getId(),
                        cartItem.getProduct().getId(),
                        cartItem.getCart().getId(),
                        cartItem.getQuantity()))
                        .toList();

        return new ResponseEntity<>(cartItemDTOs, HttpStatus.OK);
    }


    @GetMapping("/allWithProductId/{productId}")
    public ResponseEntity<List<CartItem>> getCartItemsByProductId(@PathVariable("productId") Long productId) {
        return new ResponseEntity<List<CartItem>>(cartItemService.getCartItemsByProductId(productId), HttpStatus.OK);
    }

    @GetMapping("/productsWithCartId/{cartId}")
    public ResponseEntity<List<Product>> getProductsByCartId(@PathVariable("cartId") Long cartId) {
        List<Product> products = cartItemService.getProductsByCartId(cartId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }


    @PostMapping("/add")
    public ResponseEntity<String> addNewCartItem(@RequestBody CartItemDTO cartItemDTO) {
        try {
            // Retrieve the product and cart from their respective services/repositories
            Product product = productService.getProduct(cartItemDTO.getProductId());
            Cart cart = cartService.getCart(cartItemDTO.getCartId());

            // Create and save a new CartItem entity, default quantity=1
            CartItem cartItem = new CartItem(product, cart, 1);
            cartItemService.addNewCartItem(cartItem);

            return new ResponseEntity<>("Product added to cart successfully", HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Invalid product or cart ID", HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error adding product to cart", HttpStatus.INTERNAL_SERVER_ERROR);
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
            @RequestBody Map<String, Object> updates) {

        try {
            Integer quantity = updates.containsKey("quantity") ? (Integer) updates.get("quantity") : null;

            cartItemService.updateCartItem(cartItemId, null, quantity, null);
            return new ResponseEntity<>("CartItem updated successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating cartItem", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
