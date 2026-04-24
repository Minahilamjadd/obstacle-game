package example.com.finalgameproject;

import example.com.finalgameproject.skins.BallSkin;
import example.com.finalgameproject.skins.FireBall;
import example.com.finalgameproject.skins.IceBall;
import example.com.finalgameproject.skins.ConcreteBall;
import example.com.finalgameproject.skins.ObstacleSkin;
import example.com.finalgameproject.skins.FireObstacle;
import example.com.finalgameproject.skins.IceObstacle;
import example.com.finalgameproject.skins.ConcreteObstacle;

public class ThemeFactory {
    private final String theme;

    public ThemeFactory(String theme) {
        this.theme = theme;
    }

    public BallSkin createBallSkin() {
        switch (theme) {
            case "Fire":
                return new FireBall();
            case "Ice":
                return new IceBall();
            case "Concrete":
                return new ConcreteBall();
            default:
                throw new IllegalArgumentException("Unknown theme: " + theme);
        }
    }

    public ObstacleSkin createObstacleSkin() {
        switch (theme) {
            case "Fire":
                return new FireObstacle();
            case "Ice":
                return new IceObstacle();
            case "Concrete":
                return new ConcreteObstacle();
            default:
                throw new IllegalArgumentException("Unknown theme: " + theme);
        }
    }

    public String getTheme() {

        return theme;
    }
}
