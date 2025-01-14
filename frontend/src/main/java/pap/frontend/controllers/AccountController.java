package pap.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import pap.frontend.controllers.ControlledScreen;
import pap.frontend.controllers.ScreenController;
import pap.frontend.services.AuthService;

public class AccountController extends AuthenticatedController {

    @FXML
    private Label usernameLabel;

    @FXML
    private Button logoutButton;

    private ScreenController screenController;


    public AccountController() {
        super(AuthService.getInstance());
    }

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML
    public void initialize() {

        refreshData();

    }

    protected void refreshData() {
        checkAuthentication();

        if (authService.isAuthenticated()) {
            String username = authService.getCurrentUserEmail();
            usernameLabel.setText(username);
        }
    }

    @FXML
    private void logout() {
        if (screenController != null) {
            authService.logout();
            screenController.activate("login");
        }
    }
}
