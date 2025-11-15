package ledgerly.app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    public void initialize() {
        userListView.setCellFactory(param -> new UserListCell());
        loadUsers();
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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
