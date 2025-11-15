package ledgerly.app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import ledgerly.app.db.DatabaseManager;
import ledgerly.app.model.User;

import java.util.List;

public class MainController {

    @FXML
    private ListView<User> userListView;

    @FXML
    public void initialize() {
        // Set the custom cell factory to render User objects
        userListView.setCellFactory(param -> new UserListCell());

        // Load users from the database
        List<User> userList = DatabaseManager.getUsers();
        ObservableList<User> observableUserList = FXCollections.observableArrayList(userList);

        // Populate the ListView
        userListView.setItems(observableUserList);
    }
}
