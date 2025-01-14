package pap.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pap.frontend.models.UserRole;
import pap.frontend.services.AuthService;

public class LoginController implements ControlledScreen {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private ScreenController screenController;
    private final AuthService authService = AuthService.getInstance();

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            String token = authService.login(email, password);
            authService.saveToken(token);

            UserRole role = authService.getUserRole();
            if (role == null) {
                throw new RuntimeException("Unable to determine user role.");
            }

            if (role == UserRole.USER) {
                screenController.activate("userView"); // Navigate to the user view
                errorLabel.setText(""); // Clear any previous error messages
            } else if (role == UserRole.ADMIN) {
                screenController.activate("adminView"); // Navigate to the admin view
                errorLabel.setText(""); // Clear any previous error messages
            } else {
                throw new RuntimeException("Unknown user role: " + role);
            }

        } catch (Exception e) {
            errorLabel.setText("Login failed: " + e.getMessage());
        }
    }

    public void redirectToRegistration(ActionEvent actionEvent) {
        screenController.activate("registration");
    }
}
