package pap.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pap.frontend.controllers.ControlledScreen;
import pap.frontend.controllers.ScreenController;
import pap.frontend.models.Review;
import pap.frontend.models.User;
import pap.frontend.services.AuthService;

import java.util.List;

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
        // Example: Set the username dynamically (this should be replaced with actual logic)

        refreshData();

    }
    private void loadUser() {
        try {
            System.out.println(authService.getCurrentUser());
            currentUser = authService.getCurrentUser();
            if (currentUser != null) {
                System.out.println("User loaded: " + currentUser.getUsername());
            } else {
                System.out.println("User is null");
            }
        }catch (Exception e) {
            showAlert("Error", "Failed to load users: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void refreshData() {
        checkAuthentication();

        if (authService.isAuthenticated()) {
            loadUser();
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

        Label usernameLabel = new Label("Username: " + currentUser.getUsername());
        Label emailLabel = new Label("Email: " + currentUser.getEmail());

        usernameLabel.setStyle("-fx-font-size: 14px;");
        emailLabel.setStyle("-fx-font-size: 14px;");;

        userDataBox.getChildren().addAll(usernameLabel, emailLabel);

        contentPane.getChildren().clear();
        contentPane.getChildren().add(userDataBox);
    }

    @FXML
    private void changeUsername() {
        // Tworzymy okno dialogowe z polem tekstowym
        VBox dialogBox = new VBox(10);
        dialogBox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label promptLabel = new Label("Enter new username:");
        promptLabel.setStyle("-fx-font-size: 14px;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("New username");
        usernameField.setPrefWidth(250);

        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-padding: 10 20; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        Label feedbackLabel = new Label(); // Informacja zwrotna
        feedbackLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        submitButton.setOnAction(event -> {
            String newUsername = usernameField.getText().trim();
            if (newUsername.isEmpty()) {
                feedbackLabel.setText("Username cannot be empty.");
                return;
            }

            if (newUsername.length() < 1 || newUsername.length() > 50) {
                feedbackLabel.setText("Username must be between 1 and 50 characters.");
                return;
            }

            // Wywołujemy serwis do zmiany nazwy użytkownika
            try {
                authService.updateUser(newUsername);
                refreshData();
                feedbackLabel.setText("Username updated successfully to: " + currentUser.getUsername());
                feedbackLabel.setStyle("-fx-text-fill: green;");
            } catch (Exception e) {
                feedbackLabel.setText("Error updating username: " + e.getMessage());
            }
        });

        dialogBox.getChildren().addAll(promptLabel, usernameField, submitButton, feedbackLabel);

        // Czyścimy istniejącą zawartość i dodajemy dialog
        contentPane.getChildren().clear();
        contentPane.getChildren().add(dialogBox);
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
