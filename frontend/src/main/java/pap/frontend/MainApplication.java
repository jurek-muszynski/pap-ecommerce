package pap.frontend;

import javafx.application.Application;
import javafx.stage.Stage;
import pap.frontend.controllers.ProductController;

/**
 * The `MainApplication` class is the entry point for the JavaFX application.
 * It extends `Application` and is responsible for launching the JavaFX runtime and initializing the application.

 * **Key Components:**

 * 1. **`start(Stage primaryStage)`:**
 *    - This method is automatically called by the JavaFX runtime when the application is launched.
 *    - It creates an instance of the `ProductController`, which manages the application UI and logic.
 *    - The `startApplication` method of `ProductController` is called to load the UI and set up the primary stage.

 * 2. **`main(String[] args)`:**
 *    - The main method is the standard Java entry point.
 *    - It calls `launch(args)` to start the JavaFX application lifecycle.
 *    - This method delegates to JavaFX to initialize and invoke the `start` method.

 * **Purpose:**
 * - The `MainApplication` class acts as a bootstrapper for the application, delegating UI initialization to the `ProductController`.
 * - By keeping this class minimal, the separation of concerns is maintained, with the controller handling UI-specific logic.

 * **Extensibility:**
 * - If additional controllers or stages are added in the future, this class can serve as a central point for managing them.
 */

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ProductController productController = new ProductController();
        productController.startApplication(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
