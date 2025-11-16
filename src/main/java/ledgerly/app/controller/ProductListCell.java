package ledgerly.app.controller;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
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
    private final Button deleteButton;
    private final Button editButton;
    private final Consumer<Product> deleteCallback;
    private final Consumer<Product> editCallback;

    public ProductListCell(Consumer<Product> editCallback, Consumer<Product> deleteCallback) {
        super();
        this.editCallback = editCallback;
        this.deleteCallback = deleteCallback;

        nameLabel = new Label();
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        editButton = new Button();
        editButton.setGraphic(createSvgGraphic("/ledgerly/app/svg/pencil-square.svg", 16, Color.DARKGRAY));
        editButton.getStyleClass().add("edit-icon-button");

        deleteButton = new Button();
        deleteButton.setGraphic(createSvgGraphic("/ledgerly/app/svg/trash.svg", 16, Color.RED));
        deleteButton.getStyleClass().add("delete-icon-button");

        content = new HBox(10, nameLabel, spacer, editButton, deleteButton);
        content.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    protected void updateItem(Product product, boolean empty) {
        super.updateItem(product, empty);
        if (empty || product == null) {
            setGraphic(null);
        } else {
            nameLabel.setText(product.getProductName());
            editButton.setOnAction(event -> {
                if (editCallback != null) {
                    editCallback.accept(product);
                }
            });
            deleteButton.setOnAction(event -> {
                if (deleteCallback != null) {
                    deleteCallback.accept(product);
                }
            });
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
            return group;
        } catch (IOException ex) {
            return null;
        }
    }
}
