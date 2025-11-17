package ledgerly.app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ledgerly.app.db.DatabaseManager;
import ledgerly.app.model.Product;
import ledgerly.app.model.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProductController {

    private User currentUser;
    private ObservableList<Product> products;

    @FXML
    private ListView<Product> productListView;
    @FXML
    private TextField productNameField;
    @FXML
    private Button addProductButton;

    @FXML
    public void initialize() {
        addProductButton.setDisable(true);

        productNameField.textProperty().addListener((obs, old, val) -> {
            addProductButton.setDisable(val.trim().isEmpty());
        });

        productListView.setCellFactory(param -> new ProductListCell(this::handleEditProduct, this::handleDeleteProduct));
    }

    public void initData(User user) {
        this.currentUser = user;
        loadProducts();
    }

    private void loadProducts() {
        List<Product> productList = DatabaseManager.getProductsForUser(currentUser.getId());
        products = FXCollections.observableArrayList(productList);
        productListView.setItems(products);
    }

    @FXML
    private void handleAddProduct() {
        String productName = productNameField.getText().trim();
        if (!productName.isEmpty()) {
            DatabaseManager.addProduct(currentUser.getId(), productName);
            loadProducts();
            productNameField.clear();
        }
    }

    private void handleEditProduct(Product product) {
        if (product == null) return;

        // Use the custom dialog instead of TextInputDialog
        CustomEditDialog dialog = new CustomEditDialog(product.getProductName());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            if (!newName.trim().isEmpty() && !newName.equals(product.getProductName())) {
                DatabaseManager.updateProduct(product.getProductId(), newName.trim());
                loadProducts(); // Refresh the list
            }
        });
    }

    private void handleDeleteProduct(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText("Are you sure you want to delete this product?");
        alert.setContentText( "Product: " + product.getProductName());


        alert.setGraphic(null);

        // Apply styles to the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ledgerly/app/css/styles.css")).toExternalForm());
        dialogPane.getStyleClass().add("modal-root");
        dialogPane.lookup(".header-panel").getStyleClass().add("subtitle-label");

        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);

        okButton.getStyleClass().add("add-button");
        cancelButton.getStyleClass().add("cancel-button");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DatabaseManager.deleteProduct(product.getProductId());
            loadProducts();
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) addProductButton.getScene().getWindow();
        stage.close();
    }
}
