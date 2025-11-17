package ledgerly.app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import ledgerly.app.model.Sale;
import ledgerly.app.Main;
import ledgerly.app.db.DatabaseManager;
import ledgerly.app.model.User;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DashboardController {

    private User currentUser;

    @FXML
    private Label sidebarUserLabel;
    @FXML
    private Label welcomeUserLabel;
    @FXML
    private Label totalSalesLabel;
    @FXML
    private Label numSalesLabel;
    @FXML
    private Label avgSaleLabel;
    @FXML
    private Button productsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button addSaleButton; // New button
    @FXML
    private TableView<Sale> salesTableView;
    @FXML
    private TableColumn<Sale, Integer> idColumn;
    @FXML
    private TableColumn<Sale, String> dateColumn;
    @FXML
    private TableColumn<Sale, String> productColumn;
    @FXML
    private TableColumn<Sale, String> descriptionColumn;
    @FXML
    private TableColumn<Sale, Double> amountColumn;
    @FXML
    private TableColumn<Sale, Void> actionsColumn;


    public void initialize() {
        productsButton.setGraphic(createSvgGraphic("/ledgerly/app/svg/basket.svg", 16, Color.web("white")));
        logoutButton.setGraphic(createSvgGraphic("/ledgerly/app/svg/arrow-bar-left.svg", 16, Color.web("#333333")));
        amountColumn.setCellFactory(new Callback<TableColumn<Sale, Double>, TableCell<Sale, Double>>() {
            @Override
            public TableCell<Sale, Double> call(TableColumn<Sale, Double> col) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Double value, boolean empty) {
                        super.updateItem(value, empty);
                        if (empty || value == null) {
                            setText(null);
                            getStyleClass().remove("amount-cell");
                        } else {
                            setText(String.format("₱%.2f", value));
                            if (!getStyleClass().contains("amount-cell")) {
                                getStyleClass().add("amount-cell");
                            }
                        }
                    }
                };
            }
        });

        setupActionsColumn();
    }

    public void initData(User user) {
        this.currentUser = user;
        sidebarUserLabel.setText(currentUser.getName());
        welcomeUserLabel.setText("Welcome, " + currentUser.getName());
        loadDashboardData();
    }

    private void loadDashboardData() {
        if (currentUser == null) return;

        // Load sales data
        List<Sale> sales = DatabaseManager.getSalesForUser(currentUser.getId());
        ObservableList<Sale> observableSales = FXCollections.observableArrayList(sales);

        // Populate stats
        double totalSales = sales.stream().mapToDouble(Sale::getAmount).sum();
        long numSales = sales.size();
        double avgSale = (numSales > 0) ? totalSales / numSales : 0;

        totalSalesLabel.setText(String.format("₱%.2f", totalSales));
        numSalesLabel.setText(String.valueOf(numSales));
        avgSaleLabel.setText(String.format("₱%.2f", avgSale));

        // Populate table
        idColumn.setCellValueFactory(new PropertyValueFactory<>("saleId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        salesTableView.setItems(observableSales);
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Sale, Void>, TableCell<Sale, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Sale, Void> call(final TableColumn<Sale, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button();
                    private final Button deleteButton = new Button();
                    private final HBox pane = new HBox(10, editButton, deleteButton);

                    {
                        pane.setAlignment(Pos.CENTER);
                        editButton.setGraphic(createSvgGraphic("/ledgerly/app/svg/pencil-square.svg", 14, Color.web("#007bff")));
                        deleteButton.setGraphic(createSvgGraphic("/ledgerly/app/svg/trash.svg", 14, Color.web("#dc3545")));
                        editButton.getStyleClass().add("edit-icon-button");
                        deleteButton.getStyleClass().add("delete-icon-button");

                        editButton.setOnAction(event -> {
                            Sale sale = getTableView().getItems().get(getIndex());
                            handleEditSale(sale);
                        });

                        deleteButton.setOnAction(event -> {
                            Sale sale = getTableView().getItems().get(getIndex());
                            handleDeleteSale(sale);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        };
        actionsColumn.setCellFactory(cellFactory);
    }

    private void handleEditSale(Sale sale) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ledgerly/app/view/EditSaleView.fxml"));
            Parent root = loader.load();

            EditSaleController controller = loader.getController();
            controller.initData(sale, currentUser);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Sale");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(salesTableView.getScene().getWindow());
            dialogStage.initStyle(StageStyle.UNDECORATED);

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/ledgerly/app/css/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (controller.isSaleUpdated()) {
                loadDashboardData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteSale(Sale sale) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Sale");
        alert.setHeaderText("Are you sure you want to delete this sale?");
        alert.setContentText(
                "Sale ID: " + sale.getSaleId() +
                        "\nDate: " + sale.getCreatedAt() +
                        "\nProduct: " + sale.getProductName() +
                        "\nAmount: " + sale.getAmount() +
                        "\nDescription: " + sale.getDescription()
        );


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
            DatabaseManager.deleteSale(sale.getSaleId());
            loadDashboardData();
        }
    }


    @FXML
    private void handleAddSaleButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ledgerly/app/view/AddSaleView.fxml"));
            Parent root = loader.load();

            AddSaleController controller = loader.getController();
            controller.initData(currentUser);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Sale");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(addSaleButton.getScene().getWindow());
            dialogStage.initStyle(StageStyle.UNDECORATED);

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/ledgerly/app/css/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            // If a sale was added, refresh the dashboard
            if (controller.isSaleAdded()) {
                loadDashboardData();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProductsButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ledgerly/app/view/ProductView.fxml"));
            Parent root = loader.load();

            ProductController controller = loader.getController();
            controller.initData(currentUser);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Manage Products");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(productsButton.getScene().getWindow());
            dialogStage.initStyle(StageStyle.UNDECORATED);

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/ledgerly/app/css/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            // Refresh sales data in case product names changed
            loadDashboardData();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogoutButton() {
        try {
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();

            // Re-launch the main application window
            Stage mainStage = new Stage();
            new Main().start(mainStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Node createSvgGraphic(String resourcePath, double targetSizePx, Color fill) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Cannot find SVG resource: " + resourcePath);
                return null;
            }
            String svg = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Pattern pathPattern = Pattern.compile("(?i)d\\s*=\\s*['\"]([^'\"]+)['\"]");
            Matcher pathMatcher = pathPattern.matcher(svg);
            Group group = new Group();
            while (pathMatcher.find()) {
                SVGPath svgPath = new SVGPath();
                svgPath.setContent(pathMatcher.group(1));
                svgPath.setFill(fill);
                group.getChildren().add(svgPath);
            }
            if (group.getChildren().isEmpty()) return null;

            double originalWidth = 16; // Default
            Pattern vbPattern = Pattern.compile("(?i)viewBox\\s*=\\s*['\"]([-\\d\\.]+)\\s+([-\\d\\.]+)\\s+([-\\d\\.]+)\\s+([-\\d\\.]+)['\"]");
            Matcher vbMatcher = vbPattern.matcher(svg);
            if (vbMatcher.find()) {
                try {
                    originalWidth = Double.parseDouble(vbMatcher.group(3));
                } catch (NumberFormatException ignored) {}
            }
            double scale = (originalWidth > 0) ? targetSizePx / originalWidth : 1.0;
            group.setScaleX(scale);
            group.setScaleY(scale);
            return group;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
