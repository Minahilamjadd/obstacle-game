// Points.java
package example.com.finalgameproject;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Points extends ImageView {
    private double speed;

    public Points(Image image, double x, double y, double speed) {
        super(image);
        this.setFitWidth(50);
        this.setFitHeight(50);
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.speed = speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void move() {
        this.setLayoutX(this.getLayoutX() - speed);
    }

    public boolean isOffScreen() {
        return this.getLayoutX() + this.getFitWidth() < 0;
    }
}
