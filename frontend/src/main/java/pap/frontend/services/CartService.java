package pap.frontend.services;

import com.google.gson.Gson;
import pap.frontend.models.CartItem;
import pap.frontend.models.Product;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CartService {

    private static final String BASE_API_URL = "http://localhost:8080/api/v1";
    private final HttpClient httpClient;
    private final Gson gson;

    public CartService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public void addCartItem(CartItem cartItem) {

        String url = BASE_API_URL + "/cartItem/add";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(String.format("{\"product\": {\"id\": %d}, \"cart\": {\"id\": %d}}",
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
}
