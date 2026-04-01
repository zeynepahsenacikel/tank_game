import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;
import java.util.List;

/**
 * Represents the player's tank. Handles player input, movement,
 * firing bullets, and collision detection.
 */
public class PlayerTank {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    private boolean alive = true;
    private double x, y;
    private double speed = 3;
    private Image image;
    private boolean up, down, left, right;
    private List<Bullet> bullets;
    private long lastFireTime =0;
    private int direction = 0; //0:up, 1:down, 2:left, 3:right

    /**
     * Creates a new player tank.
     * x Initial x-coordinate
     * y Initial y-coordinate
     * bullets Reference to game's bullet list
     */
    public PlayerTank(double x, double y, List<Bullet> bullets) {
        this.x = x;
        this.y = y;
        this.bullets = bullets;
        this.image = new Image("file:assets/yellowTank1.png");
    }

    /**
     * Updates the tank's position and state.
     * walls List of walls to check for collisions
     * enemies List of enemies to check for collisions
     */
    public void update(List<Wall> walls, List<EnemyTank> enemies) {
        double newX = x;
        double newY = y;

        if (up) {
            newY = y - speed;
            direction = 0; //up
        }
        if (down) {
            newY = y + speed;
            direction = 1; //down
        }
        if (left) {
            newX = x - speed;
            direction = 2; //left
        }
        if (right) {
            newX = x + speed;
            direction = 3; //right
        }

        Rectangle2D newBounds = new Rectangle2D(newX, newY, WIDTH, HEIGHT);

        boolean canMove = true;
        for (Wall wall : walls) {
            if (newBounds.intersects(wall.getBounds())) {
                canMove = false;
                break;
            }
        }

        for (EnemyTank enemy : enemies) {
            if (newBounds.intersects(enemy.getBounds())) {
                canMove = false;
                break;
            }
        }

        if (canMove && newX >= 0 && newY <= 780 && newY >= 0 && newY <=580) {
            x = newX;
            y = newY;
        }

        for (EnemyTank enemy : enemies) {
            if (newBounds.intersects(enemy.getBounds())) {
                canMove = false;

                double pushX = x - enemy.getX();
                double pushY = y - enemy.getY();
                double length = Math.sqrt(pushX * pushX + pushY * pushY);

                if (length > 0) {
                    pushX = pushX/length * speed;
                    pushY = pushY/length * speed;

                    x += pushX;
                    y += pushY;
                }
                break;
            }
        }
    }

    /**
     * Renders the tank on screen with proper rotation based on direction.
     */
    public void render(GraphicsContext gc) {
        double centerX = x + 10; //20
        double centerY = y + 10; //20

        gc.save();

        gc.translate(centerX, centerY);
        switch (direction) {
            case 0: //up
                gc.rotate(270);
                break;
            case 1: //down
                gc.rotate(90);
                break;
            case 2: //left
                gc.rotate(180);
                break;
            case 3: //right
                break;
        }

        gc.drawImage(image, -10, -10, 20, 20); //-20 -20 40 40

        gc.restore();
    }

    /**
     * Fires a bullet in the current facing direction if cooldown has expired.
     */
    public void fire() {
        long now = System.currentTimeMillis();
        if (now - lastFireTime > 500) {
            switch (direction) {
                case 0: //up
                    bullets.add(new Bullet(x + 5, y, 0,-5, true));
                    break;
                case 1: //down
                    bullets.add(new Bullet(x + 5, y + 20, 0,5, true));
                    break;
                case 2: //left
                    bullets.add(new Bullet(x, y + 5, -5, 0, true));
                    break;
                case 3: //right
                bullets.add(new Bullet(x + 20, y + 5, 5, 0, true));
            }
            lastFireTime = now;
        }
    }

    public void setUp(boolean up) {this.up = up;} //Sets the upward movement state.
    public void setDown(boolean down) {this.down = down;} //Sets the downward movement state.
    public void setLeft(boolean left) {this.left = left;} //Sets the leftward movement state.
    public void setRight(boolean right) {this.right = right;} //Sets the rightward movement state.

    /**
     * Gets the collision bounds of the tank.
     */
    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, 20, 20); //40 40
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
