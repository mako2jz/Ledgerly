package ledgerly.app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ledgerly.app.db.DatabaseManager;
import ledgerly.app.model.Product;
import ledgerly.app.model.User;
import ledgerly.app.util.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.List;

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
    private VBox toastContainer;

    @FXML
    public void initialize() {
        addProductButton.setDisable(true);

        productNameField.textProperty().addListener((obs, old, val) -> {
            addProductButton.setDisable(val.trim().isEmpty());
        });

        productListView.setCellFactory(param -> new ProductListCell(this::handleEditProduct, this::handleDeleteProduct));

        if (toastContainer != null) {
            toastContainer.setPickOnBounds(false);
            toastContainer.setMaxWidth(Region.USE_PREF_SIZE);
        }
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
            Toast.show(toastContainer, "Product added successfully!");
        }
    }

    private void handleEditProduct(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ledgerly/app/view/EditProductView.fxml"));
            Parent root = loader.load();

            EditProductController controller = loader.getController();
            controller.initData(product);

            Stage dialogStage = createModalStage("Edit Product", root);
            dialogStage.showAndWait();

            String newName = controller.getNewProductName();
            if (newName != null && !newName.isEmpty() && !newName.equals(product.getProductName())) {
                DatabaseManager.updateProduct(product.getProductId(), newName);
                loadProducts();
                Toast.show(toastContainer, "Product updated successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteProduct(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ledgerly/app/view/DeleteProductView.fxml"));
            Parent root = loader.load();

            DeleteProductController controller = loader.getController();
            controller.initData(product);

            Stage dialogStage = createModalStage("Delete Product", root);
            dialogStage.showAndWait();

            if (controller.isConfirmed()) {
                DatabaseManager.deleteProduct(product.getProductId());
                loadProducts();
                Toast.show(toastContainer, "Product deleted successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Stage createModalStage(String title, Parent root) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(productListView.getScene().getWindow());
        dialogStage.initStyle(StageStyle.UNDECORATED);

        Scene scene = new Scene(root);
        URL cssUrl = getClass().getResource("/ledgerly/app/css/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        return dialogStage;
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) addProductButton.getScene().getWindow();
        stage.close();
    }
}
