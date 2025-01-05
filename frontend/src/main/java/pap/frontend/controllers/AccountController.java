package pap.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import pap.frontend.controllers.ControlledScreen;
import pap.frontend.controllers.ScreenController;

public class AccountController implements ControlledScreen {

    @FXML
    private Label usernameLabel;

    @FXML
    private Button logoutButton;

    private ScreenController screenController;
    private String username = "Admin User"; // Placeholder; replace with dynamic data retrieval.

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML
    public void initialize() {
        // Example: Set the username dynamically (this should be replaced with actual logic)
        usernameLabel.setText("Logged in as: " + username);
    }

    @FXML
    private void logout() {
        if (screenController != null) {
            screenController.activate("roleSelection");
        }
    }
}
