package ledgerly.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ledgerly.app.model.SampleModel;

public class MainController {

    @FXML
    private Label dataLabel;

    private final SampleModel model = new SampleModel();

    @FXML
    public void initialize() {
        model.setData("Hello from the model!");
        dataLabel.setText(model.getData());
    }

    @FXML
    private void handleButtonClick() {
        model.setData("Button clicked!");
        dataLabel.setText(model.getData());
    }
}

