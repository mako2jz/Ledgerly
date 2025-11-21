package ledgerly.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ledgerly.app.model.Sale;

public class DeleteSaleController {

    @FXML
    private Label saleDetailsLabel;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label productNameLabel;
    @FXML
    private Label saleAmountLabel;
    @FXML
    private Label saleDateLabel;
    @FXML
    private Label saleDescriptionLabel;
    @FXML
    private Label titleLabel;

    private Sale saleToDelete;
    private boolean confirmed = false;

    public void initData(Sale sale) {
        this.saleToDelete = sale;
        if (sale != null) {
            titleLabel.setText("Delete Sale");
            saleDetailsLabel.setText("Are you sure you want to delete this sale?");
            productNameLabel.setText(sale.getProductName());
            saleAmountLabel.setText(String.format("₱%.2f", sale.getAmount()));
            saleDescriptionLabel.setText(sale.getDescription() != null && !sale.getDescription().isEmpty() ? sale.getDescription() : "No description provided.");
            saleDateLabel.setText(sale.getCreatedAt());
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
