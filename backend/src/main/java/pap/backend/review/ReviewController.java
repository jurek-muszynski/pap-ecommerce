package pap.backend.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Review>> getReviews() {
        return new ResponseEntity<>(reviewService.getReviews(), HttpStatus.OK);
    }

    @GetMapping("/get/{reviewId}")
    public ResponseEntity<Review> getReview(@PathVariable("reviewId") Long reviewId) {
        return new ResponseEntity<>(reviewService.getReview(reviewId), HttpStatus.OK);
    }

    @GetMapping("/allWithUserId/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUserId(@PathVariable("userId") Long userId) {
        return new ResponseEntity<>(reviewService.getReviewsByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/allWithProductId/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProductId(@PathVariable("productId") Long productId) {
        return new ResponseEntity<>(reviewService.getReviewsByProductId(productId), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewReview(@RequestBody Review review) {
        try {
            reviewService.addNewReview(review);
            return new ResponseEntity<>("Review added successfully", HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error adding review", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable("reviewId") Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting review", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteAllByProductId/{productId}")
    public ResponseEntity<String> deleteAllReviewsByProductId(@PathVariable("productId") Long productId) {
        try {
            reviewService.deleteAllReviewsByProductId(productId);
            return new ResponseEntity<>("All Reviews for Product with id " + productId + " have been deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting reviews for product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteAllByUserId/{userId}")
    public ResponseEntity<String> deleteAllReviewsByUserId(@PathVariable("userId") Long userId) {
        try {
            reviewService.deleteAllReviewsByUserId(userId);
            return new ResponseEntity<>("All Reviews for User with id " + userId + " have been deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting reviews for user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{reviewId}")
    public ResponseEntity<String> updateReview(
            @PathVariable("reviewId") Long reviewId,
            @RequestParam(required = false) Integer rate,
            @RequestParam(required = false) String description) {

        try {
            reviewService.updateReview(reviewId, rate, description);
            return new ResponseEntity<>("Review updated successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating review", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
