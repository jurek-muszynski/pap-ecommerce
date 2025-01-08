package pap.backend.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pap.backend.user.User;
import pap.backend.user.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Autowired
    public CartService(CartRepository cartRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    public List<Cart> getCarts() {
        return cartRepository.findAll();
    }

    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cart with id " + cartId + " does not exist"
                ));
    }

    public Long getCartIdByUserId(Long userId) {
        return cartRepository.findCartIdByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cart for user with id " + userId + " does not exist"
                ));
    }

    public void addNewCart(Cart cart) {
        User user = userRepository.findById(cart.getUser().getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        cart.setUser(user);
        cart.setLastUpdate(LocalDate.now()); // Ustawiamy aktualną datę jako domyślną wartość lastUpdate
        cartRepository.save(cart);
    }

    public void deleteCart(Long cartId) {
        boolean exists = cartRepository.existsById(cartId);
        if (!exists) {
            throw new NoSuchElementException("Cart with id " + cartId + " does not exist");
        }
        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void updateCart(Long cartId, Long userId, LocalDate lastUpdate) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cart with id " + cartId + " does not exist"
                ));

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("User with id " + userId + " does not exist"));
            cart.setUser(user);
        }

        if (lastUpdate != null && !cart.getLastUpdate().equals(lastUpdate)) {
            cart.setLastUpdate(lastUpdate);
        }
    }
}
