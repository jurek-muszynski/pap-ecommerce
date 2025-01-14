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

            Object controller = loader.getController();
            if (controller instanceof ControlledScreen) {
                ControlledScreen controlledScreen = (ControlledScreen) controller;
                controlledScreen.setScreenController(this);
                controllerMap.put(name, controlledScreen);
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

            mainScene.setRoot(screen);

            ControlledScreen controller = controllerMap.get(name);
            if (controller != null) {
                if (controller instanceof UserProductController) {
                    ((UserProductController) controller).refreshData();
                } else if (controller instanceof CartController) {
                    ((CartController) controller).refreshData();
                } else if (controller instanceof AdminProductController) {
                    ((AdminProductController) controller).refreshData();
                } else if (controller instanceof AccountController) {
                    ((AccountController) controller).refreshData();
                } else if (controller instanceof SummaryController) {
                    System.out.println("Refreshing SummaryController");
                    ((SummaryController) controller).refreshData();
                }

            }
        } else {
            System.err.println("Screen not found: " + name);
            return;
        }
        mainScene.setRoot(screenMap.get(name));
    }

    public Object getController(String name) {
        return controllerMap.get(name);
    }
}
