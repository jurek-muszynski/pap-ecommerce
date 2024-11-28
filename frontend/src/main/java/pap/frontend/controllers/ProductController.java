package pap.frontend.controllers;

import pap.frontend.models.Product;
import pap.frontend.services.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
/**
 * The `ProductController` class is the controller for the Product Catalog UI.
 * It manages the interaction between the frontend UI (defined in FXML) and the backend service (`ProductService`).
 * It handles events, sets up the layout, and loads product data into the UI.

 * **Key Components:**
 * 1. **FXML-Injected Fields:**
 *    - `rootLayout`: The main `BorderPane` layout defined in the FXML file.
 *    - `productTable`: A `TableView` to display a list of products.
 *    - `nameColumn`, `descriptionColumn`, `priceColumn`: Columns for product details in the `TableView`.
 *    - These fields are linked to the corresponding elements in the FXML file using `fx:id`.

 * 2. **`ProductService`:**
 *    - An instance of `ProductService` used to fetch product data from the backend API.

 * **Methods:**

 * 1. **`startApplication(Stage stage)`:**
 *    - Responsible for initializing and displaying the UI.
 *    - Loads the FXML layout file, sets up the scene, and displays the application window.

 * 2. **`initialize()`:**
 *    - Called automatically after the FXML file is loaded.
 *    - Configures the `TableView` columns to bind to the `Product` model properties (`name`, `description`, and `price`).
 *    - Calls `setupLayout()` to configure additional UI elements.
 *    - Calls `loadProducts()` to fetch and display product data.

 * 3. **`setupLayout()`:**
 *    - Dynamically creates a horizontal layout (`HBox`) with buttons for user actions:
 *      - `Add Product`: Placeholder for adding a new product.
 *      - `Remove Product`: Placeholder for removing a selected product.
 *      - `Refresh Catalog`: Reloads product data from the backend.
 *    - Adds these buttons to the top of the layout (`setTop`).
 *    - Displays the product table in the right section of the layout (`setRight`).

 * 4. **`loadProducts()`:**
 *    - Fetches a list of products from `ProductService`.
 *    - Converts the list into an `ObservableList` and sets it as the data source for `productTable`.

 * **Use Case:**
 * - This controller is the core of the product catalog UI, responsible for fetching and displaying data, handling user interactions, and dynamically updating the layout.

 * **Extensibility:**
 * - The placeholder buttons (`Add Product`, `Remove Product`) can be linked to additional methods to handle those specific actions.
 * - The layout and functionality can be extended to include features like product search, filtering, or editing.
 */

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

    private final ProductService productService = new ProductService();

    public void startApplication(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/pap/frontend/product_list.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        stage.setTitle("Product Catalog");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        setupLayout();
        loadProducts();
    }

    private void setupLayout() {
        HBox topButtons = new HBox(10);
        Button addButton = new Button("Add Product");
        Button removeButton = new Button("Remove Product");
        Button refreshButton = new Button("Refresh Catalog");
        refreshButton.setOnAction(event -> loadProducts());
        topButtons.getChildren().addAll(addButton, removeButton, refreshButton);
        rootLayout.setTop(topButtons);
        VBox catalogView = new VBox();
        catalogView.getChildren().add(productTable);
        rootLayout.setRight(catalogView);
    }

    private void loadProducts() {
        List<Product> products = productService.getProducts();
        ObservableList<Product> productList = FXCollections.observableArrayList(products);
        productTable.setItems(productList);
    }
}
