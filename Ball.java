package example.com.finalgameproject;

import javafx.animation.FadeTransition;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Ball extends ImageView {
    private double velocityY = 0;
    private double velocityX = 0;
    private final double gravity = 0.7;
    private final double jumpStrength = -15;
    private boolean isOnGround = false;
    private boolean hasMidAirJump = true;
    private final BackgroundManager backgroundManager;
    private final Pane gameRoot;

    public Ball(Image image, double x, double y, Pane gameRoot, BackgroundManager backgroundManager) {
        super(image);
        this.backgroundManager = backgroundManager;
        this.gameRoot = gameRoot;
        setFitWidth(70);
        setFitHeight(70);
        setLayoutX(x);
        setLayoutY(y);
        gameRoot.getChildren().add(this);
    }

    public void update() {
        if (!isOnGround) velocityY += gravity;

        setLayoutY(getLayoutY() + velocityY);
        setLayoutX(getLayoutX() + velocityX);

        // Screen bounds
        if (getLayoutX() < 0) setLayoutX(0);
        if (getLayoutX() > 730) setLayoutX(730);

        // Precise platform collision
        Bounds ballBounds = getBoundsInParent();
        Bounds platformBounds = backgroundManager.getPlatform().getBoundsInParent();
        double platformTop = platformBounds.getMinY();

        // When ball's bottom reaches platform top
        if (ballBounds.getMaxY() >= platformTop &&
                ballBounds.getMinX() < platformBounds.getMaxX() &&
                ballBounds.getMaxX() > platformBounds.getMinX()) {

            // Snap to platform exactly
            setLayoutY(platformTop - getFitHeight());
            isOnGround = true;
            hasMidAirJump = true;
            velocityY = 0;
        } else {
            isOnGround = false;
        }

        // Safety net
        if (getLayoutY() > 580) {
            setLayoutY(580);
            isOnGround = true;
            velocityY = 0;
        }
    }

    // Precise collision detection with other objects
    public boolean collidesWith(ImageView other) {
        Bounds ballBounds = getBoundsInParent();
        Bounds otherBounds = other.getBoundsInParent();

        return ballBounds.intersects(otherBounds) &&
                Math.abs(ballBounds.getCenterX() - otherBounds.getCenterX()) <
                        (ballBounds.getWidth() + otherBounds.getWidth())/2 - 10 &&
                Math.abs(ballBounds.getCenterY() - otherBounds.getCenterY()) <
                        (ballBounds.getHeight() + otherBounds.getHeight())/2 - 10;
    }

    public void jump() {
        if (isOnGround) {
            velocityY = jumpStrength;
            isOnGround = false;
            spawnJumpParticles(Color.LIMEGREEN);
        } else if (hasMidAirJump) {
            velocityY = jumpStrength * 0.8;
            hasMidAirJump = false;
            spawnJumpParticles(Color.DODGERBLUE);
        }
    }

    private void spawnJumpParticles(Color color) {
        for (int i = 0; i < 5; i++) {
            Circle particle = new Circle(2 + Math.random() * 3, color);
            particle.setCenterX(getLayoutX() + getFitWidth()/2);
            particle.setCenterY(getLayoutY() + getFitHeight());
            gameRoot.getChildren().add(particle);

            FadeTransition ft = new FadeTransition(Duration.millis(300), particle);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(e -> gameRoot.getChildren().remove(particle));
            ft.play();
        }
    }

    public void moveLeft() {
        velocityX = -5;
    }
    public void moveRight() {
        velocityX = 5;
    }
    public void stopMoving() {
        velocityX = 0;
    }
}