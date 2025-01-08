package pap.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pap.frontend.models.CartItem;
import pap.frontend.models.Product;
import pap.frontend.services.CartService;
import pap.frontend.services.ProductService;

import java.util.List;

public class CartController implements ControlledScreen {
    @FXML
    private VBox cartPane;

    private final CartService cartService = new CartService();
    private final ProductService productService = new ProductService();

    private ScreenController screenController;

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML
    public void initialize() {
        // Load cart items initially
        loadCartItems();

        // Register a listener to refresh the cart view when the cart is updated
    }

    public void loadCartItems() {
        try {
            // Fetch cart items for the logged-in user (Replace with actual user ID logic)
            List<CartItem> cartItems = cartService.getCartItemsByUserId(1L);
            updateCartView(cartItems);
        } catch (Exception e) {
            showAlert("Error", "Failed to load cart items: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateCartView(List<CartItem> cartItems) {
        cartPane.getChildren().clear(); // Clear the current cart view

        for (CartItem cartItem : cartItems) {
            VBox cartItemBox = new VBox(10);

            // Fetch product details
            Product product = productService.getProductById(cartItem.getProductId());

            // Display product information
            Label productName = new Label("Product: " + product.getName());
            Label productPrice = new Label("Price: $" + product.getPrice());

            // Add a remove button for each cart item
            Button removeButton = new Button("Remove");
            removeButton.setOnAction(event -> removeCartItem(cartItem.getId()));

            cartItemBox.getChildren().addAll(productName, productPrice, removeButton);
            cartPane.getChildren().add(cartItemBox);
        }
    }

    private void removeCartItem(Long cartItemId) {
        try {
            cartService.removeCartItem(cartItemId);
            showAlert("Success", "Item removed from cart.", Alert.AlertType.INFORMATION);
            loadCartItems(); // Refresh the cart view
        } catch (Exception e) {
            showAlert("Error", "Failed to remove cart item: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void goBackToProducts() {
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

    @FXML
    private void goToSummary() {
        if (screenController != null) {
            screenController.activate("summaryView"); // Przekierowanie do widoku podsumowania
        }
    }

}
