package pap.frontend.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pap.frontend.models.Review;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ReviewService {

    private static final String BASE_API_URL = "http://localhost:8080/api/v1";
    private final HttpClient httpClient;
    private final Gson gson;
    private final AuthService authService;

    public ReviewService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.authService = AuthService.getInstance();
    }

    public List<Review> getAllReviews() {
        return fetchFromApi(BASE_API_URL + "/review/all", new TypeToken<List<Review>>() {}.getType());
    }

    public Review getReviewById(Long reviewId) {
        return fetchFromApi(BASE_API_URL + "/review/get/" + reviewId, Review.class);
    }

    public List<Review> getReviewsByUserId(Long userId) {
        return fetchFromApi(BASE_API_URL + "/review/allWithUserId/" + userId, new TypeToken<List<Review>>() {}.getType());
    }

    public List<Review> getReviewsByProductId(Long productId) {
        return fetchFromApi(BASE_API_URL + "/review/allWithProductId/" + productId, new TypeToken<List<Review>>() {}.getType());
    }

    public void addReview(Review review) {
        try {
            String token = authService.getToken();
            if (token == null) {
                throw new RuntimeException("No token found. User might not be authenticated.");
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_API_URL + "/review/add"))
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(review)))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpURLConnection.HTTP_OK && response.statusCode() != HttpURLConnection.HTTP_CREATED) {

                throw new RuntimeException(response.body());
            }

        } catch (Exception e) {
            throw new RuntimeException("ERROR ADDING REVIEW: " + e.getMessage());
        }
    }

    public void deleteReview(Long reviewId) {
        try {
            String token = authService.getToken();
            if (token == null) {
                throw new RuntimeException("No token found. User might not be authenticated.");
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .uri(URI.create(BASE_API_URL + "/review/delete/" + reviewId))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException(response.body());
            }

        } catch (Exception e) {
            throw new RuntimeException("ERROR DELETING REVIEW: " + e.getMessage());
        }
    }

    public void deleteReviewsByProductId(Long productId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_API_URL + "/review/deleteAllByProductId/" + productId))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException(response.body());
            }

        } catch (Exception e) {
            throw new RuntimeException("ERROR DELETING REVIEWS BY PRODUCT ID: " + e.getMessage());
        }
    }

    public void deleteReviewsByUserId(Long userId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_API_URL + "/review/deleteAllByUserId/" + userId))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException(response.body());
            }

        } catch (Exception e) {
            throw new RuntimeException("ERROR DELETING REVIEWS BY USER ID: " + e.getMessage());
        }
    }

    public void updateReview(Long reviewId, Integer rate, String description) {
        try {
            String url = BASE_API_URL + "/review/update/" + reviewId;

            StringBuilder requestBody = new StringBuilder();
            if (rate != null) {
                requestBody.append("rate=").append(URLEncoder.encode(rate.toString(), StandardCharsets.UTF_8)).append("&");
            }
            if (description != null) {
                requestBody.append("description=").append(URLEncoder.encode(description, StandardCharsets.UTF_8));
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException(response.body());
            }

        } catch (Exception e) {
            throw new RuntimeException("ERROR UPDATING REVIEW: " + e.getMessage());
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
                throw new RuntimeException("Failed to fetch data. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("ERROR FETCHING DATA: " + e.getMessage());
        }
    }
}
