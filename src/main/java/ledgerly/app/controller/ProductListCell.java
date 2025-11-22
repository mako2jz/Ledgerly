package ledgerly.app.controller;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import ledgerly.app.model.Product;

import java.util.function.Consumer;

import static ledgerly.app.util.SvgLoader.createSvgGraphic;

public class ProductListCell extends ListCell<Product> {

    private final HBox content;
    private final Label nameLabel;
    private final Consumer<Product> editHandler;
    private final Consumer<Product> deleteHandler;

    public ProductListCell(Consumer<Product> editHandler, Consumer<Product> deleteHandler) {
        super();
        this.editHandler = editHandler;
        this.deleteHandler = deleteHandler;

        nameLabel = new Label();
        nameLabel.getStyleClass().add("user-name-label");

        Button editButton = new Button();
        editButton.getStyleClass().add("edit-icon-button");
        editButton.setFocusTraversable(false);
        editButton.setOnAction(e -> {
            Product p = getItem();
            if (p != null && this.editHandler != null) this.editHandler.accept(p);
        });

        Button deleteButton = new Button();
        deleteButton.getStyleClass().add("delete-icon-button");
        deleteButton.setFocusTraversable(false);
        deleteButton.setOnAction(e -> {
            Product p = getItem();
            if (p != null && this.deleteHandler != null) this.deleteHandler.accept(p);
        });

        // Try to set icons (safe if SVG can't be loaded)
        Node editIcon = createSvgGraphic("/ledgerly/app/svg/pencil-square.svg", 16, Color.web("#007bff"));
        if (editIcon != null) editButton.setGraphic(editIcon);

        Node deleteIcon = createSvgGraphic("/ledgerly/app/svg/trash.svg", 16, Color.web("#dc3545"));
        if (deleteIcon != null) deleteButton.setGraphic(deleteIcon);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        content = new HBox(8, nameLabel, spacer, editButton, deleteButton);
        content.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    protected void updateItem(Product product, boolean empty) {
        super.updateItem(product, empty);
        setText(null);
        if (empty || product == null) {
            setGraphic(null);
        } else {
            nameLabel.setText(product.getProductName());
            setGraphic(content);
        }
    }
}
