module pap.frontend {
/**
 * This module-info.java file defines the module structure for the project using the Java Platform Module System (JPMS).
 *
 * - `module pap.frontend { ... }`: Declares the module name as `pap.frontend`.
 * - `requires javafx.controls;`: Includes the JavaFX Controls module for GUI components like buttons, tables, etc.
 * - `requires javafx.fxml;`: Allows the use of JavaFX FXML for loading UI layouts from FXML files.
 * - `requires java.net.http;`: Enables the use of the HttpClient API for sending HTTP requests.
 * - `requires com.google.gson;`: Adds the Gson library for parsing JSON data to Java objects and vice versa.
 * - `opens pap.frontend.controllers to javafx.fxml;`: Makes the `pap.frontend.controllers` package accessible to JavaFX's FXML module for reflection-based operations.
 * - `opens pap.frontend.models to javafx.base, com.google.gson;`: Opens the `pap.frontend.models` package to:
 *      - `javafx.base`: Allows JavaFX to reflectively access model classes (e.g., Product) for data binding in UI tables.
 *      - `com.google.gson`: Enables Gson to use reflection for serializing and deserializing model classes.
 * - `exports pap.frontend;`: Makes the `pap.frontend` package accessible to other modules, including the main class for application launch.
 */
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;
    requires java.desktop;
    requires java.mail;

    opens pap.frontend.controllers to javafx.fxml;
    opens pap.frontend.models to javafx.base, com.google.gson;
    exports pap.frontend;
}
