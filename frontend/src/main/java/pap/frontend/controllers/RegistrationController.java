package pap.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import pap.frontend.models.RegisterRequest;
import pap.frontend.services.AuthService;

public class RegistrationController implements ControlledScreen {
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
    private final AuthService authService = AuthService.getInstance();

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
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
        screenController.activate("login");
    }

    @FXML
    private void redirectToAbout() {
        screenController.activate("about");
    }

}
