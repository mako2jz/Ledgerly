package ledgerly.app.util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import static ledgerly.app.util.SvgLoader.createSvgGraphic;

public class Toast {

    /**
     * Displays a short-lived notification message in a specified container.
     * The toast appears with a slide-up and fade-in animation, stays for a few seconds,
     * and then disappears with a slide-down and fade-out animation.
     *
     * @param toastContainer The Pane container where the toast will be displayed.
     * @param message        The message to display in the toast.
     */
    public static void show(Pane toastContainer, String message) {
        if (toastContainer == null) return;

        Label toast = new Label(message);
        toast.getStyleClass().add("toast-label");

        // Optional: Add an icon to the toast
        Node icon = createSvgGraphic("/ledgerly/app/svg/check.svg");
        if (icon != null) {
            toast.setGraphic(icon);
            toast.setGraphicTextGap(8);
        }

        toast.setOpacity(0);
        toast.setTranslateY(20);
        toast.setMouseTransparent(true);
        toast.setMaxWidth(Region.USE_PREF_SIZE);

        toastContainer.getChildren().add(0, toast);

        // Show animation
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(400), toast);
        slideUp.setToY(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), toast);
        fadeIn.setToValue(1);
        ParallelTransition showTransition = new ParallelTransition(slideUp, fadeIn);

        // Hide animation
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setToValue(0);
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(400), toast);
        slideDown.setToY(20);
        ParallelTransition hideTransition = new ParallelTransition(slideDown, fadeOut);
        hideTransition.setOnFinished(e -> toastContainer.getChildren().remove(toast));

        // Sequence
        SequentialTransition sequentialTransition = new SequentialTransition(
                showTransition,
                new PauseTransition(Duration.seconds(3)),
                hideTransition
        );

        sequentialTransition.play();
    }
}
