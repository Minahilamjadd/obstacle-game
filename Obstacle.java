package example.com.finalgameproject;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Obstacle extends ImageView {
    private double speed;
    private boolean isPassed = false;

    public Obstacle(Image image, double x, double y, double speed) {
        super(image);
        this.setFitWidth(50);
        this.setFitHeight(50);
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.speed = speed;
    }

    public void move() {

        this.setLayoutX(this.getLayoutX() - speed);
    }

    public boolean isOffScreen() {

        return this.getLayoutX() + this.getFitWidth() < 0;
    }

    public boolean isPassed() {

        return isPassed;
    }

    public void setPassed(boolean passed) {

        isPassed = passed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}