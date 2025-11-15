package ledgerly.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ledgerly.app.db.Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            Connection conn = Database.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database", e);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ledgerly/app/view/MainView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 300, 250);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ledgerly/app/css/styles.css")).toExternalForm());

        primaryStage.setTitle("Ledgerly");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}