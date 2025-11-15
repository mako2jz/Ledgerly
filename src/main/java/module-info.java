module Ledgerly {
    requires javafx.controls;
    requires javafx.fxml;

    // Allow FXMLLoader to reflectively instantiate controllers / access @FXML members
    opens ledgerly.app.controller to javafx.fxml;
    opens ledgerly.app.view to javafx.fxml;

    // Export application package if other modules need it (optional)
    exports ledgerly.app;
}