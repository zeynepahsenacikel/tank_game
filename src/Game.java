import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * Main game class that manages the game loop, rendering, input handling,
 * and game state (score, lives, game over conditions).
 */
public class Game {
    private static final int max_enemies = 10;

    private PlayerTank player;
    private List<EnemyTank> enemies = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();
    private List<Wall> walls = new ArrayList<>();
    private List<Explosion> explosions = new ArrayList<>();

    private Label scoreLabel;
    private Label livesLabel;
    private Label gameOverLabel;
    private Label pauseLabel;

    private int score = 0;
    private int lives = 3;
    private long lastEnemySpawnTime = 0;
    private Random random = new Random();
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private long respawnTime = 0;

    private GraphicsContext gc;

    /**
     * Starts the game and sets up the JavaFX stage.
     */
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        scoreLabel = new Label("Score: 0");
        livesLabel = new Label("Lives: 3");
        gameOverLabel = new Label("GAME OVER\nPress R to Restart\nESC to Exit");
        pauseLabel = new Label("PAUSED\nPress P to Resume\nESC to Exit");

        setLabelStyles(gameOverLabel);
        setLabelStyles(pauseLabel);
        scoreLabel.setFont(new Font(20));
        livesLabel.setFont(new Font(20));
        scoreLabel.setTextFill(Color.BLACK);
        livesLabel.setTextFill(Color.BLACK);
        scoreLabel.setLayoutX(10);
        scoreLabel.setLayoutY(10);
        livesLabel.setLayoutX(10);
        livesLabel.setLayoutY(40);

        gameOverLabel.setVisible(false);
        pauseLabel.setVisible(false);

        root.getChildren().addAll(scoreLabel, livesLabel, gameOverLabel, pauseLabel);

        Scene scene = new Scene(root, 800, 600, Color.SALMON);
        primaryStage.setTitle("Tank 2025");
        primaryStage.setScene(scene);
        primaryStage.show();

        initializeGame();

