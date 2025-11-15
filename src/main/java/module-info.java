module Ledgerly {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // JDBC API

    opens ledgerly.app.controller to javafx.fxml;
    opens ledgerly.app.view to javafx.fxml;

    exports ledgerly.app;
}