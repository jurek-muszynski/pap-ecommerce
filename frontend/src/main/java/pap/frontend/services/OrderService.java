package pap.frontend.services;

import com.google.gson.Gson;
import pap.frontend.models.Order;
import pap.frontend.models.OrderRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OrderService {

    private static final String BASE_API_URL = "http://localhost:8080/api/v1";
    private final HttpClient httpClient;
    private final Gson gson;
    private final AuthService authService;

    public OrderService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.authService = AuthService.getInstance();
    }

    public boolean placeOrder(Long userId, String email, String deliveryAddress) {
        String url = BASE_API_URL + "/order/add";

        OrderRequest placeOrderRequest = new OrderRequest(userId, email, deliveryAddress);

        String token = authService.getToken();

        try {
            String requestBody = gson.toJson(placeOrderRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpURLConnection.HTTP_OK || response.statusCode() == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Order placed successfully.");
                return true;
            } else {
                System.err.println("Failed to place order. Status code: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
