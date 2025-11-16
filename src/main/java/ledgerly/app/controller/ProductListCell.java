package ledgerly.app.controller;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import ledgerly.app.model.Product;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductListCell extends ListCell<Product> {

    private final HBox content;
    private final Label nameLabel;
    private final Button editButton;
    private final Button deleteButton;
    private final Consumer<Product> editHandler;
    private final Consumer<Product> deleteHandler;

    public ProductListCell(Consumer<Product> editHandler, Consumer<Product> deleteHandler) {
        super();
        this.editHandler = editHandler;
        this.deleteHandler = deleteHandler;

        nameLabel = new Label();
        nameLabel.getStyleClass().add("user-name-label");

        editButton = new Button();
        editButton.getStyleClass().add("edit-icon-button");
        // prevent the button from taking keyboard focus (avoid focus ring / visual interference)
        editButton.setFocusTraversable(false);
        editButton.setOnAction(e -> {
            Product p = getItem();
            if (p != null && this.editHandler != null) this.editHandler.accept(p);
        });

        deleteButton = new Button();
        deleteButton.getStyleClass().add("delete-icon-button");
        deleteButton.setFocusTraversable(false);
        deleteButton.setOnAction(e -> {
            Product p = getItem();
            if (p != null && this.deleteHandler != null) this.deleteHandler.accept(p);
        });

        // Try to set icons (safe if SVG can't be loaded)
        Node editIcon = createSvgGraphic("/ledgerly/app/svg/pencil-square.svg", 16, Color.web("#333333"));
        if (editIcon != null) editButton.setGraphic(editIcon);

        Node deleteIcon = createSvgGraphic("/ledgerly/app/svg/trash.svg", 16, Color.web("#333333"));
        if (deleteIcon != null) deleteButton.setGraphic(deleteIcon);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        content = new HBox(12, nameLabel, spacer, editButton, deleteButton);
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

    private Node createSvgGraphic(String resourcePath, double targetSize, Color fill) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) return null;
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
            double scale = (originalWidth > 0) ? targetSize / originalWidth : 1.0;
            group.setScaleX(scale);
            group.setScaleY(scale);

            // Make the graphic non-interactive so clicks/focus go to the Button, not the SVG nodes
            group.setMouseTransparent(true);
            return group;
        } catch (IOException ex) {
            return null;
        }
    }
}
