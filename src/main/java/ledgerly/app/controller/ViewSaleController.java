package ledgerly.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ledgerly.app.model.Sale;
public class ViewSaleController {

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

    public void initData(Sale sale) {
        if (sale != null) {
            titleLabel.setText("Sale #" + sale.getSaleId());
            productNameLabel.setText(sale.getProductName());
            saleAmountLabel.setText(String.format("₱%.2f", sale.getAmount()));
            saleDescriptionLabel.setText(sale.getDescription() != null && !sale.getDescription().isEmpty() ? sale.getDescription() : "No description provided.");

            try {
                saleDateLabel.setText(sale.getCreatedAt());
            } catch (Exception e) {
                saleDateLabel.setText("Invalid date");
            }
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}
