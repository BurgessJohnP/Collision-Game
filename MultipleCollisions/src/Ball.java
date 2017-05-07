
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Ball {
    private float x;
    private float y;

    private float xSpeed;
    private float ySpeed;

    private float radius;
    private float mass;

    private Color color;

    public Ball(float x, float y, float radius, Color color) {
        this.x = x;
        this.y = y;

        this.radius = radius;
        this.mass = (float) (Math.PI * radius * radius);

        this.color = color;
    }

    /*
     * Creates a visual representation of the ball, using its coordinates and
     * radius
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Ellipse2D.Double circle = new Ellipse2D.Double(
                this.getX() - this.getRadius(), this.getY() - this.getRadius(),
                this.getRadius() * 2, this.getRadius() * 2);
        g2d.setColor(this.color);
        g2d.fill(circle);

    }

    /*
     * Update the ball's location, using previous location and current speed.
     * Checks if ball runs into any of the four edges of the window and adjusts
     * its location/speed accordingly.
     */
    public void move() {
        /* Setting new x-coordinate */
        this.setX(this.getX() + this.getxSpeed());

        /* Checking if ball hit left or right wall */
        if (this.getX() - this.getRadius() < 0) {
            this.setX(this.getRadius());
            this.setxSpeed(-this.getxSpeed());
        } else if (this.getX()
                + this.getRadius() > CollectionOfBalls.WINDOWWIDTH) {
            this.setX(CollectionOfBalls.WINDOWWIDTH - this.getRadius());
            this.setxSpeed(-this.getxSpeed());
        }

        /* Setting new y-coordinate */
        this.setY(this.getY() + this.getySpeed());

        /* Checking if ball hit top or bottom wall */
        if (this.getY() - this.getRadius() < 0) {
            this.setY(this.getRadius());
            this.setySpeed(-this.getySpeed());
        } else if (this.getY()
                + this.getRadius() > CollectionOfBalls.WINDOWHEIGHT) {
            this.setY(CollectionOfBalls.WINDOWHEIGHT - this.getRadius());
            this.setySpeed(-this.getySpeed());
        }
    }

    /**
     * @return the speed along the x-axis
     */
    public float getxSpeed() {
        return this.xSpeed;
    }

    /**
     * @return the speed along the y-axis
     */
    public float getySpeed() {
        return this.ySpeed;
    }

    /**
     * @param xSpeed
     *            the new speed along the x-axis
     */
    public void setxSpeed(float xSpeed) {
        this.xSpeed = xSpeed;
    }

    /**
     * @param xSpeed
     *            the new speed along the y-axis
     */
    public void setySpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }

    /**
     * @return the x-coordinate
     */
    public float getX() {
        return this.x;
    }

    /**
     * @return the y-coordinate
     */
    public float getY() {
        return this.y;
    }

    /**
     * @param x
     *            the new x-coordinate
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @param y
     *            the new y-coordinate
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return the radius
     */
    public float getRadius() {
        return this.radius;
    }

    /**
     * @return the mass
     */
    public float getMass() {
        return this.mass;
    }

}
