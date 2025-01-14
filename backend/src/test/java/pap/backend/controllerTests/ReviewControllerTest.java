package pap.backend.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pap.backend.config.JwtService;
import pap.backend.review.Review;
import pap.backend.review.ReviewController;
import pap.backend.review.ReviewService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private Review testReview;

    @BeforeEach
    void setUp() {
        testReview = new Review();
        testReview.setId(1L);
        testReview.setRate(5);
        testReview.setDescription("Great product!");
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getReviews_ShouldReturnAllReviews() throws Exception {
        List<Review> reviews = Arrays.asList(testReview);
        Mockito.when(reviewService.getReviews()).thenReturn(reviews);

        mockMvc.perform(get("/api/v1/review/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].rate").value(5))
                .andExpect(jsonPath("$[0].description").value("Great product!"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getReview_ShouldReturnReviewById() throws Exception {
        Mockito.when(reviewService.getReview(1L)).thenReturn(testReview);

        mockMvc.perform(get("/api/v1/review/get/{reviewId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.rate").value(5))
                .andExpect(jsonPath("$.description").value("Great product!"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void addNewReview_ShouldAddReview() throws Exception {
        doNothing().when(reviewService).addNewReview(Mockito.any(Review.class));

        mockMvc.perform(post("/api/v1/review/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReview))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("Review added successfully"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void deleteReview_ShouldDeleteReviewById() throws Exception {
        doNothing().when(reviewService).deleteReview(1L);

        mockMvc.perform(delete("/api/v1/review/delete/{reviewId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Review deleted successfully"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void updateReview_ShouldUpdateReview() throws Exception {
        doNothing().when(reviewService).updateReview(1L, 4, "Updated review");

        mockMvc.perform(put("/api/v1/review/update/{reviewId}", 1L)
                        .param("rate", "4")
                        .param("description", "Updated review")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Review updated successfully"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getReviewsByUserId_ShouldReturnReviewsForUser() throws Exception {
        List<Review> reviews = Arrays.asList(testReview);
        Mockito.when(reviewService.getReviewsByUserId(1L)).thenReturn(reviews);

        mockMvc.perform(get("/api/v1/review/allWithUserId/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getReviewsByProductId_ShouldReturnReviewsForProduct() throws Exception {
        List<Review> reviews = Arrays.asList(testReview);
        Mockito.when(reviewService.getReviewsByProductId(1L)).thenReturn(reviews);

        mockMvc.perform(get("/api/v1/review/allWithProductId/{productId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void deleteAllReviewsByProductId_ShouldDeleteReviewsForProduct() throws Exception {
        doNothing().when(reviewService).deleteAllReviewsByProductId(1L);

        mockMvc.perform(delete("/api/v1/review/deleteAllByProductId/{productId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("All Reviews for Product with id 1 have been deleted."));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void deleteAllReviewsByUserId_ShouldDeleteReviewsForUser() throws Exception {
        doNothing().when(reviewService).deleteAllReviewsByUserId(1L);

        mockMvc.perform(delete("/api/v1/review/deleteAllByUserId/{userId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("All Reviews for User with id 1 have been deleted."));
    }
}
