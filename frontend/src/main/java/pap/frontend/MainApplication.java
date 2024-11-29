package pap.frontend;

import javafx.application.Application;
import javafx.stage.Stage;
import pap.frontend.controllers.ProductController;


// TODO:
// 1. Align buttons and inputs neatly for a cleaner UI.
// 2. Add a note in the app explaining that Search and Filter work independently.
public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProductController productController = new ProductController();
        productController.startApplication(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
