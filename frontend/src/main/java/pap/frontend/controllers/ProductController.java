package pap.frontend.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pap.frontend.models.Category;
import pap.frontend.models.Product;
import pap.frontend.services.ProductService;

import java.util.List;
import java.util.***REMOVED***.Collectors;

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
            showDeleteProductConfirmationDialog(product);
        });

        // Product Card
        VBox productCard = new VBox(10, imageView, nameText, descriptionText, priceText, showDetailsButton, deleteButton);
        productCard.getStyleClass().add("product-card");
        productCard.setPrefWidth(200);

        return productCard;
    }

    private void showDeleteProductConfirmationDialog(Product product) {
        // Tworzymy okno dialogowe
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Confirm Deletion");

        // Uk≈***REMOVED***ad okna
        VBox dialogVBox = new VBox(10);
        dialogVBox.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        // Dodaj tekst z potwierdzeniem
        Label confirmationLabel = new Label("Are you sure you want to delete the product: " + product.getName() + "?");

        // Przyciski potwierdzenia
        Button yesButton = new Button("Yes");
        yesButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        yesButton.setOnAction(event -> {
            try {
                // Usu≈Ñ produkt
                productService.deleteProduct(product.getId());

                // Od≈õwie≈º listƒô produkt√≥w
                loadProducts();

                // Wy≈õwietl komunikat o sukcesie
                showAlert("Success", "Product deleted successfully.", Alert.AlertType.INFORMATION);

                // Zamknij okno dialogowe
                dialogStage.close();
            } catch (Exception e) {
                showAlert("Error", "Failed to delete product: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Button noButton = new Button("No");
        noButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        noButton.setOnAction(event -> dialogStage.close());

        // Uk≈***REMOVED***ad przycisk√≥w
        HBox buttonLayout = new HBox(10, yesButton, noButton);
        buttonLayout.setAlignment(Pos.CENTER);

        dialogVBox.getChildren().addAll(confirmationLabel, buttonLayout);

        // Ustawienie sceny i pokazanie okna
        Scene scene = new Scene(dialogVBox, 400, 150);
        dialogStage.setScene(scene);
        dialogStage.show();
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
        detailsStage.setTitle("Szczeg√≥≈***REMOVED***y Produktu");

        // Tytu≈***REMOVED*** produktu
        Label titleLabel = new Label(product.getName());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Powiƒôkszony obraz produktu
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

        // Dostƒôpna ilo≈õƒá
        Label quantityLabel = new Label("Quantity available: " + product.getQuantity());
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        // OkrƒÖg≈***REMOVED***y przycisk zamykania
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

        // G≈***REMOVED***√≥wny uk≈***REMOVED***ad
        VBox detailsLayout = new VBox(20);
        detailsLayout.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-spacing: 20; -fx-background-color: #f9f9f9;");

        // Dodanie element√≥w do g≈***REMOVED***√≥wnego uk≈***REMOVED***adu
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

        // Obs≈***REMOVED***uga przycisku "Submit"
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

                // Wysy≈***REMOVED***anie produktu do backendu
                productService.addProduct(newProduct);

                // Informacja o sukcesie
                showAlert("Success", "Product added successfully.", Alert.AlertType.INFORMATION);

                // Zamkniƒôcie okna
                stage.close();

                // Od≈õwie≈ºenie katalogu
                loadProducts();
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid number format in price or quantity fields.", Alert.AlertType.ERROR);
            } catch (IllegalArgumentException e) {
                showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Error", "Failed to add product. Please check your input and try again.", Alert.AlertType.ERROR);
            }
        });

        // Obs≈***REMOVED***uga przycisku "Cancel"
        cancelButton.setOnAction(event -> stage.close());

        // Uk≈***REMOVED***ad formularza
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

    @FXML
    private void openManageCategoriesForm() {
        // Tworzenie nowego okna
        Stage stage = new Stage();
        VBox mainLayout = new VBox(10); // G≈***REMOVED***√≥wny uk≈***REMOVED***ad
        mainLayout.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        // Przyciski: dodawanie kategorii
        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");

        // Lista kategorii
        VBox categoriesListLayout = new VBox(10); // Uk≈***REMOVED***ad dla listy kategorii
        categoriesListLayout.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1;");

        // Obs≈***REMOVED***uga przycisku "Add Category"
        addCategoryButton.setOnAction(event -> {
            openAddCategoryForm(() -> loadCategoriesList(categoriesListLayout)); // Wywo≈***REMOVED***anie formularza dodawania z callbackiem
        });

        // Za≈***REMOVED***adowanie listy kategorii do uk≈***REMOVED***adu
        loadCategoriesList(categoriesListLayout);

        // Dodanie element√≥w do g≈***REMOVED***√≥wnego uk≈***REMOVED***adu
        mainLayout.getChildren().addAll(
                new Label("Manage Categories"),
                addCategoryButton,
                categoriesListLayout
        );

        // Konfiguracja sceny i okna
        Scene scene = new Scene(mainLayout, 500, 600);
        stage.setScene(scene);
        stage.setTitle("Manage Categories");
        stage.show();
    }

    private void loadCategoriesList(VBox categoriesListLayout) {
        categoriesListLayout.getChildren().clear(); // Czy≈õƒá istniejƒÖce elementy

        try {
            // Pobierz listƒô kategorii z backendu
            List<Category> categories = productService.getCategories();

            for (Category category : categories) {
                HBox categoryItem = new HBox(10);
                categoryItem.setStyle("-fx-padding: 5; -fx-border-color: #ddd; -fx-border-width: 1; -fx-background-color: #f6f6f6;");

                Label categoryName = new Label(category.getName());
                categoryName.setStyle("-fx-font-size: 14px;");

                Button editButton = new Button("Edit");
                editButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                editButton.setOnAction(event -> editCategory(category, categoriesListLayout));


                Button deleteButton = new Button("Delete");
                deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    showDeleteConfirmationDialog(category, categoriesListLayout);
                });

                categoryItem.getChildren().addAll(categoryName, editButton, deleteButton);
                categoriesListLayout.getChildren().add(categoryItem);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load categories: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showDeleteConfirmationDialog(Category category, VBox categoriesListLayout) {
        // Pobierz produkty przypisane do tej kategorii
        List<Product> productsInCategory = productService.getProductsByCategoryId(category.getId());

        // Tworzenie okna dialogowego
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Confirm Deletion");

        // Uk≈***REMOVED***ad okna
        VBox dialogVBox = new VBox(10);
        dialogVBox.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        // Dodaj tekst z potwierdzeniem
        Label confirmationLabel = new Label("Are you sure you want to delete the category: " + category.getName() + "?");
        Label productsLabel = new Label("The following products will also be deleted:");

        // Lista produkt√≥w, kt√≥re zostanƒÖ usuniƒôte
        VBox productListLayout = new VBox(5);
        for (Product product : productsInCategory) {
            Label productLabel = new Label(product.getName());
            productListLayout.getChildren().add(productLabel);
        }

        // Przyciski potwierdzenia
        Button yesButton = new Button("Yes");
        yesButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        yesButton.setOnAction(event -> {
            try {
                // Najpierw usu≈Ñ produkty z tej kategorii
                for (Product product : productsInCategory) {
                    productService.deleteProduct(product.getId());
                }

                // Teraz usu≈Ñ kategoriƒô
                productService.deleteCategory(category.getId());

                // Informacja o sukcesie
                showAlert("Success", "Category and products deleted successfully.", Alert.AlertType.INFORMATION);

                // Zamknij okno
                dialogStage.close();

                // Od≈õwie≈º listƒô kategorii
                loadCategoriesList(categoriesListLayout);
                loadCategories(); // Od≈õwie≈ºenie listy kategorii w Product Catalog
                loadProducts();
            } catch (Exception e) {
                showAlert("Error", "Failed to delete category and products: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Button noButton = new Button("No");
        noButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        noButton.setOnAction(event -> dialogStage.close());

        // Uk≈***REMOVED***ad przycisk√≥w
        HBox buttonLayout = new HBox(10, yesButton, noButton);

        dialogVBox.getChildren().addAll(confirmationLabel, productsLabel, productListLayout, buttonLayout);

        // Ustawienie sceny i pokazanie okna
        Scene scene = new Scene(dialogVBox, 400, 300);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    private void openAddCategoryForm(Runnable onCategoryAdded) {
        // Tworzenie nowego okna
        Stage stage = new Stage();
        VBox formLayout = new VBox(10);
        formLayout.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        // Pola formularza
        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        // Przyciski
        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

        // Obs≈***REMOVED***uga przycisku "Submit"
        submitButton.setOnAction(event -> {
            try {
                // Pobieranie danych z formularza
                String name = nameField.getText();

                // Tworzenie nowej kategorii
                Category newCategory = new Category();
                newCategory.setName(name);

                // Wysy≈***REMOVED***anie produktu do backendu
                productService.addCategory(newCategory);
                loadCategories(); // Od≈õwie≈ºenie listy kategorii w Product Catalog

                // Informacja o sukcesie
                showAlert("Success", "Category added successfully.", Alert.AlertType.INFORMATION);

                // Zamkniƒôcie okna
                stage.close();

                // Wywo≈***REMOVED***anie callbacka
                if (onCategoryAdded != null) {
                    onCategoryAdded.run();
                }
            } catch (IllegalArgumentException e) {
                showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Error", "Failed to add category. Please check your input and try again.", Alert.AlertType.ERROR);
            }
        });

        // Obs≈***REMOVED***uga przycisku "Cancel"
        cancelButton.setOnAction(event -> stage.close());

        // Uk≈***REMOVED***ad formularza
        formLayout.getChildren().addAll(
                new Label("Add New Category"),
                nameField,
                new HBox(10, submitButton, cancelButton)
        );

        // Konfiguracja okna
        Scene scene = new Scene(formLayout, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Add Category");
        stage.show();
    }

    private void editCategory(Category category, VBox categoriesListLayout) {
        // Tworzenie okna do edycji
        Stage editStage = new Stage();
        VBox editLayout = new VBox(10);
        editLayout.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        TextField nameField = new TextField(category.getName());
        nameField.setPromptText("Category Name");

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        saveButton.setOnAction(event -> {
            try {
                String updatedName = nameField.getText().trim();
                if (updatedName.isEmpty()) {
                    throw new IllegalArgumentException("Category name cannot be empty.");
                }

                productService.updateCategory(category.getId(), updatedName);

                showAlert("Success", "Category updated successfully.", Alert.AlertType.INFORMATION);
                loadCategoriesList(categoriesListLayout); // Od≈õwie≈ºenie listy
                loadCategories(); // Od≈õwie≈ºenie listy kategorii w Product Catalog
                editStage.close();
            } catch (Exception e) {
                showAlert("Error", "Failed to update category: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        cancelButton.setOnAction(event -> editStage.close());

        editLayout.getChildren().addAll(new Label("Edit Category"), nameField, new HBox(10, saveButton, cancelButton));

        Scene editScene = new Scene(editLayout, 300, 200);
        editStage.setScene(editScene);
        editStage.setTitle("Edit Category");
        editStage.show();
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
