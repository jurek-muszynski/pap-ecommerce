package pap.frontend.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pap.frontend.models.Category;
import pap.frontend.models.Product;
import pap.frontend.services.ProductService;

import java.util.List;
import java.util.stream.Collectors;

public class ProductController {

    @FXML
    private TilePane productTilePane;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    private final ProductService productService = new ProductService();

    @FXML
    public void initialize() {
        // Apply CSS style classes
        productTilePane.getStyleClass().add("tile-pane");
        searchField.getStyleClass().add("text-field");
        categoryComboBox.getStyleClass().add("combo-box");

        loadCategories();
        loadProducts();

        // Enable live search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSuggestions(newValue);
        });
    }

    private void loadProducts() {
        List<Product> products = productService.getProducts();
        updateProductTiles(products);
    }

    private void loadCategories() {
        List<Category> categories = productService.getCategories();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
    }

    @FXML
    private void searchByName() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            List<Product> filteredProducts = productService.getProducts().stream()
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
            System.out.println("Failed to load image for product: " + product.getName());
        }
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("image-view");

        // Text elements
        Text nameText = new Text(product.getName());
        nameText.getStyleClass().add("name-text");

        Text descriptionText = new Text(product.getDescription());
        descriptionText.setWrappingWidth(150);
        descriptionText.getStyleClass().add("description-text");

        Text priceText = new Text(String.format("$%.2f", product.getPrice()));
        priceText.getStyleClass().add("price-text");

        // Buttons
        Button showDetailsButton = new Button("Show Details");
        showDetailsButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: white;");
        showDetailsButton.setOnAction(event -> showProductDetails(product));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #FF4D4F; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> {
            productService.deleteProduct(product.getId());
            loadProducts();
        });

        // Product Card
        VBox productCard = new VBox(10, imageView, nameText, descriptionText, priceText, showDetailsButton, deleteButton);
        productCard.getStyleClass().add("product-card");
        productCard.setPrefWidth(200);

        return productCard;
    }


    private void filterSuggestions(String query) {
        if (!query.isEmpty()) {
            List<Product> filteredProducts = productService.getProducts().stream()
                    .filter(product -> product.getName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            updateProductTiles(filteredProducts);
        } else {
            updateProductTiles(productService.getProducts());
        }
    }

    private void showProductDetails(Product product) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Szczegóły Produktu");

        // Tytuł produktu
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

        // Okrągły przycisk zamykania
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

        // Główny układ
        VBox detailsLayout = new VBox(20);
        detailsLayout.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-spacing: 20; -fx-background-color: #f9f9f9;");

        // Dodanie elementów do głównego układu
        detailsLayout.getChildren().addAll(titleLabel, imageView, descriptionLabel, priceLabel, categoryLabel, quantityLabel, closeButton);

        // Ustawienie marginesu przycisku
        VBox.setMargin(closeButton, new Insets(20, 0, 30, 0));

        Scene scene = new Scene(detailsLayout, 450, 650);
        detailsStage.setScene(scene);
        detailsStage.show();
    }


    @FXML
    private void openAddProductForm() {
        // Tworzenie nowego okna
        Stage stage = new Stage();
        VBox formLayout = new VBox(10);
        formLayout.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        // Pola formularza
        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        TextField imageUrlField = new TextField();
        imageUrlField.setPromptText("Image URL");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        ComboBox<Category> categoryComboBox = new ComboBox<>(FXCollections.observableArrayList(productService.getCategories()));
        categoryComboBox.setPromptText("Select Category");

        // Przyciski
        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

        // Obsługa przycisku "Submit"
        submitButton.setOnAction(event -> {
            try {
                // Pobieranie danych z formularza
                String name = nameField.getText();
                String description = descriptionField.getText();
                String imageUrl = imageUrlField.getText();
                Double price = Double.parseDouble(priceField.getText());
                Integer quantity = Integer.parseInt(quantityField.getText());
                Category category = categoryComboBox.getValue();

                if (category == null) {
                    throw new IllegalArgumentException("Category must be selected.");
                }

                // Tworzenie nowego produktu
                Product newProduct = new Product();
                newProduct.setName(name);
                newProduct.setDescription(description);
                newProduct.setImageUrl(imageUrl);
                newProduct.setPrice(price);
                newProduct.setQuantity(quantity);
                newProduct.setCategory(category);

                // Wysyłanie produktu do backendu
                productService.addProduct(newProduct);

                // Informacja o sukcesie
                showAlert("Success", "Product added successfully.", Alert.AlertType.INFORMATION);

                // Zamknięcie okna
                stage.close();

                // Odświeżenie katalogu
                loadProducts();
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid number format in price or quantity fields.", Alert.AlertType.ERROR);
            } catch (IllegalArgumentException e) {
                showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Error", "Failed to add product. Please check your input and try again.", Alert.AlertType.ERROR);
            }
        });

        // Obsługa przycisku "Cancel"
        cancelButton.setOnAction(event -> stage.close());

        // Układ formularza
        formLayout.getChildren().addAll(
                new Label("Add New Product"),
                nameField,
                descriptionField,
                imageUrlField,
                priceField,
                quantityField,
                categoryComboBox,
                new HBox(10, submitButton, cancelButton)
        );

        // Konfiguracja okna
        Scene scene = new Scene(formLayout, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Add Product");
        stage.show();
    }


    public void updateTable(ActionEvent actionEvent) {
        loadProducts();
        updateProductTiles(productService.getProducts());
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
