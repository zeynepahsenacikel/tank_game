import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Explosion {
    public enum ExplosionType {
        LARGE, //Tank destruction
        SMALL //Bullet hitting wall
    }

    private double x, y;
    private Image largeImage;
    private Image smallImage;
    private long startTime;
    private boolean active = true;
    private long duration;
    private ExplosionType type;

    /**
     * Constructs a new Explosion object.
     * x The x-coordinate of the explosion
     * y The y-coordinate of the explosion
     * type The type of explosion (LARGE or SMALL)
     */
    public Explosion(double x, double y, ExplosionType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.largeImage = new Image("file:assets/explosion.png");
        this.smallImage = new Image("file:assets/smallExplosion.png");
        this.startTime = System.currentTimeMillis();
        this.duration = (type == ExplosionType.LARGE) ? 500 : 300;
    }

    /**
     * Renders the explosion on the game canvas.
     */
    public void render(GraphicsContext gc) {
        if (active) {
            if (type == ExplosionType.LARGE) {
                gc.drawImage(largeImage, x, y, 35, 35);
            } else {
                gc.drawImage(smallImage, x, y, 15, 15);
            }

            if (System.currentTimeMillis() - startTime > duration) {
                active = false;
            }
        }
    }


    public boolean isActive() {
        return active;
    }
}
