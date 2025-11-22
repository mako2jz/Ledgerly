package ledgerly.app.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ledgerly.app.db.DatabaseManager;
import ledgerly.app.model.Product;
import ledgerly.app.model.User;

import java.util.List;
import java.util.stream.Collectors;

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
    private ObservableList<Product> allProducts;

    @FXML
    public void initialize() {
        // Only allow numbers in the amount field
        amountField.textProperty().addListener((observable, oldValue, newValue)
                -> { if (!newValue.matches("\\d*(\\.\\d*)?"))
                { amountField.setText(oldValue); }
        });

        // Set up a StringConverter to display product names
        productComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Product product) {
                return product == null ? null : product.getProductName();
            }

            @Override
            public Product fromString(String string) {
                // This allows selecting an item by typing its exact name
                return productComboBox.getItems().stream()
                        .filter(p -> p.getProductName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        // After selecting an item, move focus away
        productComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                amountField.requestFocus();
            }
        });

        // Optionally hide dropdown after selecting
        productComboBox.setOnAction(e -> productComboBox.hide());

        // Add a listener to the editor's text property for autocompletion
        productComboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                productComboBox.setItems(allProducts); // Show all if search is empty
            } else {
                // Filter the list of products based on the input text
                ObservableList<Product> filteredList = allProducts.stream()
                        .filter(p -> p.getProductName().toLowerCase().contains(newText.toLowerCase()))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
                productComboBox.setItems(filteredList);
            }
            // Show the dropdown with filtered results
            if (!productComboBox.isShowing()) {
                assert newText != null;
                if (!newText.isEmpty()) {
                    productComboBox.show();
                }
            }
        });

        // When focus is lost, if the text doesn't match an item, clear it.
        productComboBox.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (wasFocused && !isNowFocused) {
                Product selected = productComboBox.getSelectionModel().getSelectedItem();
                String text = productComboBox.getEditor().getText();
                if (selected == null && text != null && !text.isEmpty()) {
                    // Find if the text matches any product name
                    Product match = allProducts.stream()
                            .filter(p -> p.getProductName().equalsIgnoreCase(text))
                            .findFirst().orElse(null);
                    if (match != null) {
                        productComboBox.getSelectionModel().select(match);
                    } else {
                        productComboBox.getEditor().clear(); // Or handle as an error
                    }
                }
            }
        });

        // Disable button if required fields are empty
        addSaleButton.disableProperty().bind(
                Bindings.or(
                        productComboBox.valueProperty().isNull(),
                        amountField.textProperty().isEmpty() )
        );
    }

    public void initData(User user) {
        this.currentUser = user;
        loadProducts();
    }

    private void loadProducts() {
        List<Product> productList = DatabaseManager.getProductsForUser(currentUser.getId());
        allProducts = FXCollections.observableArrayList(productList);
        productComboBox.setItems(allProducts);
    }

    @FXML
    private void handleAddSale() {
        Product selectedProduct = productComboBox.getSelectionModel().getSelectedItem();
        String amountText = amountField.getText();
        String description = descriptionArea.getText();

        try {
            double amount = Double.parseDouble(amountText);
            DatabaseManager.addSale(currentUser.getId(), selectedProduct.getProductId(), amount, description);
            saleAdded = true;
            closeStage();
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
        }
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