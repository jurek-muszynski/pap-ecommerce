package pap.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import pap.frontend.models.RegisterRequest;
import pap.frontend.services.AuthService;

public class RegistrationController implements ControlledScreen {
    @FXML
    private VBox RegistrationPane;

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label errorLabel;

    private ScreenController screenController;
    private final AuthService authService = AuthService.getInstance(); // Service to handle authentication

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML
    public void initialize() {
        // Apply CSS style once the scene is set
        RegistrationPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(getClass().getResource("/pap/frontend/styles/cartStyles.css").toExternalForm());
            }
        });
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        try {
            authService.register(new RegisterRequest(username, email, password));
            screenController.activate("login");
        } catch (Exception e) {
            errorLabel.setText("Registration failed: " + e.getMessage());
        }
    }

    @FXML
    private void redirectToLogin() {
        screenController.activate("login"); // Redirect to login screen
    }

    @FXML
    private void redirectToAbout() {
        screenController.activate("about");
    }

}
