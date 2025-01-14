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

        ScreenController screenController = new ScreenController(mainScene);

        screenController.addScreen("registration", "/pap/frontend/registration.fxml");
        screenController.addScreen("login", "/pap/frontend/login.fxml");
        screenController.addScreen("adminView", "/pap/frontend/admin_view.fxml");
        screenController.addScreen("userView", "/pap/frontend/user_view.fxml");
        screenController.addScreen("accountManagement", "/pap/frontend/account_management.fxml");
        screenController.addScreen("cartView", "/pap/frontend/cart.fxml");
        screenController.addScreen("summaryView", "/pap/frontend/summary.fxml");
        screenController.addScreen("about", "/pap/frontend/about.fxml");
        screenController.activate("registration");

        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Product Catalog");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
