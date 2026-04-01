import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;
import java.util.List;
import java.util.Random;

/**
 * Represents an enemy tank in the game. Handles AI movement, firing bullets,
 * and collision detection with walls and player.
 */
public class EnemyTank {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    private double x,y;
    private double speed = 1.2;
    private Image image;
    private boolean alive = true;

    private List<Bullet> bullets;
    private Random random = new Random();
    private int direction = 0; // 0:up, 1:down, 2:left, 3:right
    private long lastDirectionChange = 0;
    private long lastFireTime = 0;

    /**
     * Constructs a new EnemyTank object.
     * x The initial x-coordinate of the tank
     * y The initial y-coordinate of the tank
     * bullets The list of bullets to add fired bullets to
     */
    public EnemyTank(double x, double y, List<Bullet> bullets) {
        this.x = x;
        this.y = y;
        this.bullets = bullets;
        this.image = new Image("file:assets/whiteTank1.png");
    }

    /**
     * Updates the enemy tank's position and behavior.
     */
    public void update(List<Wall> walls, PlayerTank player) {
        //change direction
        long now = System.currentTimeMillis();
        if (now - lastDirectionChange > 2000) {
            direction = random.nextInt(4);
            lastDirectionChange = now;
        }

        double newX = x;
        double newY = y;

        //move
        switch (direction) {
            case 0: newY = y - speed; break; //up
            case 1: newY = y + speed; break; //down
            case 2: newX = x - speed; break; //left
            case 3: newX = x + speed; break; //right
        }

        Rectangle2D newBounds = new Rectangle2D(newX, newY, WIDTH, HEIGHT);
        boolean canMove = true;

        for (Wall wall : walls) {
            if (newBounds.intersects(wall.getBounds())) {
                canMove = false;
                direction = random.nextInt(4);
                break;
            }
        }

        if (canMove) {
            if (newX >= 0 && newX <= 780) {
                x = newX;
            } else {
                direction = random.nextInt(4);
            }
            if (newY >= 0 && newY <= 580) {
                y = newY;
            } else {
                direction = random.nextInt(4);
            }
        }

        if (newBounds.intersects(player.getBounds())) {
            canMove = false;
        }

        //ateş et
        if (now - lastFireTime > 1000 + random.nextInt(1000)) {
            switch (direction) {
                case 0:
                    bullets.add(new Bullet(x + 5, y, 0,-5, false));
                    break;
                case 1:
                    bullets.add(new Bullet(x + 5, y + 20, 0,5, false));
                    break;
                case 2:
                    bullets.add(new Bullet(x, y + 5, -5, 0,false));
                    break;
                case 3:
                    bullets.add(new Bullet(x + 20, y + 5, 5, 0,false));
                    break;
            }
            lastFireTime = now;
        }
    }

    /**
     * Renders the enemy tank on the game canvas.
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
     * Gets the collision bounds of the enemy tank.
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
