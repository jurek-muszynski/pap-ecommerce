package pap.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        // G�***REMOVED***ówne okno wyboru ról
        VBox root = new VBox(20);
        root.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Przycisk dla Admina
        Button adminButton = new Button("Admin");
        adminButton.setOnAction(event -> loadAdminView(primaryStage));

        // Przycisk dla Użytkownika
        Button userButton = new Button("Użytkownik");
        userButton.setOnAction(event -> loadUserView(primaryStage));

        root.getChildren().addAll(adminButton, userButton);

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Wybór roli");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void loadAdminView(Stage stage) {
        try {
            // Ładowanie widoku admina (np. product_list.fxml)
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/pap/frontend/product_list.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserView(Stage stage) {
        try {
            // Ładowanie widoku użytkownika (nowy plik FXML, np. user_view.fxml)
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/pap/frontend/user_view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}