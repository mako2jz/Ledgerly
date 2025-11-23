module Ledgerly {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.base;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;

    opens ledgerly.app.controller to javafx.fxml;
    opens ledgerly.app.view to javafx.fxml;
    opens ledgerly.app.model to javafx.base; // Allow reflection for TableView

    exports ledgerly.app;
}
