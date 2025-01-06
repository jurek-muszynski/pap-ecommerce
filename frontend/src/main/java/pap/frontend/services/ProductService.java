package pap.frontend.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pap.frontend.models.Category;
import pap.frontend.models.Product;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ProductService {

    private static final String BASE_API_URL = "http://localhost:8080/api/v1";
    private final HttpClient httpClient;
    private final Gson gson;

    public ProductService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<Product> getProducts() {
        return fetchFromApi(BASE_API_URL + "/product/all", new TypeToken<List<Product>>() {}.getType());
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return fetchFromApi(BASE_API_URL + "/product/allWithCategoryId/" + categoryId, new TypeToken<List<Product>>() {}.getType());
    }

    public Product getProductByName(String productName) {
        return fetchFromApi(BASE_API_URL + "/product/withName/" + productName, Product.class);
    }

    public Product getProductById(Long productId) {
        return fetchFromApi(BASE_API_URL + "/product/get/" + productId, Product.class);
    }

    public List<Category> getCategories() {
        return fetchFromApi(BASE_API_URL + "/category/all", new TypeToken<List<Category>>() {}.getType());
    }

    private <T> T fetchFromApi(String url, java.lang.reflect.Type type) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return gson.fromJson(response.body(), type);
            } else {
                System.err.println("Failed to fetch data. Status code: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteProduct(Long productId) {
        String url = BASE_API_URL + "/product/delete/" + productId;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException(response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("ERROR WHILE DELETING PRODUCT: " + e.getMessage());
        }
    }

    public void addProduct(Product product) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_API_URL + "/product/add"))
                    .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(product)))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpURLConnection.HTTP_OK && response.statusCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException(response.body());
            }

        } catch (Exception e) {
            throw new RuntimeException("ERROR ADDING PRODUCT: " + e.getMessage());
        }
    }

}
