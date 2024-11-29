package pap.frontend.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pap.frontend.models.Category;
import pap.frontend.models.Product;
import pap.frontend.services.ProductService;

import java.io.IOException;
import java.util.List;
import java.util.***REMOVED***.Collectors;

public class ProductController {

    @FXML
    private BorderPane rootLayout;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, String> descriptionColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    private final ProductService productService = new ProductService();

    private List<Category> categoryList;

    public void startApplication(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/pap/frontend/product_list.fxml"));
            BorderPane root = fxmlLoader.load();
            Scene scene = new Scene(root, 1000, 700);
            primaryStage.setTitle("Product Catalog");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Ustawiamy kolumny w tabeli
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        categoryColumn.setCellValueFactory(cellData -> {
            Category category = cellData.getValue().getCategory();
            String categoryName = category != null ? category.getName() : "Unknown";
            return new javafx.beans.property.SimpleStringProperty(categoryName);
        });

        loadCategories();
        loadProducts();

        // Dodanie listenera do searchField, aby zrealizować filtrowanie na bieżąco
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSuggestions(newValue);
        });
    }

    private void filterSuggestions(String query) {
        if (!query.isEmpty()) {
            List<String> filteredNames = productService.getProducts().***REMOVED***()
                    .map(Product::getName)
                    .filter(name -> name.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    private List<String> getProductNames() {
        return productService.getProducts().***REMOVED***()
                .map(Product::getName)
                .collect(Collectors.toList());
    }

    private void loadProducts() {
        List<Product> products = productService.getProducts();
        updateTable(products);
    }

    private void loadCategories() {
        categoryList = productService.getCategories();
        categoryComboBox.setItems(FXCollections.observableArrayList(categoryList));
    }

    @FXML
    private void searchByName() {
        String searchQuery = searchField.getText().trim();
        if (!searchQuery.isEmpty()) {
            List<Product> filteredProducts = productService.getProducts().***REMOVED***()
                    .filter(product -> product.getName().toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(Collectors.toList());

            if (!filteredProducts.isEmpty()) {
                updateTable(filteredProducts);
            } else {
                updateTable(List.of());  // Jeśli nie ma wyników
            }
        } else {
            // Jeśli nie wpisano żadnego tekstu, pokazujemy wszystkie produkty
            updateTable(productService.getProducts());
        }
    }

    @FXML
    private void filterByCategory() {
        Category selectedCategory = categoryComboBox.getValue();
        if (selectedCategory != null) {
            List<Product> products = productService.getProductsByCategoryId(selectedCategory.getId());
            updateTable(products);
        }
    }

    @FXML
    private void updateTable() {
        loadProducts();
    }

    private void updateTable(List<Product> products) {
        ObservableList<Product> productList = FXCollections.observableArrayList(products);
        productTable.setItems(productList);
        productTable.refresh();
    }
}
