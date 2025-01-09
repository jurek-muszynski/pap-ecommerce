package pap.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import pap.frontend.models.CartItem;
import pap.frontend.models.Order;
import pap.frontend.models.Product;
import pap.frontend.services.AuthService;
import pap.frontend.services.CartService;
import pap.frontend.services.OrderService;
import pap.frontend.services.ProductService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

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
    private final OrderService orderService = new OrderService();

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

        refreshData();

        // Disable Place Order button by default
        placeOrderButton.setDisable(true);

        // Add listeners to enable the button only when fields are filled
        deliveryAddressField.textProperty().addListener((observable, oldValue, newValue) -> checkForm());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> checkForm());
    }

    public void refreshData() {
        checkAuthentication();

        if (authService.isAuthenticated()){
            loadSummary();
        }
    }

    private void loadSummary() {
        try {
            Long userId = authService.getCurrentUserId();
            List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);
            updateSummaryView(cartItems);
        } catch (Exception e) {
            showAlert("Error", "Failed to load order summary: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateSummaryView(List<CartItem> cartItems) {
        summaryPane.getChildren().clear();
        double totalPrice = 0.0;

        for (CartItem cartItem : cartItems) {
            VBox itemBox = new VBox(10);

            Product product = productService.getProductById(cartItem.getProductId());

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
        Long userId = authService.getCurrentUserId();
        try {
            boolean success = orderService.placeOrder(userId, email, deliveryAddress);

            if (success) {
                showAlert("Success", "Order placed successfully! Confirmation email sent.", Alert.AlertType.INFORMATION);

                if (screenController != null) {
                    screenController.activate("userView");
                }
            } else {
                showAlert("Error", "Failed to place order. Please try again.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
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
