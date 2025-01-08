package pap.frontend.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pap.frontend.models.CartItem;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CartService {

    private static final String BASE_API_URL = "http://localhost:8080/api/v1";
    private final HttpClient httpClient;
    private final Gson gson;
    private final AuthService authService;

    public CartService() {
        this.httpClient = HttpClient.newHttpClient();
        gson = new Gson();
        authService = AuthService.getInstance();
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

    public List<CartItem> getCartItems() {
        return fetchFromApi(BASE_API_URL + "/cartItem/all", new TypeToken<List<CartItem>>() {}.getType());
    }

    public List<CartItem> getCartItemsByCartId(Long cartId) {
        return fetchFromApi(BASE_API_URL + "/cartItem/allWithCartId/" + cartId, new TypeToken<List<CartItem>>() {}.getType());
    }

    public List<CartItem> getCartItemsByUserId(Long userId) {
        return fetchFromApi(BASE_API_URL + "/cartItem/allWithUserId/" + userId, new TypeToken<List<CartItem>>() {}.getType());
    }

    public Long getCartByUserId(Long userId) {
        return fetchFromApi(BASE_API_URL + "/cart/byUser/" + userId, new TypeToken<Long>() {}.getType());
    }

    public void addCartItem(CartItem cartItem) {

        String url = BASE_API_URL + "/cartItem/add";

        try {
            String token = authService.getToken();
            if (token == null) {
                throw new RuntimeException("No token found. User might not be authenticated.");
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(String.format("{\"productId\": %d, \"cartId\": %d}}",
                            cartItem.getProductId(), cartItem.getCartId())))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpURLConnection.HTTP_OK && response.statusCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException(response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("ERROR ADDING CART ITEM: " + e.getMessage());
        }
    }

    public void removeCartItem(Long cartItemId) {
        String url = BASE_API_URL + "/cartItem/delete/" + cartItemId;

        try {
            String token = authService.getToken();
            if (token == null) {
                throw new RuntimeException("No token found. User might not be authenticated.");
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .uri(URI.create(url))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException(response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("ERROR REMOVING CART ITEM: " + e.getMessage());
        }
    }
}
