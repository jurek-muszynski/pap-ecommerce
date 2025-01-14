package pap.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pap.frontend.models.CartItem;
import pap.frontend.models.Product;
import pap.frontend.services.AuthService;
import pap.frontend.services.CartService;
import pap.frontend.services.ProductService;

import java.text.DecimalFormat;
import java.util.List;

public class CartController extends AuthenticatedController {
    @FXML
    private VBox cartPane;

    private final CartService cartService = new CartService();
    private final ProductService productService = new ProductService();

    private ScreenController screenController;

    public CartController() {
        super(AuthService.getInstance());
    }

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML
    public void initialize() {
        refreshData();

        cartPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(getClass().getResource("/pap/frontend/styles/cartStyles.css").toExternalForm());
            }
        });
    }

    public void loadCartItems() {
        try {
            Long userId = authService.getCurrentUserId();
            List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);
            updateCartView(cartItems);
        } catch (Exception e) {
            showAlert("Error", "Failed to load cart items: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateCartView(List<CartItem> cartItems) {
        cartPane.getChildren().clear();

        for (CartItem cartItem : cartItems) {
            VBox cartItemBox = new VBox(10);
            cartItemBox.getStyleClass().add("cart-item");


            Product product = productService.getProductById(cartItem.getProductId());

            Label productName = new Label("Product: " + product.getName());
            productName.getStyleClass().add("product-name");

            double price = product.getPrice() * cartItem.getQuantity();

            Label productPrice = new Label("Price: $" + new DecimalFormat("#.##").format(price));
            productPrice.getStyleClass().add("product-price");

            HBox quantityBox = new HBox(5);
            quantityBox.getStyleClass().add("quantity-box");

            Button decreaseButton = new Button("-");
            decreaseButton.getStyleClass().add("quantity-button");
            decreaseButton.setOnAction(event -> {
                if (cartItem.getQuantity() > 1) {
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    updateCartItem(cartItem);
                    updateCartView(cartItems);
                }
            });

            Label quantityLabel = new Label(String.valueOf(cartItem.getQuantity()));
            quantityLabel.getStyleClass().add("quantity-label");

            Button increaseButton = new Button("+");
            increaseButton.getStyleClass().add("quantity-button");
            increaseButton.setOnAction(event -> {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                updateCartItem(cartItem);
                updateCartView(cartItems);
            });

            quantityBox.getChildren().addAll(decreaseButton, quantityLabel, increaseButton);

            Button removeButton = new Button("Remove");
            removeButton.getStyleClass().add("remove-button");
            removeButton.setOnAction(event -> removeCartItem(cartItem.getId()));

            cartItemBox.getChildren().addAll(productName, productPrice, quantityBox, removeButton);
            cartPane.getChildren().add(cartItemBox);
        }
    }


    private void removeCartItem(Long cartItemId) {
        try {
            cartService.removeCartItem(cartItemId);
            showAlert("Success", "Item removed from cart.", Alert.AlertType.INFORMATION);
            loadCartItems();
        } catch (Exception e) {
            showAlert("Error", "Failed to remove cart item: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateCartItem(CartItem cartItem) {
        try {
            cartService.updateCartItem(cartItem.getId(), cartItem.getQuantity());
            loadCartItems();
        } catch (Exception e) {
            showAlert("Error", "Failed to update cart item: " + e.getMessage(), Alert.AlertType.ERROR);
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
            screenController.activate("summaryView");

            // Uzyskanie kontrolera widoku podsumowania i wywołanie metody onActivate
            Object controller = screenController.getController("summaryView");
            if (controller instanceof SummaryController) {
                ((SummaryController) controller).onActivate();
            }
        }
    }

    public void refreshData() {
        checkAuthentication();

        if (authService.isAuthenticated()) {
            loadCartItems();
        }
    }
}
