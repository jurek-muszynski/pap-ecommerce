module pap.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens pap.frontend to javafx.fxml;
    exports pap.frontend;
    exports pap.frontend.models;
    opens pap.frontend.models to javafx.fxml;
}