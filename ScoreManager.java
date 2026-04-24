package example.com.finalgameproject;

public class ScoreManager {
    private int score = 0;

    public void incrementBy(int value) {
        score += value;
    }

    public int getScore() {
        return score;
    }

    public void reset() {
        score = 0;
    }
}

