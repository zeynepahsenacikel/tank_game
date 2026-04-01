import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;

/**
 /* Represents a bullet in the game. Bullets can be fired by both the player and enemy tanks.
 * */
public class Bullet {
    private double x, y;
    private double speedX, speedY;
    private boolean alive = true;
    private boolean fromPlayer; //if true it is from player else enemy

    private Image image;

    /**
     * Constructs a new Bullet object.
     * x The initial x-coordinate of the bullet
     * y The initial y-coordinate of the bullet
     * speedX The horizontal speed of the bullet
     * speedY The vertical speed of the bullet
     * fromPlayer True if bullet is fired by player, false if by enemy
     */
    public Bullet(double x, double y, double speedX, double speedY, boolean fromPlayer) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.fromPlayer = fromPlayer;
        this.image = new Image("file:assets/bullet.png");
    }

    /**
     * Updates the bullet's position and checks if it's out of bounds.
     */
    public void update() {
        x += speedX;
        y += speedY;

        if (y < 0 || y > 600) {
            alive = false;
        }
    }

    /**
     * Renders the bullet on the game canvas.
     * gc The GraphicsContext used for drawing
     */
    public void render(GraphicsContext gc) {
        if (alive) {
            gc.drawImage(image, x, y, 5, 5); //10 10
        }
    }

    /**
     * Checks if the bullet is still active.
     */
    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Checks if the bullet was fired by the player.
     */
    public boolean isFromPlayer() {
        return fromPlayer;
    }

    /**
     * Gets the collision bounds of the bullet.
     */
    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, 5, 5); //10 10
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}