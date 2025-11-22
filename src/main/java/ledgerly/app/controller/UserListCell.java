package ledgerly.app.controller;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import ledgerly.app.model.User;

public class UserListCell extends ListCell<User> {
    private final HBox content;
    private final Text initialsText;
    private final Label nameLabel;

    public UserListCell() {
        super();
        initialsText = new Text();
        initialsText.getStyleClass().add("avatar-text");

        StackPane avatarPane = new StackPane();
        avatarPane.getStyleClass().add("avatar-circle");
        avatarPane.setPrefSize(40, 40);
        avatarPane.getChildren().add(initialsText);

        nameLabel = new Label();
        nameLabel.getStyleClass().add("user-name-label");

        content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(avatarPane, nameLabel);
    }

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        if (empty || user == null) {
            setGraphic(null);
        } else {
            initialsText.setText(user.getInitials());
            nameLabel.setText(user.getName());
            setGraphic(content);
        }
    }
}
