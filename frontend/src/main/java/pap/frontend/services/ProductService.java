package pap.frontend.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pap.frontend.models.Category;
import pap.frontend.models.Product;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Product deleted successfully");
            } else {
                System.err.println("Failed to delete product. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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

            if (response.statusCode() != 200) {
                throw new RuntimeException("Error: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to add product: " + e.getMessage());
        }
    }

    public void addCategory(Category category) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_API_URL + "/category/add"))
                    .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(category)))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Error: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to add category: " + e.getMessage());
        }
    }

    public void deleteCategory(Long categoryId) {
        String url = BASE_API_URL + "/category/delete/" + categoryId;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Category deleted successfully");
            } else {
                System.err.println("Failed to delete category. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateCategory(Long categoryId, String name) {
        try {
            // Tworzenie URL-a endpointu z ID kategorii
            String url = BASE_API_URL + "/category/update/" + categoryId;

            // Tworzenie treści żądania (parametry w formacie form-urlencoded)
            String requestBody = name != null ? "name=" + URLEncoder.encode(name, StandardCharsets.UTF_8) : "";

            // Tworzenie żądania PUT
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody)) // PUT wymaga treści
                    .header("Content-Type", "application/x-www-form-urlencoded") // Zgodnie z backendem
                    .build();

            // Wysyłanie żądania i odbieranie odpowiedzi
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Debug: Logowanie szczegółów
            System.out.println("Request URL: " + url);
            System.out.println("Request Body: " + requestBody);
            System.out.println("Response Status: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            // Sprawdzanie kodu odpowiedzi
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to update category: " + response.body());
            }
        } catch (Exception e) {
            // Debug: Informacje o błędzie
            System.err.println("Error during updateCategory: " + e.getMessage());
            throw new RuntimeException("Error while updating category: " + e.getMessage());
        }
    }



}
