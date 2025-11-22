package ledgerly.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddUserController {

    @FXML
    private TextField usernameField;

    @FXML
    private Button createUserButton;

    private String username = null;

    @FXML
    public void initialize() {
        // Disable the create button initially
        createUserButton.setDisable(true);

        // Add a listener to the text field to enable/disable the button
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> createUserButton.setDisable(newValue.trim().isEmpty()));
    }

    @FXML
    private void handleCreateUser() {
        this.username = usernameField.getText().trim();
        closeStage();
    }

    @FXML
    private void handleCancel() {
        closeStage();
    }

    public String getUsername() {
        return username;
    }

    private void closeStage() {
        Stage stage = (Stage) createUserButton.getScene().getWindow();
        stage.close();
    }
}
