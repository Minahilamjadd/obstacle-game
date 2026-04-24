package example.com.finalgameproject;

import example.com.finalgameproject.auth.UserManager;
import example.com.finalgameproject.skins.BallSkin;
import example.com.finalgameproject.ui.*;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {
    private Stage primaryStage;
    private UserManager userManager;
    private Scene currentScene;
    private Ball ball;
    private BackgroundManager backgroundManager;
    private ScoreManager scoreManager = new ScoreManager();
    private Speed speedManager = new Speed();
    private List<Obstacle> obstacles = new ArrayList<>();
    private List<Points> points = new ArrayList<>();
    private Text scoreText;
    private Text feedbackText;
    private String currentTheme = "Fire";
    private boolean gameOver;
    private AnimationTimer gameLoop;
    private Random random = new Random();
    private boolean isGuestMode = false;

    // Constants for spawning
    private static final double OBSTACLE_SPAWN_RATE = 0.03;
    private static final double POINT_SPAWN_RATE = 0.02;
    private static final double MIN_DISTANCE_BETWEEN_OBSTACLES = 600;
    private static final double POINT_MIN_Y = 200;
    private static final double POINT_MAX_Y = 400;
    private static final double BASE_OBSTACLE_SPAWN_RATE = 0.015; // 1.5% base chance
    private static final double MAX_OBSTACLE_SPAWN_RATE = 0.025; // 2.5% max chance
    private static final double MIN_OBSTACLE_DISTANCE = 300; // Minimum 300px between obstacles


    public GameManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.userManager = new UserManager();
        showMainMenu();
    }

    public void showMainMenu() {
        MainMenuPane mainMenu = new MainMenuPane(new MainMenuPane.MainMenuListener() {
            @Override
            public void onPlayAsGuest() {
                isGuestMode = true;
                showThemeSelection();
            }

            @Override
            public void onLogin() {
                isGuestMode = false;
                showLoginScreen();
            }

            @Override
            public void onShowRankings() {
                showRankings();
            }
        });

        currentScene = new Scene(mainMenu, 800, 600);
        primaryStage.setScene(currentScene);
        primaryStage.show();
    }

    public void showLoginScreen() {
        LoginPane loginPane = new LoginPane(new LoginPane.LoginListener() {
            @Override
            public void onLoginSuccess(String username, String password) {
                if (userManager.login(username, password)) {
                    showThemeSelection();
                }
                else {
                    showAlert("Login Failed", "Invalid username or password");
                }
            }

            @Override
            public void onSignUp(String username, String password) {
                if (userManager.signUp(username, password)) {
                    showAlert("Success", "Account created successfully!");
                    showThemeSelection();
                }
                else {
                    showAlert("Error", "Username already exists");
                }
            }
        });

        currentScene.setRoot(loginPane);
    }

    private void showThemeSelection() {
        ThemeSelectionPane themePane = new ThemeSelectionPane(new ThemeSelectionPane.ThemeSelectionListener() {
            @Override
            public void onThemeSelected(String theme) {
                currentTheme = theme;
                startGame();
            }

            @Override
            public void onShowRankings() {
                showRankings();
            }

            @Override
            public void onLogout() {
                if (isGuestMode) {
                    showMainMenu();
                }
                else {
                    userManager.logout();
                    showLoginScreen();
                }
            }
        });

        currentScene.setRoot(themePane);
    }

    private void startGame() {
        Pane gamePane = new Pane();
        backgroundManager = new BackgroundManager(gamePane, currentTheme);

        BallSkin ballSkin = new ThemeFactory(currentTheme).createBallSkin();
        double platformTop = backgroundManager.getPlatform().getLayoutY();

        // Positioning the ball on the platform
        double ballY = platformTop - 70;  // 70 is ball height
        ball = new Ball(ballSkin.getImage(), 100, ballY, gamePane, backgroundManager);


        scoreManager.reset(); // Ensuring that the score starts at 0
        scoreText = new Text("Score: 0"); // Reinitializing the text
        scoreText.setStyle("-fx-font-size: 20; -fx-fill: white;");
        scoreText.setLayoutX(20);
        scoreText.setLayoutY(30);

        feedbackText = new Text();
        feedbackText.setStyle("-fx-font-size: 24; -fx-fill: gold; -fx-font-weight: bold;");
        feedbackText.setLayoutX(300);
        feedbackText.setLayoutY(100);
        feedbackText.setOpacity(0);

        gamePane.getChildren().addAll(scoreText, feedbackText);

        obstacles.clear();
        points.clear();
        scoreManager.reset();
        speedManager.reset();
        gameOver = false;

        if (currentScene == null) {
            currentScene = new Scene(gamePane, 800, 600);
            primaryStage.setScene(currentScene);
        }
        else {
            currentScene.setRoot(gamePane);
        }

        setupInputHandlers();
        startGameLoop();
    }

    private void startGameLoop() {
        if (gameLoop != null) gameLoop.stop();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver) updateGame();
            }
        };
        gameLoop.start();
    }

    private void updateGame() {
        // Updating speed FIRST based on current score
        speedManager.update(scoreManager.getScore());

        // Applying speed to all moving elements
        backgroundManager.setScrollSpeed(speedManager.getSpeed());

        ball.update();

        // Spawning obstacles and points using current speed
        if (obstacles.isEmpty() ||
                (obstacles.get(obstacles.size()-1).getLayoutX() < MIN_DISTANCE_BETWEEN_OBSTACLES &&
                        random.nextDouble() < OBSTACLE_SPAWN_RATE)) {
            spawnObstacle();
        }

        if (random.nextDouble() < POINT_SPAWN_RATE) {
            spawnPoint();
        }

        moveObstacles();
        movePoints();

        if (checkObstacleCollision()) gameOver();
    }

    private void spawnObstacle() {
        Obstacle obstacle = new Obstacle(
                new ThemeFactory(currentTheme).createObstacleSkin().getImage(),
                800,
                backgroundManager.getPlatform().getLayoutY() - 60,
                speedManager.getSpeed() // Passing current speed
        );
        obstacle.setFitWidth(60);
        obstacle.setFitHeight(60);
        obstacles.add(obstacle);
        ((Pane)currentScene.getRoot()).getChildren().add(obstacle);
    }



    private void spawnPoint() {
        Points point = new Points(
                new Image(getClass().getResourceAsStream("/images/star.png")),
                800,
                random.nextInt(200) + 200, // Stars between 200-400 Y
                speedManager.getSpeed() // Pass current speed
        );
        point.setFitWidth(40);
        point.setFitHeight(40);
        points.add(point);
        ((Pane)currentScene.getRoot()).getChildren().add(point);
    }


    private void moveObstacles() {
        obstacles.removeIf(obs -> {
            obs.move();
            if (obs.isOffScreen()) {
                ((Pane)currentScene.getRoot()).getChildren().remove(obs);
                return true;
            }
            return false;
        });
    }

    private void movePoints() {
        points.removeIf(point -> {
            point.move();
            if (point.isOffScreen()) {
                ((Pane)currentScene.getRoot()).getChildren().remove(point);
                return true;
            }
            if (ball.collidesWith(point)) {
                scoreManager.incrementBy(5); // Increase score
                ((Pane)currentScene.getRoot()).getChildren().remove(point);
                updateScoreDisplay(); // Add this line
                return true;
            }
            return false;
        });
    }

    private void updateScoreDisplay() {
        scoreText.setText("Score: " + scoreManager.getScore());

        // Showing appreciation every 15 points
        if (scoreManager.getScore() % 15 == 0) {
            showFeedback("Great! +" + scoreManager.getScore() + " points!");
        }
    }

    private void showFeedback(String message) {
        feedbackText.setText(message);
        feedbackText.setOpacity(1);

        FadeTransition ft = new FadeTransition(Duration.seconds(2), feedbackText);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
    }

    private boolean checkObstacleCollision() {
        return obstacles.stream().anyMatch(obs -> ball.collidesWith(obs));
    }

    private void gameOver() {
        gameOver = true;

        if (!isGuestMode) {
            userManager.updateCurrentUserScore(scoreManager.getScore());
        }

        VBox gameOverPane = new VBox(20);
        gameOverPane.setAlignment(Pos.CENTER);
        gameOverPane.setLayoutX(250);
        gameOverPane.setLayoutY(250);
        gameOverPane.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 20px;");

        Text gameOverText = new Text("Game Over! Score: " + scoreManager.getScore());
        gameOverText.setStyle("-fx-font-size: 30; -fx-fill: white;");

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> {
            ((Pane)currentScene.getRoot()).getChildren().remove(gameOverPane);
            startGame();
        });

        Button menuButton = new Button("Main Menu");
        menuButton.setOnAction(e -> {
            ((Pane)currentScene.getRoot()).getChildren().remove(gameOverPane);
            showThemeSelection();
        });

        gameOverPane.getChildren().addAll(gameOverText, restartButton, menuButton);
        ((Pane)currentScene.getRoot()).getChildren().add(gameOverPane);
    }

    private void showRankings() {
        RankingPane rankingPane = new RankingPane(userManager, new Runnable() {
            @Override
            public void run() {
                if (isGuestMode) {
                    showMainMenu();
                }
                else {
                    showThemeSelection();
                }
            }
        });
        currentScene.setRoot(rankingPane);
    }

    private void setupInputHandlers() {
        currentScene.setOnKeyPressed(e -> {
            if (gameOver) return;
            switch(e.getCode()) {
                case SPACE: ball.jump(); break;
                case LEFT: ball.moveLeft(); break;
                case RIGHT: ball.moveRight(); break;
            }
        });

        currentScene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) {
                ball.stopMoving();
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}