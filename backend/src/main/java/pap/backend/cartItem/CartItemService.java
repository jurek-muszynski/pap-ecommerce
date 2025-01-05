package pap.backend.cartItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pap.backend.cart.Cart;
import pap.backend.cart.CartRepository;
import pap.backend.product.Product;
import pap.backend.product.ProductRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartItemService(CartItemRepository cartItemRepository, CartRepository cartRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> getCartItems() {
        return cartItemRepository.findAll();
    }

    public CartItem getCartItem(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NoSuchElementException(
                        "CartItem with id " + cartItemId + " does not exist"
                ));
    }

    public void addNewCartItem(CartItem cartItem) {
        Optional<Cart> cartOptional = cartRepository.findById(cartItem.getCart().getId());
        if (cartOptional.isEmpty()) {
            throw new IllegalStateException("Cart with id " + cartItem.getCart().getId() + " does not exist");
        }

        Optional<Product> productOptional = productRepository.findById(cartItem.getProduct().getId());
        if (productOptional.isEmpty()) {
            throw new IllegalStateException("Product with id " + cartItem.getProduct().getId() + " does not exist");
        }

        List<CartItem> cartItems = cartItemRepository.findCartItemsByCartId(cartItem.getCart().getId());
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(cartItem.getProduct().getId())) {
                throw new IllegalStateException("Product with id " + cartItem.getProduct().getId() + " is already in the cart");
            }
        }

        cartItem.setCart(cartOptional.get());
        cartItem.setProduct(productOptional.get());

        cartItemRepository.save(cartItem);
    }

    public void deleteCartItem(Long cartItemId) {
        boolean exists = cartItemRepository.existsById(cartItemId);
        if (!exists) {
            throw new NoSuchElementException("CartItem with id " + cartItemId + " does not exist");
        }
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void deleteAllCartItemsByCartId(Long cartId) {
        // Sprawdzenie, czy Cart o podanym ID istnieje
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalStateException("Cart with id " + cartId + " does not exist"));

        // Usunięcie wszystkich CartItem powiązanych z Cart
        cartItemRepository.deleteCartItemsByCartId(cartId);
    }

    @Transactional
    public void deleteAllCartItemsByProductId(Long productId) {
        // Sprawdzenie, czy Produkt o podanym ID istnieje
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product with id " + productId + " does not exist"));

        // Usunięcie wszystkich CartItem powiązanych z Produktem
        cartItemRepository.deleteCartItemsByProductId(productId);
    }


    @Transactional
    public void updateCartItem(Long cartItemId, Long productId, Long cartId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NoSuchElementException(
                        "CartItem with id " + cartItemId + " does not exist"
                ));

        if (productId != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalStateException("Product with id " + productId + " does not exist"));
            cartItem.setProduct(product);
        }

        if (cartId != null) {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new IllegalStateException("Cart with id " + cartId + " does not exist"));
            cartItem.setCart(cart);
        }
    }

    public List<CartItem> getCartItemsByCartId(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalStateException("Cart with id " + cartId + " does not exist"));

        return cartItemRepository.findCartItemsByCartId(cartId);
    }

    public List<CartItem> getCartItemsByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product with id " + productId + " does not exist"));

        return cartItemRepository.findCartItemsByProductId(productId);
    }
}
