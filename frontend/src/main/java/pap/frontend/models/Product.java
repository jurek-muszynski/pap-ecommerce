package pap.frontend.models;

import java.util.Objects;

/**
 * This Java class `Product` represents a model for a product entity. It contains the properties, constructors,
 * getters, setters, and utility methods to work with product data in the application.

 * **Properties:**
 * - `id`: A unique identifier for the product (`Long`).
 * - `name`: The name of the product (`String`).
 * - `price`: The price of the product (`double`).
 * - `imageUrl`: A URL pointing to the product's image (`String`).
 * - `description`: A description of the product (`String`).
 * - `quantity`: The quantity of the product available in stock (`int`).

 * **Constructors:**
 * - A no-argument default constructor (`Product()`).
 * - A parameterized constructor (`Product(Long, String, double, String, String, int)`) to initialize all fields.

 * **Getter and Setter Methods:**
 * - Provide access to and modification of all fields (`getId`, `setId`, etc.).
 * - These methods encapsulate the fields, allowing controlled access to the properties.

 * **Utility Methods:**
 * - `toString()`: Overrides the `Object` class's `toString()` method to provide a string representation of the product's properties.
 * - This is useful for debugging or logging purposes.

 * **Key Features:**
 * - Encapsulation: Fields are private, and access is provided through public getter and setter methods.
 * - Compatibility: The class can be serialized/deserialized (e.g., for JSON data exchange) and used with frameworks like Gson.
 * - Extendable: The design allows for future extensions, such as adding new fields or methods to handle specific product-related logic.
 */

public class Product {

    private Long id;
    private String name;
    private double price;
    private String imageUrl;
    private String description;
    private int quantity;

    public Product() {
    }

    public Product(Long id, String name, double price, String imageUrl, String description, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    // Settery
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}