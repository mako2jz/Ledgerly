package ledgerly.app.controller;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import ledgerly.app.db.DatabaseManager;
import ledgerly.app.model.User;
import ledgerly.app.util.Toast;
import ledgerly.app.util.SvgLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController {

    @FXML
    private ListView<User> userListView;

    @FXML
    private Button addButton;

    @FXML
    private Label titleLabel; // injected from FXML

    @FXML
    private VBox mainContainer;

    // Container for multiple toasts
    @FXML
    private VBox toastContainer;

    @FXML
    public void initialize() {
        // sets up user list view
        userListView.setCellFactory(param -> new UserListCell());
        loadUsers();
        userListView.setOnMouseClicked(this::handleUserSelection);

        // prepare title animation
        if (titleLabel != null) {
            titleLabel.setOpacity(0);
            titleLabel.setTranslateX(-20);
            // ... (animation code is unchanged)
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

        if (toastContainer != null) {
            toastContainer.setPickOnBounds(false);
            toastContainer.setMaxWidth(Region.USE_PREF_SIZE);
        }
    }

    private void handleUserSelection(MouseEvent event) {
        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                // Close the current login window
                Stage currentStage = (Stage) userListView.getScene().getWindow();
                currentStage.close();

                // Load the dashboard view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ledgerly/app/view/DashboardView.fxml"));
                Parent root = loader.load();

                // Pass the selected user to the dashboard controller
                DashboardController controller = loader.getController();
                controller.initData(selectedUser);

                Stage dashboardStage = new Stage();
                dashboardStage.setTitle("Ledgerly Dashboard");
                Scene scene = new Scene(root);

                URL cssUrl = getClass().getResource("/ledgerly/app/css/styles.css");
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                }

                dashboardStage.setScene(scene);
                dashboardStage.setMaximized(true);
                dashboardStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
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
                Toast.show(toastContainer, "User added successfully!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
