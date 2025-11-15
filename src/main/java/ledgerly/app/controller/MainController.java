package ledgerly.app.controller;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import ledgerly.app.db.DatabaseManager;
import ledgerly.app.model.User;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainController {

    @FXML
    private ListView<User> userListView;

    @FXML
    private Button addButton;

    @FXML
    private Label titleLabel; // injected from FXML

    @FXML
    private VBox mainContainer;

    @FXML
    private Label toastLabel;

    @FXML
    public void initialize() {
        // sets up user list view
        userListView.setCellFactory(param -> new UserListCell());
        loadUsers();

        // prepare title animation: start slightly to the left and transparent
        if (titleLabel != null) {
            titleLabel.setOpacity(0);
            titleLabel.setTranslateX(-20);

            FadeTransition fade = new FadeTransition(Duration.millis(1000), titleLabel);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setInterpolator(Interpolator.EASE_BOTH);

            TranslateTransition slide = new TranslateTransition(Duration.millis(1000), titleLabel);
            slide.setFromX(-20);
            slide.setToX(0);
            slide.setInterpolator(Interpolator.EASE_BOTH);

            ParallelTransition animation = new ParallelTransition(fade, slide);
            animation.setDelay(Duration.millis(300));
            animation.play();
        }
    }

    private void loadUsers() {
        List<User> userList = DatabaseManager.getUsers();
        ObservableList<User> observableUserList = FXCollections.observableArrayList(userList);
        userListView.setItems(observableUserList);
    }

    @FXML
    private void handleAddUserButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ledgerly/app/view/AddUserView.fxml"));
            Parent root = loader.load();

            AddUserController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New User");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(addButton.getScene().getWindow());
            dialogStage.initStyle(StageStyle.UNDECORATED);

            Scene scene = new Scene(root);

            URL cssUrl = getClass().getResource("/ledgerly/app/css/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogStage.showAndWait();

            String newUsername = controller.getUsername();
            if (newUsername != null && !newUsername.isEmpty()) {
                DatabaseManager.addUser(newUsername);
                loadUsers(); // Refresh the user list
                showToast();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showToast() {
        toastLabel.setVisible(true);
        toastLabel.setTranslateY(50); // Start below view
        toastLabel.setOpacity(0);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(400), toastLabel);
        slideUp.setToY(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), toastLabel);
        fadeIn.setToValue(1);

        ParallelTransition showTransition = new ParallelTransition(slideUp, fadeIn);

        TranslateTransition slideDown = new TranslateTransition(Duration.millis(400), toastLabel);
        slideDown.setToY(50);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toastLabel);
        fadeOut.setToValue(0);

        ParallelTransition hideTransition = new ParallelTransition(slideDown, fadeOut);
        hideTransition.setOnFinished(e -> toastLabel.setVisible(false));

        SequentialTransition sequentialTransition = new SequentialTransition(
                showTransition,
                new PauseTransition(Duration.seconds(3)),
                hideTransition
        );
        sequentialTransition.play();
    }
}
