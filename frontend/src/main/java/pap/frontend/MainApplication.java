package pap.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


// TODO:
// 1. Add a note in the app explaining that Search and Filter work independently.
// 2. Fix searching two-part words in search by name
public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/pap/frontend/product_list.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}