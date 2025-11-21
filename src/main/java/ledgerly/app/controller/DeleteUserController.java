package ledgerly.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ledgerly.app.model.User;

public class DeleteUserController {

    @FXML
    private Label confirmationLabel;
    @FXML
    private Button confirmButton;

    private boolean confirmed = false;

    /**
     * Initializes the controller with the user data to be confirmed for deletion.
     * @param user The user to be deleted.
     */
    public void initData(User user) {
        if (user != null) {
            confirmationLabel.setText("Are you sure you want to delete the profile for '" + user.getName() + "'? All associated sales and product data will be permanently lost. This action cannot be undone.");
        }
    }

    /**
     * Returns true if the user confirmed the deletion, false otherwise.
     * @return boolean confirmation status.
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    private void handleConfirm() {
        confirmed = true;
        closeModal();
    }

    @FXML
    private void handleCancel() {
        confirmed = false;
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}
