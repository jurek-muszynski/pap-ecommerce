package pap.frontend.controllers;

import pap.frontend.services.AuthService;

public abstract class AuthenticatedController implements ControlledScreen {

    protected final AuthService authService;
    protected ScreenController screenController;

    public AuthenticatedController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    protected void checkAuthentication() {
        if (!authService.isAuthenticated()) {
            System.err.println("User not authenticated. Redirecting to login screen.");
            if (screenController != null) {
                screenController.activate("login");
            }
        }
    }
}
