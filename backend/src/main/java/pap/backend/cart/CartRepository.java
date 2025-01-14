package pap.backend.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c.id FROM Cart c WHERE c.user.id = :userId")
    Optional<Long> findCartIdByUserId(Long userId);


}
