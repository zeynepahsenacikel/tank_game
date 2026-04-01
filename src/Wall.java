import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;

/**
 * Represents a wall obstacle in the game. Walls block tank movement
 * and can be hit by bullets.
 */
public class Wall {
    private double x, y;
    private Image image;

    /**
     * Constructs a new Wall object.
     * x The x-coordinate of the wall
     * y The y-coordinate of the wall
     */

    public Wall(double x, double y) {
        this.x = x;
        this.y = y;
        this.image = new Image("file:assets/wall.png");
    }

    /**
     * Renders the wall on the game canvas.
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, 20, 20); //40 40
    }

    /**
     * Gets the collision bounds of the wall.
     */
    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, 20, 20); //40 40
    }
}
