package pap.backend.cartItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findCartItemsByCartId(Long cartId);

    List<CartItem> findCartItemsByProductId(Long productId);

    void deleteCartItemsByCartId(Long cartId);

    void deleteCartItemsByProductId(Long productId);

    @Query
    ("SELECT c FROM CartItem c WHERE c.cart.user.id = :userId")
    List<CartItem> findCartItemsByUserId(Long userId);
}
