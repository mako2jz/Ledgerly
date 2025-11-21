package ledgerly.app.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
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
    private ObservableList<Product> allProducts;
    private FilteredList<Product> filteredProducts;

    @FXML
    public void initialize() {
        // Only allow numbers in the amount field
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                amountField.setText(oldValue);
            }
        });

        // Set up a StringConverter to display product names and handle conversion
        productComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Product product) {
                return product == null ? null : product.getProductName();
            }

            @Override
            public Product fromString(String string) {
                // This is necessary for when an item is selected from the dropdown.
                // It ensures the ComboBox can find the Product object from its name.
                return allProducts.stream()
                        .filter(p -> p.getProductName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        // Add a listener to the editor's text property for autocompletion
        productComboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (filteredProducts == null) return;

            // The editor's text is the source of truth for the filter.
            // We must check if the new text is the same as the currently selected item's name.
            // If it is, we don't need to filter, which prevents recursion.
            Product selected = productComboBox.getSelectionModel().getSelectedItem();
            if (newText != null && selected != null && newText.equals(selected.getProductName())) {
                return;
            }

            // Update the filter predicate based on the new text.
            filteredProducts.setPredicate(product -> {
                if (newText == null || newText.isEmpty()) {
                    return true; // Show all products if the text is empty
                }
                String lowerCaseFilter = newText.toLowerCase();
                return product.getProductName().toLowerCase().contains(lowerCaseFilter);
            });

            // Show the dropdown with filtered results
            if (!productComboBox.isShowing() && !newText.isEmpty()) {
                productComboBox.show();
            }
        });

        // When focus is lost, validate the selection.
        productComboBox.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (wasFocused && !isNowFocused) {
                String text = productComboBox.getEditor().getText();
                if (text != null && !text.isEmpty()) {
                    // Find if the text matches any product name exactly (case-insensitive)
                    Product match = allProducts.stream()
                            .filter(p -> p.getProductName().equalsIgnoreCase(text))
                            .findFirst().orElse(null);

                    if (match != null) {
                        // A match was found, select it. This will update the editor text via the converter.
                        productComboBox.getSelectionModel().select(match);
                    } else {
                        // No match found. If an item was previously selected, revert to it. Otherwise, clear.
                        Product selected = productComboBox.getSelectionModel().getSelectedItem();
                        if (selected != null) {
                            productComboBox.getEditor().setText(selected.getProductName());
                        } else {
                            productComboBox.getEditor().clear();
                        }
                    }
                }
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

    public void initData(Sale sale, User user) {
        this.saleToEdit = sale;
        this.currentUser = user;

        // Populate fields
        saleIdLabel.setText(String.valueOf(sale.getSaleId()));
        dateLabel.setText(sale.getCreatedAt());
        amountField.setText(String.valueOf(sale.getAmount()));
        descriptionArea.setText(sale.getDescription());

        // Populate and set product ComboBox
        loadProducts();

        // Find and select the product associated with the sale
        Product saleProduct = allProducts.stream()
                .filter(p -> Objects.equals(p.getProductName(), sale.getProductName()))
                .findFirst()
                .orElse(null);

        if (saleProduct != null) {
            productComboBox.getSelectionModel().select(saleProduct);
        }
    }

    private void loadProducts() {
        List<Product> productList = DatabaseManager.getProductsForUser(currentUser.getId());
        allProducts = FXCollections.observableArrayList(productList);
        filteredProducts = new FilteredList<>(allProducts, p -> true);
        productComboBox.setItems(filteredProducts);
    }

    @FXML
    private void handleSave() {
        Product selectedProduct = productComboBox.getSelectionModel().getSelectedItem();
        String amountText = amountField.getText();
        String description = descriptionArea.getText();

        if (selectedProduct == null || amountText.isEmpty()) {
            System.err.println("Save button was clicked with empty required fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            DatabaseManager.updateSale(saleToEdit.getSaleId(), selectedProduct.getProductId(), amount, description);
            saleUpdated = true;
            closeStage();
        } catch (NumberFormatException e) {
            System.err.println("Invalid amount format: " + amountText);
        }
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
