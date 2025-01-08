package pap.frontend.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.HashMap;

public class ScreenController {
    private final HashMap<String, Pane> screenMap = new HashMap<>();
    private final HashMap<String, Object> controllerMap = new HashMap<>();
    private final Scene main;

    public ScreenController(Scene main) {
        this.main = main;
    }

    public void addScreen(String name, String resourcePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            Pane pane = loader.load();
            screenMap.put(name, pane);

            // Pobierz i zapisz kontroler widoku
            Object controller = loader.getController();
            if (controller != null) {
                controllerMap.put(name, controller);

                // Jeśli kontroler implementuje ControlledScreen, ustaw ScreenController
                if (controller instanceof ControlledScreen) {
                    ((ControlledScreen) controller).setScreenController(this);
                }
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
        if (!screenMap.containsKey(name)) {
            System.err.println("Screen not found: " + name);
            return;
        }
        main.setRoot(screenMap.get(name));
    }

    public Object getController(String name) {
        return controllerMap.get(name);
    }
}
