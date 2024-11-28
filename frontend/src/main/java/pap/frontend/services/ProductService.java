package pap.frontend.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pap.frontend.models.Product;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * The `ProductService` class is a service layer responsible for interacting with the backend API to fetch product data.
 * It handles HTTP requests, processes JSON responses, and converts them into Java objects for use in the application.

 * **Key Components:**
 * 1. `API_URL`:
 *    - A constant string representing the endpoint URL (`http://localhost:8080/api/v1/product/all`).
 *    - This endpoint is expected to return a list of all products in JSON format.

 * 2. `httpClient`:
 *    - An instance of `HttpClient` used to send HTTP requests and receive responses.
 *    - It provides a modern, efficient API for making HTTP calls in Java.

 * 3. `gson`:
 *    - An instance of the Gson library used to deserialize JSON responses into Java objects.
 *    - Simplifies conversion between JSON and the `Product` class.

 * **Constructor:**
 * - Initializes `httpClient` using `HttpClient.newHttpClient()` and `gson` using `new Gson()`.

 * **Methods:**
 * 1. `getProducts()`:
 *    - Fetches a list of products from the backend API.
 *    - Steps:
 *        a. Creates an HTTP GET request to the `API_URL`.
 *        b. Sends the request using `httpClient` and processes the response.
 *        c. If the response status code is 200 (OK), the JSON response body is parsed into a list of `Product` objects using Gson.
 *        d. If the response status code is not 200 or an exception occurs, an empty list is returned, and the error is logged.

 * **Error Handling:**
 * - Logs the HTTP status code if the response is not OK.
 * - Handles `IOException` and `InterruptedException` by logging the stack trace and returning an empty list.

 * **Use Case:**
 * - This service is used in the frontend application to fetch and process product data from the backend.
 * - It abstracts the complexity of HTTP requests and JSON deserialization, making it easy to retrieve product data as Java objects.

 * **Extensibility:**
 * - Additional methods can be added to handle other API endpoints, such as adding, updating, or deleting products.
 * - API URLs can be parameterized for dynamic endpoint access.
 */

public class ProductService {

    private static final String API_URL = "http://localhost:8080/api/v1/product/all";
    private final HttpClient httpClient;
    private final Gson gson;

    public ProductService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<Product> getProducts() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return gson.fromJson(response.body(), new TypeToken<List<Product>>() {}.getType());
            } else {
                System.err.println("Błąd podczas pobierania produktów: " + response.statusCode());
                return List.of();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
