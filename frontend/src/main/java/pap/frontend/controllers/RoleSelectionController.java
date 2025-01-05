package pap.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import pap.frontend.controllers.ControlledScreen;
import pap.frontend.controllers.ScreenController;

public class RoleSelectionController implements ControlledScreen {
    private ScreenController screenController;

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML
    public void loadAdminView(ActionEvent event) {
        screenController.activate("adminView");
    }

    @FXML
    public void loadUserView(ActionEvent event) {
        screenController.activate("userView");
    }
}
