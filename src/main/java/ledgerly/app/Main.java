package ledgerly.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ledgerly/app/view/MainView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 300, 250);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ledgerly/app/css/styles.css")).toExternalForm());

        primaryStage.setTitle("Ledgerly");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}