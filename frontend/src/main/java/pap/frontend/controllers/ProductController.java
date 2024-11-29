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
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        categoryColumn.setCellValueFactory(cellData -> {
            int categoryId = cellData.getValue().getCategoryId();
            String categoryName = categoryComboBox.getItems().***REMOVED***()
                    .filter(category -> category.getId() == categoryId)
                    .map(Category::getName)
                    .findFirst()
                    .orElse("Unknown");
            return new javafx.beans.property.SimpleStringProperty(categoryName);
        });
        loadProducts();
        loadCategories();
    }

    private void loadProducts() {
        List<Product> products = productService.getProducts();
        updateTable(products);
    }

    private void loadCategories() {
        List<Category> categories = productService.getCategories();
        ObservableList<Category> categoryList = FXCollections.observableArrayList(categories);
        categoryComboBox.setItems(categoryList);
    }

    @FXML
    private void searchByName() {
        String searchQuery = searchField.getText().trim();
        if (!searchQuery.isEmpty()) {
            Product product = productService.getProductByName(searchQuery);
            if (product != null) {
                updateTable(List.of(product));
            } else {
                updateTable(List.of());
                System.out.println("No product found with the name: " + searchQuery);
            }
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
