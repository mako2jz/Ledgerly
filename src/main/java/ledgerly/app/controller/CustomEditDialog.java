package ledgerly.app.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class CustomEditDialog extends Dialog<String> {

    private final TextField textField;

    public CustomEditDialog(String defaultValue) {
        setTitle("Edit Product");
        Label headerLabel = new Label("Editing product: " + defaultValue);
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.getStyleClass().add("title-label-edit");
        getDialogPane().setHeader(headerLabel);

        // Create the content
        VBox content = new VBox(15);
        content.setPadding(new Insets(0, 30, 0, 30));

        Label contentLabel = new Label("New Name:");
        contentLabel.getStyleClass().add("subtitle-label");
        textField = new TextField(defaultValue);
        textField.getStyleClass().add("text-field");

        content.getChildren().addAll(contentLabel, textField);

        // Create buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cancelButtonType, okButtonType);

        // Style buttons
        Button okButton = (Button) getDialogPane().lookupButton(okButtonType);
        Button cancelButton = (Button) getDialogPane().lookupButton(cancelButtonType);
        okButton.getStyleClass().add("add-button");
        cancelButton.getStyleClass().add("cancel-button");

        // Center the button bar
        Node buttonBar = getDialogPane().lookup(".button-bar");
        if (buttonBar != null) {
            buttonBar.setStyle("-fx-alignment: center; -fx-padding: 10 0 10 0;");
        }

        getDialogPane().setContent(content);
        getDialogPane().getStylesheets().add(getClass().getResource("/ledgerly/app/css/styles.css").toExternalForm());
        getDialogPane().getStyleClass().add("modal-root");

        // Return the text field's value when OK is pressed
        setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return textField.getText();
            }
            return null;
        });
    }
}
