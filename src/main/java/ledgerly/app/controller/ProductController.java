package ledgerly.app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ledgerly.app.db.DatabaseManager;
import ledgerly.app.model.Product;
import ledgerly.app.model.User;

import java.util.List;
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
        if (product != null) {
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
