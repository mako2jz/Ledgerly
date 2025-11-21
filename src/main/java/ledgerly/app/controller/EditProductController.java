package ledgerly.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ledgerly.app.model.Product;

public class EditProductController {

    @FXML
    private TextField productNameField;
    @FXML
    private Button saveButton;

    private String newProductName;

    public void initData(Product product) {
        if (product != null) {
            productNameField.setText(product.getProductName());
        }
    }

    public String getNewProductName() {
        return newProductName;
    }

    @FXML
    private void handleSave() {
        newProductName = productNameField.getText().trim();
        closeModal();
    }

    @FXML
    private void handleCancel() {
        newProductName = null;
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
