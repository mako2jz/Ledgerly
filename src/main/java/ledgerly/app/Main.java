package ledgerly.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlUrl = getClass().getResource("/ledgerly/app/view/MainView.fxml");
        if (fxmlUrl == null) {
            throw new IOException("Cannot find FXML file 'MainView.fxml'.");
        }

        Parent root = FXMLLoader.load(fxmlUrl);
        Scene scene = new Scene(root, 700, 760);

        // Load and apply the stylesheet from the correct path
        URL cssUrl = getClass().getResource("/ledgerly/app/css/styles.css");
        if (cssUrl == null) {
            throw new IOException("Cannot find CSS file 'styles.css'.");
        }
        scene.getStylesheets().add(cssUrl.toExternalForm());

        primaryStage.setTitle("Ledgerly - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
