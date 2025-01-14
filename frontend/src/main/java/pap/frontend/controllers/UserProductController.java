package pap.frontend.controllers;

import com.sun.mail.smtp.SMTPOutputStream;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pap.frontend.models.Cart;
import pap.frontend.models.CartItem;
import pap.frontend.models.Category;
import pap.frontend.models.Product;
import pap.frontend.services.*;

import java.util.List;
import java.util.Optional;
import java.util.***REMOVED***.Collectors;

public class UserProductController extends AuthenticatedController {

    @FXML
    private TilePane productTilePane;

    @FXML
    private VBox cartPane;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();
    private final CartService cartService = new CartService();
    private final ReviewService reviewService = new ReviewService();

    private ScreenController screenController;

    public UserProductController(){
        super(AuthService.getInstance());
    }

    @FXML
    public void initialize() {
        productTilePane.getStyleClass().add("tile-pane");
        searchField.getStyleClass().add("text-field");

        refreshData();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSuggestions(newValue);
        });
    }

    public void refreshData() {
        checkAuthentication();

        if (authService.isAuthenticated()) {
            loadCategories();
            loadProducts();
        }
    }

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML
    private void openAccountManagement() {
        if (screenController != null) {
            screenController.activate("accountManagement");
        }
    }

    @FXML
    private void openCart() {
        if (screenController != null) {
            screenController.activate("cartView");
        }
    }

    private void loadProducts() {
        List<Product> products = productService.getProducts();
        updateProductTiles(products);
    }

    private void loadCategories() {
        List<Category> categories = categoryService.getCategories();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
    }

    @FXML
    private void goBackToRoleSelection() {
        if (screenController != null) {
            screenController.activate("login");
        }
    }

    @FXML
    private void searchByName() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            List<Product> filteredProducts = productService.getProducts().***REMOVED***()
                    .filter(product -> product.getName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            updateProductTiles(filteredProducts);
        } else {
            updateProductTiles(productService.getProducts());
        }
    }

    @FXML
    private void filterByCategory() {
        Category selectedCategory = categoryComboBox.getValue();
        if (selectedCategory != null) {
            List<Product> filteredProducts = productService.getProductsByCategoryId(selectedCategory.getId());
            updateProductTiles(filteredProducts);
        }
    }

    @FXML
    private void updateProductTiles(List<Product> products) {
        productTilePane.getChildren().clear();
        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productTilePane.getChildren().add(productCard);
        }
    }

    private VBox createProductCard(Product product) {
        ImageView imageView = new ImageView();
        try {
            String imageUrl = product.getImageUrl();
            Image image = new Image(imageUrl);
            imageView.setImage(image);
        } catch (Exception e) {

        }
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("image-view");

        Text nameText = new Text(product.getName());
        nameText.getStyleClass().add("name-text");

        Text descriptionText = new Text(product.getDescription());
        descriptionText.setWrappingWidth(150);
        descriptionText.getStyleClass().add("description-text");

        Text priceText = new Text(String.format("$%.2f", product.getPrice()));
        priceText.getStyleClass().add("price-text");

        Button showDetailsButton = new Button("Show Details");
        showDetailsButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: white;");
        showDetailsButton.setOnAction(event -> showProductDetails(product));

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        addToCartButton.setOnAction(event -> addToCart(product));

        Button showRewiewsButton = new Button("Show Reviews");
        showRewiewsButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: white;");
        showRewiewsButton.setOnAction(event -> showReviews(product));

        // Product Card
        VBox productCard = new VBox(10, imageView, nameText, descriptionText, priceText, showDetailsButton, addToCartButton, showRewiewsButton);
        productCard.getStyleClass().add("product-card");
        productCard.setPrefWidth(200);

        return productCard;
    }

    private void filterSuggestions(String query) {
        if (!query.isEmpty()) {
            List<Product> filteredProducts = productService.getProducts().***REMOVED***()
                    .filter(product -> product.getName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            updateProductTiles(filteredProducts);
        } else {
            updateProductTiles(productService.getProducts());
        }
    }

    private void showProductDetails(Product product) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("SzczegÃ³Å***REMOVED***y Produktu");

        Label titleLabel = new Label(product.getName());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        ImageView imageView = new ImageView();
        try {
            Image image = new Image(product.getImageUrl());
            imageView.setImage(image);
        } catch (Exception e) {
            System.out.println("Failed to load product image: " + product.getName());
        }
        imageView.setFitWidth(300);
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);

        Label descriptionLabel = new Label("Description: " + product.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        Label priceLabel = new Label(String.format("Price: $%.2f", product.getPrice()));
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #008000;");

        Label categoryLabel = new Label("Category: " + (product.getCategory() != null ? product.getCategory().getName() : "N/A"));
        categoryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        Label quantityLabel = new Label("Quantity available: " + product.getQuantity());
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        Button closeButton = new Button("X");
        closeButton.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-border-color: #ff4d4f; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 25px; " +
                        "-fx-background-radius: 25px; " +
                        "-fx-text-fill: #ff4d4f; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand; " +
                        "-fx-padding: 10;"
        );

        closeButton.setOnMouseEntered(event -> {
            closeButton.setStyle(
                    "-fx-background-color: #ff4d4f; " +
                            "-fx-border-color: #ff4d4f; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 25px; " +
                            "-fx-background-radius: 25px; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-cursor: hand; " +
                            "-fx-padding: 10;"
            );
        });

        closeButton.setOnMouseExited(event -> {
            closeButton.setStyle(
                    "-fx-background-color: transparent; " +
                            "-fx-border-color: #ff4d4f; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 25px; " +
                            "-fx-background-radius: 25px; " +
                            "-fx-text-fill: #ff4d4f; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-cursor: hand; " +
                            "-fx-padding: 10;"
            );
        });

        closeButton.setOnAction(event -> detailsStage.close());

        VBox detailsLayout = new VBox(20);
        detailsLayout.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-spacing: 20; -fx-background-color: #f9f9f9;");

        detailsLayout.getChildren().addAll(titleLabel, imageView, descriptionLabel, priceLabel, categoryLabel, quantityLabel, closeButton);

        VBox.setMargin(closeButton, new Insets(20, 0, 30, 0));

        Scene scene = new Scene(detailsLayout, 450, 650);
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private void addToCart(Product product) {
        try {
            Long userId = authService.getCurrentUserId();
            if (userId == null) {
                throw new RuntimeException("User not authenticated. Cannot add items to the cart.");
            }


            CartItem cartItem = new CartItem();
            cartItem.setProductId(product.getId());
            cartItem.setCartId(cartService.getCartByUserId(userId));
            cartItem.setQuantity(1);

            cartService.addCartItem(cartItem);

            showAlert("Success", "Product added to cart successfully.", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void updateTable() {
        loadProducts();
        loadCategories();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showReviews(Product product) {

        Long productId = product.getId();

        // Aktywacja nowego widoku z opiniami
        screenController.activateWithParam("reviewView", productId);
    }


}
