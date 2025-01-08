package pap.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import pap.frontend.models.CartItem;
import pap.frontend.models.Product;
import pap.frontend.services.AuthService;
import pap.frontend.services.CartService;
import pap.frontend.services.ProductService;

import java.util.List;

public class SummaryController extends AuthenticatedController {
    @FXML
    private VBox summaryPane;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private TextField deliveryAddressField;

    @FXML
    private TextField emailField;

    @FXML
    private Button placeOrderButton;

    private final CartService cartService = new CartService();
    private final ProductService productService = new ProductService();

    public SummaryController() {
        super(AuthService.getInstance());
    }

    private ScreenController screenController;

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML
    public void initialize() {

        checkAuthentication();

        if (authService.isAuthenticated()){
            loadSummary();
        }

        // Disable Place Order button by default
        placeOrderButton.setDisable(true);

        // Add listeners to enable the button only when fields are filled
        deliveryAddressField.textProperty().addListener((observable, oldValue, newValue) -> checkForm());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> checkForm());
    }

    private void loadSummary() {
        try {
            // Fetch cart items for the logged-in user (Replace with actual user ID logic)
            List<CartItem> cartItems = cartService.getCartItemsByUserId(1L);
            updateSummaryView(cartItems);
        } catch (Exception e) {
            showAlert("Error", "Failed to load order summary: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateSummaryView(List<CartItem> cartItems) {
        summaryPane.getChildren().clear(); // Clear the current summary view
        double totalPrice = 0.0;

        for (CartItem cartItem : cartItems) {
            VBox itemBox = new VBox(10);

            // Fetch product details
            Product product = productService.getProductById(cartItem.getProductId());

            // Display product information
            Label productName = new Label("Product: " + product.getName());
            Label productPrice = new Label("Price: $" + product.getPrice());
            totalPrice += product.getPrice();

            itemBox.getChildren().addAll(productName, productPrice);
            summaryPane.getChildren().add(itemBox);
        }

        totalPriceLabel.setText("Total Price: $" + String.format("%.2f", totalPrice));
    }

    private void checkForm() {
        boolean isFormValid = !deliveryAddressField.getText().isEmpty() && !emailField.getText().isEmpty();
        placeOrderButton.setDisable(!isFormValid);
    }

    @FXML
    private void placeOrder() {
        String deliveryAddress = deliveryAddressField.getText();
        String email = emailField.getText();

        // Here, you could send the order details to the backend
        showAlert("Success", "Order placed successfully!\nDelivery Address: " + deliveryAddress + "\nEmail: " + email, Alert.AlertType.INFORMATION);

        // Optionally, redirect to a new screen or clear the cart
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
}
