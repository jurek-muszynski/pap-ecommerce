package pap.backend.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pap.backend.product.Product;
import pap.backend.product.ProductRepository;
import pap.backend.user.User;
import pap.backend.user.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<Review> getReviews() {
        return reviewRepository.findAll();
    }

    public Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Review with id " + reviewId + " does not exist"
                ));
    }

    public void addNewReview(Review review) {
        Optional<User> userOptional = userRepository.findById(review.getUser().getId());
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("User with id " + review.getUser().getId() + " does not exist");
        }

        Optional<Product> productOptional = productRepository.findById(review.getProduct().getId());
        if (productOptional.isEmpty()) {
            throw new IllegalStateException("Product with id " + review.getProduct().getId() + " does not exist");
        }

        if (review.getRate() < 1 || review.getRate() > 5) {
            throw new IllegalStateException("Rate must be between 1 and 5");
        }

        review.setUser(userOptional.get());
        review.setProduct(productOptional.get());

        reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        boolean exists = reviewRepository.existsById(reviewId);
        if (!exists) {
            throw new NoSuchElementException("Review with id " + reviewId + " does not exist");
        }
        reviewRepository.deleteById(reviewId);
    }

    @Transactional
    public void deleteAllReviewsByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product with id " + productId + " does not exist"));

        reviewRepository.deleteReviewsByProductId(productId);
    }

    @Transactional
    public void deleteAllReviewsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User with id " + userId + " does not exist"));

        reviewRepository.deleteReviewsByUserId(userId);
    }

    @Transactional
    public void updateReview(Long reviewId, Integer rate, String description) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Review with id " + reviewId + " does not exist"
                ));

        if (rate != null) {
            if (rate < 1 || rate > 5) {
                throw new IllegalStateException("Rate must be between 1 and 5");
            }
            review.setRate(rate);
        }

        if (description != null && !description.isBlank()) {
            review.setDescription(description);
        }
    }

    public List<Review> getReviewsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User with id " + userId + " does not exist"));

        return reviewRepository.findReviewsByUserId(userId);
    }

    public List<Review> getReviewsByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product with id " + productId + " does not exist"));

        return reviewRepository.findReviewsByProductId(productId);
    }
}
