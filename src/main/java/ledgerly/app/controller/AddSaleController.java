package ledgerly.app.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ledgerly.app.db.DatabaseManager;
import ledgerly.app.model.Product;
import ledgerly.app.model.User;

public class AddSaleController {

    @FXML
    private ComboBox<Product> productComboBox;
    @FXML
    private TextField amountField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button addSaleButton;

    private User currentUser;
    private boolean saleAdded = false;

    public void initData(User user) {
        this.currentUser = user;
        productComboBox.setItems(FXCollections.observableArrayList(DatabaseManager.getProductsForUser(currentUser.getId())));
    }

    @FXML
    public void initialize() {
        // Only allow numbers in the amount field
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                amountField.setText(oldValue);
            }
        });

        // Disable button if required fields are empty
        addSaleButton.disableProperty().bind(
                Bindings.or(
                        productComboBox.valueProperty().isNull(),
                        amountField.textProperty().isEmpty()
                )
        );
    }

    @FXML
    private void handleAddSale() {
        Product selectedProduct = productComboBox.getValue();
        double amount = Double.parseDouble(amountField.getText());
        String description = descriptionArea.getText();

        DatabaseManager.addSale(currentUser.getId(), selectedProduct.getProductId(), amount, description);
        saleAdded = true;
        closeStage();
    }

    @FXML
    private void handleCancel() {
        closeStage();
    }

    public boolean isSaleAdded() {
        return saleAdded;
    }

    private void closeStage() {
        Stage stage = (Stage) addSaleButton.getScene().getWindow();
        stage.close();
    }
}
