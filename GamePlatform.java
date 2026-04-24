package example.com.finalgameproject;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GamePlatform extends ImageView {
    //CONSTRUCTOR
    public GamePlatform(Image image, double x, double y) {
        super(image);
        this.setFitWidth(800);
        this.setFitHeight(80);
        this.setLayoutX(x);
        this.setLayoutY(y);
    }
}