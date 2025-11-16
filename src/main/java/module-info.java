module Ledgerly {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics; // JDBC API

    opens ledgerly.app.controller to javafx.fxml;
    opens ledgerly.app.view to javafx.fxml;
    opens ledgerly.app.model to javafx.base; // Allow reflection for TableView

    exports ledgerly.app;
}
