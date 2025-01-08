package pap.frontend.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pap.frontend.models.User;
import pap.frontend.models.UserRole;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService {

    private static AuthService instance;

    private static final String BASE_API_URL = "http://localhost:8080/api/v1/auth";
    private final HttpClient httpClient;
    private final Gson gson;
    private String token;

    private AuthService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public String login(String email, String password) throws Exception {
        String url = BASE_API_URL + "/login";

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("email", email);
        requestBody.addProperty("password", password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
            this.token = jsonResponse.get("token").getAsString();
            return this.token;
        } else {
            throw new RuntimeException("Login failed: " + response.statusCode() + " - " + response.body());
        }
    }

    public boolean isAuthenticated() {
        return token != null && !token.isEmpty();
    }

    public void saveToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void logout() {
        this.token = null; // Clear the token on logout
    }

    public User getCurrentUser() {
        String url = "http://localhost:8080/api/v1/user/me";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + getToken()) // Attach token
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return gson.fromJson(response.body(), User.class);
            } else {
                throw new RuntimeException("Failed to fetch current user. Status: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error fetching current user: " + e.getMessage());
        }
    }

    public UserRole getUserRole() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/user/me"))
                    .header("Authorization", "Bearer " + getToken())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                // Assuming the response contains a JSON object with a "role" field
                User user = gson.fromJson(response.body(), User.class);
                return user.getRole(); // Assuming User class has a `getRole()` method
            } else {
                throw new RuntimeException("Failed to fetch user role. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch user role: " + e.getMessage(), e);
        }
    }



    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
