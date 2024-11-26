package pap.frontend;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pap.frontend.models.Product;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        fetchProducts();
    }

    public void fetchProducts() {
        try {
            // Tworzenie poÅ***REMOVED***Ä…czenia z API
            URL url = new URL("http://localhost:8080/api/v1/product/all");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Sprawdzenie kodu odpowiedzi HTTP
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HTTP Response Code: " + responseCode);
            }

            Gson gson = new Gson();
            Type productListType = new TypeToken<List<Product>>(){}.getType();
            List<Product> products = gson.fromJson(new InputStreamReader(conn.getInputStream()), productListType);

            for (Product product : products) {
                System.out.println(product.getName() + " " + product.getPrice());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch();
    }
}