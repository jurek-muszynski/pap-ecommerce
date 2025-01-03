package pap.backend.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findReviewsByUserId(Long userId);

    List<Review> findReviewsByProductId(Long productId);

    void deleteReviewsByUserId(Long userId);

    void deleteReviewsByProductId(Long productId);
}
