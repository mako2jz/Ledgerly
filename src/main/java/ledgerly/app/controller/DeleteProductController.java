package ledgerly.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ledgerly.app.model.Product;

public class DeleteProductController {

    @FXML
    private Label confirmationLabel;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;

    private boolean confirmed = false;

    public void initData(Product product) {
        if (product != null) {
            confirmationLabel.setText("Are you sure you want to delete the product '" + product.getProductName() + "'? This action cannot be undone.");
        }
    }

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
