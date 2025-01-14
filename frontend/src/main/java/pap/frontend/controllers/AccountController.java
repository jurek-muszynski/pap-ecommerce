package pap.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pap.frontend.models.User;
import pap.frontend.services.AuthService;

import java.util.function.Consumer;

public class AccountController extends AuthenticatedController {

    @FXML
    private StackPane contentPane;

    private ScreenController screenController;

    private User currentUser;

    public AccountController() { super(AuthService.getInstance()); }

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML
    public void initialize() {
        refreshData();
    }
    private void loadUser() {
        try {
            currentUser = authService.getCurrentUser();
        }catch (Exception e) {
            showAlert("Error", "Failed to load users: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void refreshData() {
        checkAuthentication();

        if (authService.isAuthenticated()) {
            String username = authService.getCurrentUserEmail();
            usernameLabel.setText(username);
        }
    }

    @FXML
    private void goBackToProducts() {
        if (screenController != null) {
            screenController.activate("userView");
        }
    }

    @FXML
    private void yourData() {
        VBox userDataBox = new VBox(10);
        userDataBox.setStyle("-fx-padding: 20;");

        Label usernameLabel = new Label("Username: " + currentUser.getName());
        Label emailLabel = new Label("Email: " + currentUser.getEmail());

        usernameLabel.setStyle("-fx-font-size: 14px;");
        emailLabel.setStyle("-fx-font-size: 14px;");;

        userDataBox.getChildren().addAll(usernameLabel, emailLabel);

        contentPane.getChildren().clear();
        contentPane.getChildren().add(userDataBox);
    }

    @FXML
    private void changeUserField(String fieldLabel, String placeholder,
                                 int minLength, int maxLength,
                                 Consumer<String> updateAction) {
        VBox dialogBox = new VBox(10);
        dialogBox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label promptLabel = new Label("Enter new " + fieldLabel + ":");
        promptLabel.setStyle("-fx-font-size: 14px;");

        TextField inputField = new TextField();
        inputField.setPromptText(placeholder);
        inputField.setPrefWidth(250);

        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-padding: 10 20; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        Label feedbackLabel = new Label();
        feedbackLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        submitButton.setOnAction(event -> {
            String input = inputField.getText().trim();

            if (input.isEmpty()) {
                feedbackLabel.setText(fieldLabel + " cannot be empty.");
                return;
            }
            if (input.length() < minLength || input.length() > maxLength) {
                feedbackLabel.setText(fieldLabel + " must be between " + minLength + " and " + maxLength + " characters.");
                return;
            }

            if (fieldLabel.equals("email")) {
                try {
                    updateAction.accept(input);
                    feedbackLabel.setText(fieldLabel + " updated successfully!");
                    feedbackLabel.setStyle("-fx-text-fill: green;");
                    showAlert("Success", "Email updated successfully. Please log in again.", Alert.AlertType.INFORMATION);
                    contentPane.getChildren().clear();
                    logout();
                } catch (Exception e) {
                    feedbackLabel.setText("Error updating " + fieldLabel + ": " + e.getMessage());
                }
            } else {
                try {
                    updateAction.accept(input);
                    refreshData();
                    feedbackLabel.setText(fieldLabel + " updated successfully!");
                    feedbackLabel.setStyle("-fx-text-fill: green;");
                } catch (Exception e) {
                    feedbackLabel.setText("Error updating " + fieldLabel + ": " + e.getMessage());
                }
            }
        });

        dialogBox.getChildren().addAll(promptLabel, inputField, submitButton, feedbackLabel);

        contentPane.getChildren().clear();
        contentPane.getChildren().add(dialogBox);
    }

    @FXML
    private void changeUsername() {
        changeUserField(
                "username",
                "New username",
                1,
                50,
                newUsername -> {
                    User user = authService.getCurrentUser();
                    authService.updateUser(user.getId(), user.getEmail(), user.getPassword(), user.getRole(), newUsername);
                }
        );
    }

    @FXML
    private void changeEmail() {
        changeUserField(
                "email",
                "New email",
                1,
                50,
                newEmail -> {
                    User user = authService.getCurrentUser();
                    authService.updateUser(user.getId(), newEmail, user.getPassword(), user.getRole(), user.getName());
                }
        );
    }

    @FXML
    private void changePassword() {
        changeUserField(
                "password",
                "New password",
                1,
                50,
                newPassword ->{
                    User user = authService.getCurrentUser();
                    authService.updateUser(user.getId(), user.getEmail(), newPassword, user.getRole(), user.getName());
                }
        );
    }

    @FXML
    private void logout() {
        if (screenController != null) {
            authService.logout();
            screenController.activate("login");
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
