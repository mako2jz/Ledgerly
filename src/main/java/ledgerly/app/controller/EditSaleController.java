package ledgerly.app.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ledgerly.app.db.DatabaseManager;
import ledgerly.app.model.Product;
import ledgerly.app.model.Sale;
import ledgerly.app.model.User;

import java.util.List;
import java.util.Objects;

public class EditSaleController {

    @FXML
    private Label saleIdLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private ComboBox<Product> productComboBox;
    @FXML
    private TextField amountField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button saveButton;

    private Sale saleToEdit;
    private User currentUser;
    private boolean saleUpdated = false;

    public void initData(Sale sale, User user) {
        this.saleToEdit = sale;
        this.currentUser = user;

        // Populate non-editable fields
        saleIdLabel.setText(String.valueOf(sale.getSaleId()));
        dateLabel.setText(sale.getCreatedAt());

        // Populate editable fields
        amountField.setText(String.format("%.2f", sale.getAmount()));
        descriptionArea.setText(sale.getDescription());

        // Populate and set product ComboBox
        List<Product> userProducts = DatabaseManager.getProductsForUser(currentUser.getId());
        productComboBox.setItems(FXCollections.observableArrayList(userProducts));

        // Find and select the current product
        Product currentProduct = userProducts.stream()
                .filter(p -> Objects.equals(p.getProductName(), sale.getProductName()))
                .findFirst()
                .orElse(null); // Handles "Unknown Product" case
        if (currentProduct != null) {
            productComboBox.setValue(currentProduct);
        }
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
        saveButton.disableProperty().bind(
                Bindings.or(
                        productComboBox.valueProperty().isNull(),
                        amountField.textProperty().isEmpty()
                )
        );
    }

    @FXML
    private void handleSave() {
        Product selectedProduct = productComboBox.getValue();
        double amount = Double.parseDouble(amountField.getText());
        String description = descriptionArea.getText();

        DatabaseManager.updateSale(saleToEdit.getSaleId(), selectedProduct.getProductId(), amount, description);
        saleUpdated = true;
        closeStage();
    }

    @FXML
    private void handleCancel() {
        closeStage();
    }

    public boolean isSaleUpdated() {
        return saleUpdated;
    }

    private void closeStage() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
