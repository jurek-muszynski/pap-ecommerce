package pap.frontend.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pap.frontend.models.Category;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CategoryService {

    private static final String BASE_API_URL = "http://localhost:8080/api/v1";
    private final HttpClient httpClient;
    private final Gson gson;
    private final AuthService authService;

    public CategoryService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.authService = AuthService.getInstance();
    }

    public List<Category> getCategories() {
        return fetchFromApi(BASE_API_URL + "/category/all", new TypeToken<List<Category>>() {}.getType());
    }

    public void addCategory(Category category) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_API_URL + "/category/add"))
                    .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(category)))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpURLConnection.HTTP_OK && response.statusCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException(response.body());
            }

        } catch (Exception e) {
            throw new RuntimeException("ERROR ADDING CATEGORY: " + e.getMessage());
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

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException(response.body());
            }

        } catch (Exception e) {
            throw new RuntimeException("ERROR WHILE DELETING CATEGORY\n" + e.getMessage());
        }
    }

    public void updateCategory(Long categoryId, String name) {
        try {
            String url = BASE_API_URL + "/category/update/" + categoryId;

            String requestBody = name != null ? "name=" + URLEncoder.encode(name, StandardCharsets.UTF_8) : "";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException(response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("ERROR WHILE UPDATING CATEGORY: " + e.getMessage());
        }
    }

    private <T> T fetchFromApi(String url, java.lang.reflect.Type type) {
        try {
            String token = authService.getToken();
            if (token == null) {
                throw new RuntimeException("No token found. User might not be authenticated.");
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return gson.fromJson(response.body(), type);
            } else {
                System.err.println("Failed to fetch data. Status code: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
