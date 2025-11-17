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
                showToast("User successfully added");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showToast(String message) {
        Label toast = new Label(message);
        toast.getStyleClass().add("toast-label");

        Node icon = createSvgGraphic("/ledgerly/app/svg/check.svg");
        if (icon != null) {
            toast.setGraphic(icon);
            toast.setGraphicTextGap(8);
        }

        toast.setOpacity(0);
        toast.setTranslateY(20);
        toast.setMouseTransparent(false);
        toast.setPickOnBounds(true);
        toast.setMaxWidth(Region.USE_PREF_SIZE);

        if (toastContainer != null) {
            toastContainer.getChildren().add(0, toast);
        }

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(400), toast);
        slideUp.setToY(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), toast);
        fadeIn.setToValue(1);
        ParallelTransition showTransition = new ParallelTransition(slideUp, fadeIn);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setToValue(0);
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(400), toast);
        slideDown.setToY(20);
        ParallelTransition hideTransition = new ParallelTransition(slideDown, fadeOut);
        hideTransition.setOnFinished(e -> {
            if (toastContainer != null) {
                toastContainer.getChildren().remove(toast);
            }
        });

        SequentialTransition sequentialTransition = new SequentialTransition(
                showTransition,
                new PauseTransition(Duration.seconds(3)),
                hideTransition
        );
        sequentialTransition.play();
    }

    private Node createSvgGraphic(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            String svg = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Pattern pathPattern = Pattern.compile("(?i)d\\s*=\\s*['\"]([^'\"]+)['\"]");
            Matcher pathMatcher = pathPattern.matcher(svg);
            Group group = new Group();
            while (pathMatcher.find()) {
                SVGPath svgPath = new SVGPath();
                svgPath.setContent(pathMatcher.group(1));
                svgPath.setFill(Color.WHITE);
                group.getChildren().add(svgPath);
            }
            if (group.getChildren().isEmpty()) return null;

            double originalWidth = 16;
            Pattern vbPattern = Pattern.compile("(?i)viewBox\\s*=\\s*['\"]([-\\d\\.]+)\\s+([-\\d\\.]+)\\s+([-\\d\\.]+)\\s+([-\\d\\.]+)['\"]");
            Matcher vbMatcher = vbPattern.matcher(svg);
            if (vbMatcher.find()) {
                try {
                    originalWidth = Double.parseDouble(vbMatcher.group(3));
                } catch (NumberFormatException ignored) {}
            }
            double scale = (originalWidth > 0) ? 16 / originalWidth : 1.0;
            group.setScaleX(scale);
            group.setScaleY(scale);
            group.setMouseTransparent(true);
            return group;
        } catch (IOException ex) {
            return null;
        }
    }
}
