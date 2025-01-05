package pap.frontend.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import pap.frontend.controllers.ControlledScreen;
import pap.frontend.controllers.ScreenController;
import pap.frontend.models.Category;
import pap.frontend.models.Product;
import pap.frontend.services.CategoryService;
import pap.frontend.services.ProductService;

import java.util.List;
import java.util.***REMOVED***.Collectors;

public class UserProductController implements ControlledScreen {

    @FXML
    private TilePane productTilePane;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    private ScreenController screenController;

    @FXML
    public void initialize() {
        productTilePane.getStyleClass().add("tile-pane");
        searchField.getStyleClass().add("text-field");

        loadCategories();
        loadProducts();

        // Enable live search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSuggestions(newValue);
        });
    }

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
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
            screenController.activate("roleSelection");
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
        productTilePane.getChildren().clear(); // Clear previous products
        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productTilePane.getChildren().add(productCard);
        }
    }

    private VBox createProductCard(Product product) {
        // Image
        ImageView imageView = new ImageView();
        try {
            String imageUrl = product.getImageUrl();
            Image image = new Image(imageUrl);
            imageView.setImage(image);
        } catch (Exception e) {
            System.out.println("Failed to load product image: " + product.getName());
        }
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Text elements
        Text nameText = new Text(product.getName());
        Text descriptionText = new Text(product.getDescription());
        descriptionText.setWrappingWidth(150);
        Text priceText = new Text(String.format("$%.2f", product.getPrice()));

        // "Show Details" button
        Button showDetailsButton = new Button("Show Details");
        showDetailsButton.setOnAction(event -> showProductDetails(product));

        VBox productCard = new VBox(10, imageView, nameText, descriptionText, priceText, showDetailsButton);
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
        detailsStage.setTitle("Szczegó�***REMOVED***y Produktu");

        // Tytu�***REMOVED*** produktu
        Label titleLabel = new Label(product.getName());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Powiększony obraz produktu
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

        // Opis produktu
        Label descriptionLabel = new Label("Description: " + product.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        // Cena produktu
        Label priceLabel = new Label(String.format("Price: $%.2f", product.getPrice()));
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #008000;");

        // Kategoria produktu
        Label categoryLabel = new Label("Category: " + (product.getCategory() != null ? product.getCategory().getName() : "N/A"));
        categoryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        // Dostępna ilość
        Label quantityLabel = new Label("Quantity available: " + product.getQuantity());
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        // Okrąg�***REMOVED***y przycisk zamykania
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent; -fx-border-color: #ff4d4f; -fx-text-fill: #ff4d4f;");
        closeButton.setOnAction(event -> detailsStage.close());

        VBox detailsLayout = new VBox(20, titleLabel, imageView, descriptionLabel, priceLabel, categoryLabel, quantityLabel, closeButton);
        detailsLayout.setPadding(new Insets(20));
        Scene scene = new Scene(detailsLayout, 450, 650);
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    @FXML
    private void updateTable() {
        loadProducts();
    }
}
