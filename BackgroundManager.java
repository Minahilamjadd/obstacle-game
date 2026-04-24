package example.com.finalgameproject;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.Objects;

public class BackgroundManager {
    private ImageView background1, background2;
    private ImageView platform;
    private double speed = 3;
    private String theme;
    private double scrollSpeed;

    public BackgroundManager(Pane root, String theme) {
        this.theme = theme.toLowerCase();

        try {
            // Background setup
            String bgPath = "/images/" + this.theme + "_background.jpg";
            Image bgImage = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(bgPath)));

            background1 = new ImageView(bgImage);
            background2 = new ImageView(bgImage);

            background1.setFitWidth(800);
            background1.setFitHeight(600);
            background2.setFitWidth(800);
            background2.setFitHeight(600);
            background1.setX(0);
            background2.setX(800);

            // Platform setup
            Image platformImage = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/images/platform.png")));
            platform = new ImageView(platformImage);
            platform.setFitWidth(800);
            platform.setFitHeight(20);
            platform.setLayoutY(580);

            root.getChildren().addAll(background1, background2, platform);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load assets for theme: " + theme, e);
        }
    }

    public void scrollBackground() {
        background1.setX(background1.getX() - scrollSpeed);
        background2.setX(background2.getX() - scrollSpeed);

        if (background1.getX() <= -800) background1.setX(background2.getX() + 800);
        if (background2.getX() <= -800) background2.setX(background1.getX() + 800);
    }

    public void setScrollSpeed(double speed) {
        this.scrollSpeed = speed;
    }

    public ImageView getPlatform() {
        return platform;
    }

    public double getPlatformTopY() {
        return platform.getLayoutY(); // Returns the top Y coordinate of the platform
    }
}