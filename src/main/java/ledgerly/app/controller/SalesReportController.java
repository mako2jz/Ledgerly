package ledgerly.app.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ledgerly.app.db.DatabaseManager;
import ledgerly.app.model.ProductSalesReport;
import ledgerly.app.model.User;

import java.util.List;

public class SalesReportController {

    private User currentUser;
    private List<ProductSalesReport> reportData;
    private static final int ROWS_PER_PAGE = 15;

    @FXML
    private TableView<ProductSalesReport> reportTableView;
    @FXML
    private TableColumn<ProductSalesReport, String> productColumn;
    @FXML
    private TableColumn<ProductSalesReport, Integer> countColumn;
    @FXML
    private TableColumn<ProductSalesReport, Double> totalValueColumn;
    @FXML
    private Pagination pagination;
    @FXML
    private Label grandTotalLabel;
    @FXML
    private Button closeButton;

    @FXML
    public void initialize() {
        for (TableColumn<?, ?> col : reportTableView.getColumns()) {
            col.setResizable(false);
        }

        reportTableView.getColumns().forEach(col ->
                col.setReorderable(false)
        );
    }

    public void initData(User user) {
        this.currentUser = user;
        setupColumns();
        loadReportData();
    }

    private void setupColumns() {
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        countColumn.setCellValueFactory(new PropertyValueFactory<>("salesCount"));
        totalValueColumn.setCellValueFactory(new PropertyValueFactory<>("totalValue"));

        totalValueColumn.setCellFactory(tc -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("₱%.2f", value));
                }
            }
        });
    }

    private void loadReportData() {
        reportData = DatabaseManager.getProductSalesReport(currentUser.getId());

        double grandTotal = reportData.stream().mapToDouble(ProductSalesReport::getTotalValue).sum();
        grandTotalLabel.setText("Grand Total: " + String.format("₱%.2f", grandTotal));

        pagination.setPageCount((int) Math.ceil((double) reportData.size() / ROWS_PER_PAGE));
        pagination.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, reportData.size());
        reportTableView.setItems(FXCollections.observableArrayList(reportData.subList(fromIndex, toIndex)));
        return new VBox(reportTableView);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
