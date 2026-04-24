package example.com.finalgameproject;

public class Speed {
    private double currentSpeed = 3.0;
    private int lastIncreasedAt = 0;

    public void update(int currentScore) {
        // Increase speed by 0.5 every 50 points
        if (currentScore >= lastIncreasedAt + 50) {
            currentSpeed += 0.5;
            lastIncreasedAt = currentScore;
            System.out.println("Speed increased to: " + currentSpeed); // Debug log
        }

        // Cap maximum speed at 11.0
        if (currentSpeed > 11.0) {
            currentSpeed = 11.0;
        }
    }

    public double getSpeed() {
        return currentSpeed;
    }

    public void reset() {
        currentSpeed = 3.0;
        lastIncreasedAt = 0;
    }
}