package pap.frontend;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pap.frontend.controllers.ScreenController;


public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        Scene mainScene = new Scene(new Pane(), 800, 600);

        // Initialize ScreenController
        ScreenController screenController = new ScreenController(mainScene);

        // Add screens
        screenController.addScreen("registration", "/pap/frontend/views/registration.fxml");
        screenController.addScreen("login", "/pap/frontend/views/login.fxml");
        screenController.addScreen("adminView", "/pap/frontend/views/admin_view.fxml");
        screenController.addScreen("userView", "/pap/frontend/views/user_view.fxml");
        screenController.addScreen("accountManagement", "/pap/frontend/views/account_management.fxml");
        screenController.addScreen("cartView", "/pap/frontend/views/cart.fxml");
        screenController.addScreen("summaryView", "/pap/frontend/views/summary.fxml");
        screenController.addScreen("reviewView", "/pap/frontend/views/reviewView.fxml");

        screenController.addScreen("about", "/pap/frontend/views/about.fxml");
        // Activate initial screen
        screenController.activate("registration");

        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Product Catalog");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