        scene.setOnKeyPressed(e -> handleKeyPress(e.getCode()));
        scene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));
    }

    /**
     * Initializes game objects and starts the game loop.
     */
    private void initializeGame() {
        isPaused = false;
        player = new PlayerTank(400, 500, bullets);
        createWalls();
        startGameLoop();
    }

    /**
     * Applies consistent styling to UI labels.
     */
    private void setLabelStyles(Label label) {
        label.setFont(new Font(30));
        label.setTextFill(Color.RED);
        label.setLayoutX(250);
        label.setLayoutY(250);
    }

    /**
     * Handles keyboard input when keys are pressed.
     */
    private void handleKeyPress(KeyCode code) {
        if (isGameOver) {
            if (code == KeyCode.R) restartGame();
            else if (code == KeyCode.ESCAPE) System.exit(0);
        } else if (isPaused) {
            if (code == KeyCode.P) isPaused = false;
            else if (code == KeyCode.R) restartGame();
            else if (code == KeyCode.ESCAPE) System.exit(0);
        } else {
            if (code == KeyCode.LEFT) player.setLeft(true);
            if (code == KeyCode.RIGHT) player.setRight(true);
            if (code == KeyCode.UP) player.setUp(true);
            if (code == KeyCode.DOWN) player.setDown(true);
            if (code == KeyCode.X) player.fire();
            if (code == KeyCode.P) isPaused = true;
        }
    }

    /**
     * Handles keyboard input when keys are released.
     */
    private void handleKeyRelease(KeyCode code) {
        if (!isGameOver && !isPaused) {
            if (code == KeyCode.LEFT) player.setLeft(false);
            if (code == KeyCode.RIGHT) player.setRight(false);
            if (code == KeyCode.UP) player.setUp(false);
            if (code == KeyCode.DOWN) player.setDown(false);
        }
    }

    private void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isPaused) {
                    renderGame();
                    return;
                }
                if (isGameOver) {
                    renderGame();
                    return;
                }

                updateGame();
                renderGame();
            }
        }.start();
    }

    private void updateGame() {
        if (isGameOver) return;

        if (lives <= 0) {
            isGameOver = true;
            return;
        }

        if (!player.isAlive()) {
            player = new PlayerTank(400, 500, bullets);
            player.setAlive(true);
        }

        // Player ve düşman güncellemeleri
        player.update(walls, enemies);
        for (EnemyTank enemy : enemies) {
            enemy.update(walls, player);
        }

        // Mermi ve patlama güncellemeleri
        updateBullets();
        updateExplosions();

        // Düşman spawn
        if (enemies.size() < max_enemies && System.currentTimeMillis() - lastEnemySpawnTime > 2000) {
            spawnEnemies();
            lastEnemySpawnTime = System.currentTimeMillis();
        }

        // Çarpışma kontrolü
        checkCollisions();
    }

    /**
     * Spawns new enemy tanks at valid positions.
     */
    private void spawnEnemies() {
        double x, y;
        boolean validPosition;
        int attempts = 0;

        do {
            validPosition = true;
            x = 100 + random.nextInt(600);
            y = 50 + random.nextInt(100);

            Rectangle2D newEnemyBounds = new Rectangle2D(x, y, EnemyTank.WIDTH, EnemyTank.HEIGHT);

            for (Wall wall : walls) {
                if (newEnemyBounds.intersects(wall.getBounds())) {
                    validPosition = false;
                    break;
                }
            }

            if (validPosition) {
                for (EnemyTank enemy : enemies) {
                    if (newEnemyBounds.intersects(enemy.getBounds())) {
                        validPosition = false;
                        break;
                    }
                }
            }

            attempts++;
            if (attempts > 50) {
                validPosition = true;
                break;
            }
        } while (!validPosition);
        enemies.add(new EnemyTank(x, y, bullets));
    }

    /**
     * Renders all game objects.
     */
    private void renderGame() {
        gc.setFill(Color.SALMON);
        gc.fillRect(0, 0, 800, 600);

        player.render(gc);
        for (Bullet bullet : bullets) {
            bullet.render(gc);
        }
        for (Wall wall : walls) {
            wall.render(gc);
        }
        for (EnemyTank enemy : enemies) {
            enemy.render(gc);
        }
        for (Explosion explosion : explosions) {
            explosion.render(gc);
        }

        scoreLabel.setText("Score: " + score);
        livesLabel.setText("Lives: " + lives);

        //game over
        if (isGameOver) {
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRect(0, 0, 800, 600);

            //game over
            gc.setFill(Color.MISTYROSE);
            gc.setFont(new Font("American Typewriter", 70));
            gc.fillText("GAME OVER!", 190, 220);

            //score
            gc.setFill(Color.MISTYROSE);
            gc.setFont(new Font("American Typewriter", 30));
            gc.fillText("Your Score: " + score, 290, 280);

            //restart and exit
            gc.setFont(new Font("American Typewriter", 24));
            gc.fillText("Press R to Restart", 295, 350);
            gc.fillText("Press ESC to Exit", 300, 380);
        }

        //pause
        if (isPaused) {
            gc.setFill(Color.rgb(0, 0, 0, 0.4));
            gc.fillRect(0, 0, 800, 600);

            //pause
            gc.setFill(Color.TAN);
            gc.setFont(new Font("American Typewriter", 70));
            gc.fillText("PAUSED", 260, 220);

            //resume, restart and exit
            gc.setFont(new Font("American Typewriter", 24));
            gc.fillText("Press P to Resume", 295, 280);
            gc.fillText("Press R to Restart", 300, 300);
            gc.fillText("Press ESC to Exit", 600/2, 320);
        }
    }

    /**
     * Updates all active bullets and removes inactive ones.
     */
    private void updateBullets() {
        Iterator<Bullet> bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet bullet =  bulletIter.next();
            bullet.update();
            if (!bullet.isAlive()) {
                bulletIter.remove();
            }
        }
    }

    /**
     * Updates all active explosions and removes inactive ones.
     */
    private void updateExplosions() {
        Iterator<Explosion> explosionIter = explosions.iterator();
        while (explosionIter.hasNext()) {
            Explosion explosion = explosionIter.next();
            if (!explosion.isActive()) {
                explosionIter.remove();
            }
        }
    }

    /**
     * Checks for collisions between bullets, tanks, and walls.
     */
    private void checkCollisions() {
        Iterator<Bullet> bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet bullet = bulletIter.next();

            Iterator<EnemyTank> enemyIter = enemies.iterator();
            while (enemyIter.hasNext()) {
                EnemyTank enemy = enemyIter.next();
                if (bullet.isFromPlayer() && enemy.getBounds().intersects(bullet.getBounds())) {
                    bullet.setAlive(false);
                    enemy.setAlive(false);
                    explosions.add(new Explosion (enemy.getX(), enemy.getY(), Explosion.ExplosionType.LARGE));
                    enemyIter.remove();
                    score += 10;
                    break;
                }
            }


            //Enemy tank destroyed
            for (EnemyTank enemy : enemies) {
                if (bullet.isFromPlayer() && enemy.getBounds().intersects(bullet.getBounds())) {
                    bullet.setAlive(false);
                    enemy.setAlive(false);
                    explosions.add(new Explosion(enemy.getX(), enemy.getY(), Explosion.ExplosionType.LARGE));

                    score += 10;
                }
            }

            //Player tank destroyed
            if (!bullet.isFromPlayer() && bullet.getBounds().intersects(player.getBounds())) {
                bullet.setAlive(false);
                player.setAlive(false);
                explosions.add(new Explosion(player.getX(), player.getY(), Explosion.ExplosionType.LARGE));
                lives--; //Can azalt

                if (lives <= 0) {
                    isGameOver = true;
                } else {
                    player = new PlayerTank(400, 500, bullets);
                    player.setAlive(false);
                }
            }

            //hit the wall
            for (Wall wall : walls) {
                if (wall.getBounds().intersects(bullet.getBounds())) {
                    bullet.setAlive(false);
                    explosions.add(new Explosion(bullet.getX(), bullet.getY(), Explosion.ExplosionType.SMALL));
                }
            }
        }

        enemies.removeIf(e -> !e.isAlive());
    }

    private void restartGame() {
        isGameOver = false;
        isPaused = false;
        gameOverLabel.setVisible(false);
        pauseLabel.setVisible(false);
        score = 0;
        lives = 3;
        bullets.clear();
        enemies.clear();
        explosions.clear();
        initializeGame();
    }

    private void createWalls() {
        //edge walls
        for (int i = 0; i < 800; i+=20) { //40
            walls.add(new Wall(i, 0));
            walls.add(new Wall(i, 580)); //560
        }
        for (int j = 0; j < 600; j+=20) { //40
            walls.add(new Wall(0, j));
            walls.add(new Wall(780, j)); //760
        }
        //inner walls
        walls.add(new Wall(240, 100));
        walls.add(new Wall(240, 120));
        walls.add(new Wall(260, 100));
        walls.add(new Wall(260, 120));
        walls.add(new Wall(280, 100));
        walls.add(new Wall(280, 120));
        walls.add(new Wall(300, 100));
        walls.add(new Wall(300, 120));
        walls.add(new Wall(320, 100));
        walls.add(new Wall(320, 120));
        walls.add(new Wall(340, 100));
        walls.add(new Wall(340, 120));
        walls.add(new Wall(360, 100));
        walls.add(new Wall(360, 120));
        walls.add(new Wall(380, 100));
        walls.add(new Wall(380, 120));
        walls.add(new Wall(400, 100));
        walls.add(new Wall(400, 120));
        walls.add(new Wall(420, 100));
        walls.add(new Wall(420, 120));
        walls.add(new Wall(440, 100));
        walls.add(new Wall(440, 120));
        walls.add(new Wall(460, 100));
        walls.add(new Wall(460, 120));
        walls.add(new Wall(480, 100));
        walls.add(new Wall(480, 120));
        walls.add(new Wall(500, 100));
        walls.add(new Wall(500, 120));
        walls.add(new Wall(520, 100));
        walls.add(new Wall(520, 120));
        walls.add(new Wall(540, 100));
        walls.add(new Wall(540, 120));
        walls.add(new Wall(320, 320));
        walls.add(new Wall(340, 320));
        walls.add(new Wall(340, 340));
        walls.add(new Wall(360, 320));
        walls.add(new Wall(360, 340));
        walls.add(new Wall(380, 320));
        walls.add(new Wall(380, 340));
        walls.add(new Wall(400, 320));
        walls.add(new Wall(400, 340));
        walls.add(new Wall(420, 320));
        walls.add(new Wall(420, 340));
        walls.add(new Wall(440, 320));
        walls.add(new Wall(440, 340));
        walls.add(new Wall(460, 320));
        walls.add(new Wall(120, 340));
        walls.add(new Wall(120, 360));
        walls.add(new Wall(140, 360));
        walls.add(new Wall(140, 380));
        walls.add(new Wall(160, 380));
        walls.add(new Wall(160, 400));
        walls.add(new Wall(180, 400));
        walls.add(new Wall(180, 420));
        walls.add(new Wall(200, 420));
        walls.add(new Wall(200, 440));
        walls.add(new Wall(220, 440));
        walls.add(new Wall(220, 460));
        walls.add(new Wall(240, 460));
        walls.add(new Wall(240, 480));
        walls.add(new Wall(540, 480));
        walls.add(new Wall(540, 460));
        walls.add(new Wall(560, 460));
        walls.add(new Wall(560, 440));
        walls.add(new Wall(580, 440));
        walls.add(new Wall(580, 420));
        walls.add(new Wall(600, 420));
        walls.add(new Wall(600, 400));
        walls.add(new Wall(620, 400));
        walls.add(new Wall(620, 380));
        walls.add(new Wall(640, 380));
        walls.add(new Wall(640, 360));
        walls.add(new Wall(660, 360));
        walls.add(new Wall(660, 340));
    }

    /**
     * Checks if a tank would collide with any walls at given position.
     */
    private boolean checkTankWallCollision(Rectangle2D tankBounds) {
        for (Wall wall : walls) {
            if (tankBounds.intersects(wall.getBounds())) {
                return true;
            }
        }
        return false;
    }
}