package pap.frontend.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.HashMap;

public class ScreenController {
    private final HashMap<String, Pane> screenMap = new HashMap<>();
    private final HashMap<String, ControlledScreen> controllerMap = new HashMap<>();
    private final Scene mainScene;

    public ScreenController(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public void addScreen(String name, String resourcePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            Pane pane = loader.load();
            screenMap.put(name, pane);

            // Register the controller if it implements ControlledScreen
            Object controller = loader.getController();
            if (controller instanceof ControlledScreen) {
                ControlledScreen controlledScreen = (ControlledScreen) controller;
                controlledScreen.setScreenController(this);
                controllerMap.put(name, controlledScreen); // Map the controller to the screen name
            }
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + resourcePath);
            e.printStackTrace();
        }
    }

    public void removeScreen(String name) {
        screenMap.remove(name);
        controllerMap.remove(name);
    }

    public void activate(String name) {
        Pane screen = screenMap.get(name);
        if (screen != null) {
            // Set the screen in the root scene
            mainScene.setRoot(screen);

            // Notify the controller to refresh data if it implements ControlledScreen
            ControlledScreen controller = controllerMap.get(name);
            if (controller != null) {
                if (controller instanceof UserProductController) {
                    ((UserProductController) controller).refreshData();
                } else if (controller instanceof CartController) {
                    ((CartController) controller).refreshData();
                } else if (controller instanceof AdminProductController) {
                    ((AdminProductController) controller).refreshData();
                }
                // Add similar checks for other controllers that need refreshing
            }
        } else {
            System.err.println("Screen not found: " + name);
        }
    }
}
