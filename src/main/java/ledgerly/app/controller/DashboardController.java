package ledgerly.app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import ledgerly.app.model.Sale;
import ledgerly.app.Main;
import ledgerly.app.db.DatabaseManager;
import ledgerly.app.model.User;
import ledgerly.app.util.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static ledgerly.app.util.SvgLoader.createSvgGraphic;

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
    @FXML
    private VBox toastContainer;


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

        if (toastContainer != null) {
            toastContainer.setPickOnBounds(false);
            toastContainer.setMaxWidth(Region.USE_PREF_SIZE);
        }
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
                    private final Button viewButton = new Button();
                    private final Button editButton = new Button();
                    private final Button deleteButton = new Button();
                    private final HBox pane = new HBox(10, viewButton, editButton, deleteButton);

                    {
                        pane.setAlignment(Pos.CENTER);
                        viewButton.setGraphic(createSvgGraphic("/ledgerly/app/svg/eye.svg", 14, Color.web("#919191")));
                        editButton.setGraphic(createSvgGraphic("/ledgerly/app/svg/pencil-square.svg", 14, Color.web("#007bff")));
                        deleteButton.setGraphic(createSvgGraphic("/ledgerly/app/svg/trash.svg", 14, Color.web("#dc3545")));
                        viewButton.getStyleClass().add("view-icon-button");
                        editButton.getStyleClass().add("edit-icon-button");
                        deleteButton.getStyleClass().add("delete-icon-button");

                        viewButton.setOnAction(event -> {
                            Sale sale = getTableView().getItems().get(getIndex());
                            handleViewSale(sale);
                        });

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

    private void handleViewSale(Sale sale) {
        if (sale == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ledgerly/app/view/ViewSaleView.fxml"));
            Parent root = loader.load();

            ViewSaleController controller = loader.getController();
            controller.initData(sale);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setTitle("View Sale");

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/ledgerly/app/css/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            stage.setScene(scene);


            // Set owner to the main window
            Stage owner = (Stage) salesTableView.getScene().getWindow();
            stage.initOwner(owner);

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
                Toast.show(toastContainer, "Sale updated successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteSale(Sale sale) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ledgerly/app/view/DeleteSaleView.fxml"));
            Parent root = loader.load();

            DeleteSaleController controller = loader.getController();
            controller.initData(sale);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Delete Sale");
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

            if (controller.isConfirmed()) {
                DatabaseManager.deleteSale(sale.getSaleId());
                loadDashboardData(); // Refresh the sales list
                Toast.show(toastContainer, "Sale deleted successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddSale() {
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
                Toast.show(toastContainer, "Sale added successfully!");
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

}
