package ledgerly.app.util;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SvgLoader {

    /**
     * Creates a JavaFX Node from an SVG file, allowing customization of size and color.
     * This method is designed to parse simple SVG files containing <path> elements.
     *
     * @param resourcePath The path to the SVG resource file (e.g., "/ledgerly/app/svg/icon.svg").
     * @param targetSize   The desired width and height of the icon.
     * @param fill         The color to apply to the SVG paths.
     * @return A Node representing the SVG graphic, or null if loading fails.
     */
    public static Node createSvgGraphic(String resourcePath, double targetSize, Color fill) {
        try (InputStream is = SvgLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("SVG resource not found: " + resourcePath);
                return null;
            }
            String svg = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // Regex to find path data (d="...")
            Pattern pathPattern = Pattern.compile("(?i)d\\s*=\\s*['\"]([^'\"]+)['\"]");
            Matcher pathMatcher = pathPattern.matcher(svg);

            Group group = new Group();
            while (pathMatcher.find()) {
                SVGPath svgPath = new SVGPath();
                svgPath.setContent(pathMatcher.group(1));
                svgPath.setFill(fill);
                group.getChildren().add(svgPath);
            }

            if (group.getChildren().isEmpty()) {
                return null; // No paths found
            }

            // Regex to find viewBox for scaling
            double originalWidth = 16; // Default SVG size
            Pattern vbPattern = Pattern.compile("(?i)viewBox\\s*=\\s*['\"]([-\\d\\.]+)\\s+([-\\d\\.]+)\\s+([-\\d\\.]+)\\s+([-\\d\\.]+)['\"]");
            Matcher vbMatcher = vbPattern.matcher(svg);
            if (vbMatcher.find()) {
                try {
                    originalWidth = Double.parseDouble(vbMatcher.group(3));
                } catch (NumberFormatException ignored) {
                    // Keep default width if viewBox width is not a valid number
                }
            }

            double scale = (originalWidth > 0) ? targetSize / originalWidth : 1.0;
            group.setScaleX(scale);
            group.setScaleY(scale);

            // Ensure the icon itself doesn't intercept mouse events
            group.setMouseTransparent(true);

            return group;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Overloaded method that defaults to a size of 16x16 and a white fill.
     */
    public static Node createSvgGraphic(String resourcePath) {
        return createSvgGraphic(resourcePath, 16, Color.WHITE);
    }
}
