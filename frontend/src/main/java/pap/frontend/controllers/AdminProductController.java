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
import pap.frontend.services.*;

import java.util.List;
import java.util.***REMOVED***.Collectors;

public class AdminProductController extends AuthenticatedController{

    @FXML
    private TilePane productTilePane;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    @FXML
    private Button decreaseButton;

    @FXML
    private Button increaseButton;

    @FXML
    private Label quantityLabel;

    private Product selectedProduct;

    @FXML
    private Button saveButton;


    public AdminProductController() {
        super(AuthService.getInstance());
    }

    private ScreenController screenController;

    @FXML
    public void initialize() {
        productTilePane.getStyleClass().add("tile-pane");
        searchField.getStyleClass().add("text-field");
        categoryComboBox.getStyleClass().add("combo-box");

        refreshData();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSuggestions(newValue);
        });
        hideQuantityControls();
    }

    public void refreshData() {
        checkAuthentication();

        if (authService.isAuthenticated()) {
            loadCategories();
            loadProducts();
        }
    }

    private void hideQuantityControls() {
        decreaseButton.setVisible(false);
        increaseButton.setVisible(false);
        quantityLabel.setVisible(false);
        saveButton.setVisible(false);
    }

    private void showQuantityControls() {
        decreaseButton.setVisible(true);
        increaseButton.setVisible(true);
        quantityLabel.setVisible(true);
        saveButton.setVisible(true);
    }


    @FXML
    private void decreaseQuantity() {
        if (selectedProduct != null && selectedProduct.getQuantity() > 0) {
            selectedProduct.setQuantity(selectedProduct.getQuantity() - 1);
            quantityLabel.setText("Quantity: " + selectedProduct.getQuantity());
            productService.updateProductQuantity(selectedProduct.getId(), selectedProduct.getQuantity());
        }
    }

    @FXML
    private void increaseQuantity() {
        if (selectedProduct != null) {
            selectedProduct.setQuantity(selectedProduct.getQuantity() + 1);
            quantityLabel.setText("Quantity: " + selectedProduct.getQuantity());
            productService.updateProductQuantity(selectedProduct.getId(), selectedProduct.getQuantity());
        }
    }

    @FXML
    private void saveQuantity() {
        if (selectedProduct != null) {
            try {
                productService.updateProductQuantity(selectedProduct.getId(), selectedProduct.getQuantity());
                showAlert("Success", "Product quantity updated successfully.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Error", "Failed to update product quantity: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
        hideQuantityControls();
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
        productTilePane.getChildren().clear();
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

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #FF4D4F; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> {
            showDeleteProductConfirmationDialog(product);
        });

        VBox productCard = new VBox(10, imageView, nameText, descriptionText, priceText, showDetailsButton, deleteButton);
        productCard.getStyleClass().add("product-card");
        productCard.setPrefWidth(200);

        return productCard;
    }

    private void showDeleteProductConfirmationDialog(Product product) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Confirm Deletion");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        Label confirmationLabel = new Label("Are you sure you want to delete the product: " + product.getName() + "?");

        Button yesButton = new Button("Yes");
        yesButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        yesButton.setOnAction(event -> {
            try {

                productService.deleteProduct(product.getId());

                loadProducts();

                showAlert("Success", "Product deleted successfully.", Alert.AlertType.INFORMATION);

                dialogStage.close();
            } catch (Exception e) {
                showAlert("Error", "Failed to delete product: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Button noButton = new Button("No");
        noButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        noButton.setOnAction(event -> dialogStage.close());

        HBox buttonLayout = new HBox(10, yesButton, noButton);
        buttonLayout.setAlignment(Pos.CENTER);

        dialogVBox.getChildren().addAll(confirmationLabel, buttonLayout);

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

        selectedProduct = product;
        quantityLabel.setText("Quantity: " + product.getQuantity());
        showQuantityControls();

        Stage detailsStage = new Stage();
        detailsStage.setTitle("Szczeg처�***REMOVED***y Produktu");

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


    @FXML
    private void openAddProductForm() {
        Stage stage = new Stage();
        VBox formLayout = new VBox(10);
        formLayout.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

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

        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

        submitButton.setOnAction(event -> {
            try {
                String name = nameField.getText();
                String description = descriptionField.getText();
                String imageUrl = imageUrlField.getText();
                Double price = Double.parseDouble(priceField.getText());
                Integer quantity = Integer.parseInt(quantityField.getText());
                Category category = categoryComboBox.getValue();

                if (category == null) {
                    throw new IllegalArgumentException("Category must be selected.");
                }

                Product newProduct = new Product();
                newProduct.setName(name);
                newProduct.setDescription(description);
                newProduct.setImageUrl(imageUrl);
                newProduct.setPrice(price);
                newProduct.setQuantity(quantity);
                newProduct.setCategory(category);

                productService.addProduct(newProduct);

                showAlert("Success", "Product added successfully.", Alert.AlertType.INFORMATION);

                stage.close();

                loadProducts();
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid number format in price or quantity fields.", Alert.AlertType.ERROR);
            } catch (RuntimeException e) {
                showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                showAlert("Error", "Failed to add product. Please check your input and try again.", Alert.AlertType.ERROR);
            }
        });

        cancelButton.setOnAction(event -> stage.close());

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

        Scene scene = new Scene(formLayout, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Add Product");
        stage.show();
    }

    @FXML
    private void openManageCategoriesForm() {
        Stage stage = new Stage();
        VBox mainLayout = new VBox(10); // G�***REMOVED***처wny uk�***REMOVED***ad
        mainLayout.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");

        VBox categoriesListLayout = new VBox(10); // Uk�***REMOVED***ad dla listy kategorii
        categoriesListLayout.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1;");

        addCategoryButton.setOnAction(event -> {
            openAddCategoryForm(() -> loadCategoriesList(categoriesListLayout)); // Wywo�***REMOVED***anie formularza dodawania z callbackiem
        });


        loadCategoriesList(categoriesListLayout);

        mainLayout.getChildren().addAll(
                new Label("Manage Categories"),
                addCategoryButton,
                categoriesListLayout
        );

        Scene scene = new Scene(mainLayout, 500, 600);
        stage.setScene(scene);
        stage.setTitle("Manage Categories");
        stage.show();
    }

    private void loadCategoriesList(VBox categoriesListLayout) {
        categoriesListLayout.getChildren().clear();

        try {
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
        List<Product> productsInCategory = productService.getProductsByCategoryId(category.getId());

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Confirm Deletion");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        if (productsInCategory.isEmpty()){
            Label confirmationLabel = new Label("Are you sure you want to delete the category: " + category.getName() + "?");

            Button yesButton = new Button("Yes");
            yesButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
            yesButton.setOnAction(event -> {
                try {

                    categoryService.deleteCategory(category.getId());

                    showAlert("Success", "Category deleted successfully.", Alert.AlertType.INFORMATION);

                    dialogStage.close();

                    loadCategoriesList(categoriesListLayout);
                    loadCategories();
                    loadProducts();
                } catch (Exception e) {
                    showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
                }
            });

            Button noButton = new Button("No");
            noButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
            noButton.setOnAction(event -> dialogStage.close());

            HBox buttonLayout = new HBox(10, yesButton, noButton);

            dialogVBox.getChildren().addAll(confirmationLabel, buttonLayout);

            Scene scene = new Scene(dialogVBox);
            dialogStage.setScene(scene);
            dialogStage.sizeToScene();
            dialogStage.show();

        } else {
            Label confirmationLabel = new Label("If you want to delete the category " + category.getName() + " the following products have to be deleted first: ");

            VBox productListLayout = new VBox(5);
            for (Product product : productsInCategory) {
                Label productLabel = new Label(product.getName());
                productListLayout.getChildren().add(productLabel);
            }

            Button closeButton = new Button("Close");
            closeButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white;");
            closeButton.setOnAction(event -> dialogStage.close());

            HBox buttonLayout = new HBox(10, closeButton);

            dialogVBox.getChildren().addAll(confirmationLabel, productListLayout, buttonLayout);

            Scene scene = new Scene(dialogVBox);
            dialogStage.setScene(scene);
            dialogStage.sizeToScene();
            dialogStage.show();
        }
    }

    private void openAddCategoryForm(Runnable onCategoryAdded) {

        Stage stage = new Stage();
        VBox formLayout = new VBox(10);
        formLayout.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

        submitButton.setOnAction(event -> {
            try {
                String name = nameField.getText();


                Category newCategory = new Category();
                newCategory.setName(name);

                categoryService.addCategory(newCategory);
                loadCategories();

                showAlert("Success", "Category added successfully.", Alert.AlertType.INFORMATION);

                stage.close();

                if (onCategoryAdded != null) {
                    onCategoryAdded.run();
                }
            } catch (IllegalArgumentException e) {
                showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Error", "Failed to add category. Please check your input and try again.", Alert.AlertType.ERROR);
            }
        });

        cancelButton.setOnAction(event -> stage.close());

        formLayout.getChildren().addAll(
                new Label("Add New Category"),
                nameField,
                new HBox(10, submitButton, cancelButton)
        );

        Scene scene = new Scene(formLayout, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Add Category");
        stage.show();
    }

    private void editCategory(Category category, VBox categoriesListLayout) {
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

                categoryService.updateCategory(category.getId(), updatedName);

                showAlert("Success", "Category updated successfully.", Alert.AlertType.INFORMATION);
                loadCategoriesList(categoriesListLayout);
                loadCategories();
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
