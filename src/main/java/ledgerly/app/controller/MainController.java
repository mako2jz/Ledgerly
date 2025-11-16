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

        if (toastContainer != null) {
            // Transparent areas won't block clicks
            toastContainer.setPickOnBounds(false);
            // Prevent the VBox from expanding to fill the StackPane width.
            toastContainer.setMaxWidth(Region.USE_PREF_SIZE);
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
        // create a new Label for each toast so multiple toasts can exist simultaneously
        Label toast = new Label("User successfully added");
        toast.getStyleClass().add("toast-label");

        Node icon = createSvgGraphic();
        if (icon != null) {
            toast.setGraphic(icon);
            toast.setGraphicTextGap(8);
        }

        // ensure the toast doesn't expand and only its painted area is pickable
        toast.setOpacity(0);
        toast.setTranslateY(20); // start slightly below
        toast.setMouseTransparent(false);
        toast.setPickOnBounds(true);
        toast.setMaxWidth(Region.USE_PREF_SIZE);

        // add new toast at index 0 so newer toasts appear above older ones (stack upward)
        if (toastContainer != null) {
            toastContainer.getChildren().add(0, toast);
        }

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(400), toast);
        slideUp.setToY(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), toast);
        fadeIn.setToValue(1);

        ParallelTransition showTransition = new ParallelTransition(slideUp, fadeIn);

        TranslateTransition slideDown = new TranslateTransition(Duration.millis(400), toast);
        slideDown.setToY(20);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setToValue(0);

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

    private Node createSvgGraphic() {
        try (InputStream is = getClass().getResourceAsStream("/ledgerly/app/icons/check.svg")) {
            if (is == null) return null;
            String svg = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // Find all d="..." or d='...' occurrences (case-insensitive)
            Pattern pathPattern = Pattern.compile("(?i)d\\s*=\\s*['\"]([^'\"]+)['\"]");
            Matcher pathMatcher = pathPattern.matcher(svg);

            Group group = new Group();
            boolean found = false;
            while (pathMatcher.find()) {
                String d = pathMatcher.group(1);
                if (d == null || d.trim().isEmpty()) continue;
                SVGPath svgPath = new SVGPath();
                svgPath.setContent(d);
                svgPath.setFill(Color.WHITE);
                group.getChildren().add(svgPath);
                found = true;
            }

            if (!found) return null;

            // Determine original width for scaling: look for width="..." or viewBox="minX minY width height"
            double originalWidth = -1;
            Pattern widthPattern = Pattern.compile("(?i)width\\s*=\\s*['\"](\\d+(?:\\.\\d+)?)['\"]");
            Matcher widthMatcher = widthPattern.matcher(svg);
            if (widthMatcher.find()) {
                try {
                    originalWidth = Double.parseDouble(widthMatcher.group(1));
                } catch (NumberFormatException ignored) {
                }
            }

            if (originalWidth <= 0) {
                // try viewBox
                Pattern vbPattern = Pattern.compile("(?i)viewBox\\s*=\\s*['\"]([-\\d\\.]+)\\s+([-\\d\\.]+)\\s+([-\\d\\.]+)\\s+([-\\d\\.]+)['\"]");
                Matcher vbMatcher = vbPattern.matcher(svg);
                if (vbMatcher.find()) {
                    try {
                        originalWidth = Double.parseDouble(vbMatcher.group(3));
                    } catch (NumberFormatException ignored) { originalWidth = -1; }
                }
            }

            double scale = 1.0;
            if (originalWidth > 0) {
                scale = (double) 16 / originalWidth;
            }

            group.setScaleX(scale);
            group.setScaleY(scale);

            group.setMouseTransparent(true);
            return group;
        } catch (IOException ex) {
            return null;
        }
    }
}
