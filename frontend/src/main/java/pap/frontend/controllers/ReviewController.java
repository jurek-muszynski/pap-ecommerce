package pap.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pap.frontend.models.Review;
import pap.frontend.services.AuthService;
import pap.frontend.services.ReviewService;

import java.util.List;

public class ReviewController extends AuthenticatedController{


    public ReviewController() {
        super(AuthService.getInstance());
    }


    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }


    @FXML
    private VBox reviewPane;

    private final ReviewService reviewService = new ReviewService();
    private Long productId;
    private ScreenController screenController;

    public void setProductId(Long productId) {

        this.productId = productId;
        refreshData();
    }

    @FXML
    public void initialize() {
        refreshData();
    }

    private void loadReviews() {
        try {
            List<Review> reviews = reviewService.getReviewsByProductId(productId);
            displayReviews(reviews);
        } catch (Exception e) {
            showAlert("Error", "Failed to load reviews: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void displayReviews(List<Review> reviews) {
        reviewPane.getChildren().clear();

        for (Review review : reviews) {
            Label reviewLabel = new Label("Rating: " + review.getRate() + " | " + review.getDescription());
            reviewPane.getChildren().add(reviewLabel);
        }
    }

    @FXML
    private void goBackToCatalog() {
        // Przekierowanie do widoku katalogu
        if (screenController != null) {
            screenController.activate("userView");
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refreshData() {
        checkAuthentication();

        if (authService.isAuthenticated()) {
            loadReviews();
        }
    }
}
