package pap.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import pap.frontend.models.Product;
import pap.frontend.models.Review;
import pap.frontend.models.User;
import pap.frontend.services.AuthService;
import pap.frontend.services.ProductService;
import pap.frontend.services.ReviewService;

import java.util.List;

public class ReviewController extends AuthenticatedController {

    public ReviewController() {
        super(AuthService.getInstance());
    }

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML
    private VBox reviewPane;

    @FXML
    private Button addReviewButton;

    @FXML
    private Button refreshButton;

    private final ReviewService reviewService = new ReviewService();
    private Long productId;
    private ScreenController screenController;
    private final ProductService productService = new ProductService();

    public void setProductId(Long productId) {
        this.productId = productId;
        refreshData();
    }

    @FXML
    public void initialize() {
        refreshButton.setOnAction(event -> refreshData());
        addReviewButton.setOnAction(event -> openAddReviewDialog());
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
        if (screenController != null) {
            screenController.activate("userView");
        }
    }

    private void openAddReviewDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Review");

        // Tworzenie pól formularza
        VBox dialogPane = new VBox(10);
        dialogPane.setStyle("-fx-padding: 10;");

        Label rateLabel = new Label("Rate:");
        ComboBox<Integer> rateComboBox = new ComboBox<>();
        rateComboBox.getItems().addAll(1, 2, 3, 4, 5);

        Label descriptionLabel = new Label("Comment:");
        TextField descriptionField = new TextField();

        dialogPane.getChildren().addAll(rateLabel, rateComboBox, descriptionLabel, descriptionField);

        dialog.getDialogPane().setContent(dialogPane);

        // Dodanie przycisków
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                Integer rate = rateComboBox.getValue();
                String description = descriptionField.getText();

                if (rate == null || description.isEmpty()) {
                    showAlert("Validation Error", "Please provide both rate and description.", Alert.AlertType.ERROR);
                    return null;
                }

                User user = authService.getCurrentUser();
                Product product = productService.getProductById(productId);
                Review newReview = new Review();
                newReview.setRate(rate);
                newReview.setDescription(description);
                newReview.setProduct(product);
                newReview.setUser(user);

                try {
                    // Sprawdzamy, czy użytkownik już dodał opinię
                    List<Review> existingReviews = reviewService.getReviewsByProductId(productId);
                    Review existingReview = existingReviews.stream()
                            .filter(r -> r.getUser().getId().equals(user.getId()))
                            .findFirst()
                            .orElse(null);

                    // Jeśli użytkownik ma już opinię, usuwamy ją
                    if (existingReview != null) {
                        reviewService.deleteReview(existingReview.getId());
                    }

                    // Dodajemy nową opinię
                    reviewService.addReview(newReview);
                    refreshData();
                    showAlert("Success", "Review added successfully!", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    showAlert("Error", "Failed to add review: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        dialog.showAndWait();
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
