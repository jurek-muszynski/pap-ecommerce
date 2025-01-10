package pap.frontend.controllers;

import javafx.fxml.FXML;

public class AboutController implements ControlledScreen {
    private ScreenController screenController;

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML
    private void goBack() {
        screenController.activate("registration");
    }
}
