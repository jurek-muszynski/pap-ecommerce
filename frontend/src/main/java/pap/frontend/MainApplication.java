package pap.frontend;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pap.frontend.controllers.ScreenController;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        Scene mainScene = new Scene(new Pane(), 800, 600); // Placeholder

        // Inicjalizacja ScreenController
        ScreenController screenController = new ScreenController(mainScene);

        // Dodawanie widoków
        screenController.addScreen("roleSelection", "/pap/frontend/role_selection.fxml");
        screenController.addScreen("adminView", "/pap/frontend/product_list.fxml");
        screenController.addScreen("userView", "/pap/frontend/user_view.fxml");

        // Aktywuj widok wyboru ról
        screenController.activate("roleSelection");

        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Product Catalog");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
