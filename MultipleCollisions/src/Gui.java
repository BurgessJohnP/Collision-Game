import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

public class Gui extends JPanel implements Runnable {

    // Keeps follow's mouse clicks
    private Point lastPoint = new Point(CollectionOfBalls.WINDOWWIDTH / 2,
            CollectionOfBalls.WINDOWHEIGHT / 2);

    private static final int NUMBEROFBALLS = 100;
    private static final float MAXRADIUS = 20;
    private static final float MINRADIUS = 10;
    private static final float MAXSTARTSPEED = 2;
    private static final float PLAYERBALLSPEED = 20;
    private static final float ALLOWEDOFFSET = 10;

    // Stores all "normal"(non-player) balls
    ArrayList<Ball> normalBallList;
    // Stores all balls that the player controls
    ArrayList<Ball> playerBallList;

    // Stores distance away from initial player ball of all player balls
    // This allows these ball to stay a constant distance from the player
    Map<Ball, float[]> playerBallDistance;

    Ball playerBall;
    boolean running = true;

    Thread animator;
    Ball b2;

    public Gui() {
        /* Updates lastPoint to the coordinates of the last mouse click */
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Gui.this.lastPoint = new Point(e.getX(), e.getY());
            }
        });

        /* Allows playerBall to follow mouse when dragged */
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Graphics g = Gui.this.getGraphics();
                Gui.this.lastPoint = new Point(e.getX(), e.getY());
                g.dispose();
            }
        });

        /* Creating the ball controlled by the player */
        this.playerBall = new Ball(CollectionOfBalls.WINDOWWIDTH / 2,
                CollectionOfBalls.WINDOWHEIGHT / 2, 20, Color.GRAY);

        /* Initializing list of non-player balls, then populating it */
        this.normalBallList = new ArrayList<Ball>();
        while (this.normalBallList.size() < NUMBEROFBALLS) {
            Ball newBall = new Ball(
                    (float) Math.random() * CollectionOfBalls.WINDOWWIDTH,
                    (float) Math.random() * CollectionOfBalls.WINDOWHEIGHT,
                    (float) Math.random() * MAXRADIUS + MINRADIUS, Color.BLACK);
            newBall.setxSpeed(
                    (float) (((Math.random() - 1) * 2) * MAXSTARTSPEED));
            newBall.setySpeed(
                    (float) (((Math.random() - 1) * 2) * MAXSTARTSPEED));

            /*
             * Only create this new ball if it does not initially collide with
             * any of the already created balls
             */
            boolean ballDoesNotCollide = true;
            for (int i = 0; i < this.normalBallList.size()
                    && ballDoesNotCollide; i++) {
                ballDoesNotCollide = !(this.checkCollision(newBall,
                        this.normalBallList.get(i)));
            }
            if (ballDoesNotCollide) {
                this.normalBallList.add(newBall);
            }
        }
        this.playerBallList = new ArrayList<Ball>();
        this.playerBallList.add(this.playerBall);
        this.playerBallDistance = new HashMap<Ball, float[]>();

        this.animator = new Thread(this);
        this.animator.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        /* Draws all balls, player and non-player */
        this.playerBall.draw(g);
        for (int i = 0; i < this.normalBallList.size(); i++) {
            this.normalBallList.get(i).draw(g);
        }
    }

    @Override
    public void run() {
        while (this.running) {

            /* Move each non-player ball and check for wall collisions */
            for (int i = 0; i < this.normalBallList.size(); i++) {
                this.normalBallList.get(i).move();
                //this.checkAndDoWallCollision((this.normalBallList.get(i)));
            }

            /* Move player ball */
            this.movePlayerBall();

            /* Check for collisions between player ball and normal balls */
            for (int i = 0; i < this.normalBallList.size(); i++) {
                if (this.checkCollision(this.playerBall,
                        this.normalBallList.get(i))) {
                    this.makeCollision(this.playerBall,
                            this.normalBallList.get(i));
                }
            }

            /* Checking for collisions between two normal balls */
            for (int i = 0; i < this.normalBallList.size(); i++) {
                for (int j = i + 1; j < this.normalBallList.size(); j++) {
                    if (this.checkCollision(this.normalBallList.get(i),
                            this.normalBallList.get(j))) {
                        this.makeCollision(this.normalBallList.get(i),
                                this.normalBallList.get(j));
                    }
                }
            }

            this.repaint();

            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
            }
        }
    }

    private void movePlayerBall() {

        /* First check if in wall */
        //this.checkAndDoWallCollision(this.playerBall);

        /*
         * Getting absolute value of the difference between the cursor and the
         * player's ball, in x and y
         */
        float xDif = Math.abs(this.lastPoint.x - this.playerBall.getX());
        float yDif = Math.abs(this.lastPoint.y - this.playerBall.getY());

        //I was so surprised this worked: it's been years since I've used trigonometry
        /*
         * PLAYERSPEED * cos(atan(xDif / YDif) gives optimal speed in x
         * direction
         */
        float newPlayerXSpeed = (float) (this.PLAYERBALLSPEED
                * Math.cos(Math.atan(yDif / xDif)));
        /*
         * PLAYERSPEED * sin(atan(xDif / YDif) gives optimal speed in y
         * direction
         */
        float newPlayerYSpeed = (float) (this.PLAYERBALLSPEED
                * Math.sin(Math.atan(yDif / xDif)));

        if (this.lastPoint.x > this.playerBall.getX()) {
            this.playerBall.setxSpeed(newPlayerXSpeed);
        } else if (this.lastPoint.x < this.playerBall.getX()) {
            this.playerBall.setxSpeed(-newPlayerXSpeed);
        }

        if (this.lastPoint.y > this.playerBall.getY()) {
            this.playerBall.setySpeed(newPlayerYSpeed);
        } else if (this.lastPoint.y < this.playerBall.getY()) {
            this.playerBall.setySpeed(-newPlayerYSpeed);
        }

        if (Math.abs(
                this.playerBall.getX() - this.lastPoint.x) < ALLOWEDOFFSET) {
            this.playerBall.setxSpeed(0);
        }

        if (Math.abs(
                this.playerBall.getY() - this.lastPoint.y) < ALLOWEDOFFSET) {
            this.playerBall.setySpeed(0);
        }

        this.playerBall.move();
        for (int i = 1; i < this.playerBallList.size(); i++) {
            Ball b = this.playerBallList.get(i);
            b.setX(this.playerBall.getX() + this.playerBallDistance.get(b)[0]);
            b.setY(this.playerBall.getY() + this.playerBallDistance.get(b)[1]);
        }
    }

    /**
     * @param b1
     *            the first ball
     * @param b2
     *            the second ball
     * @return true if the distance between the centers of the balls is less
     *         than the sum of their radii, false otherwise
     */
    private boolean checkCollision(Ball b1, Ball b2) {
        double deltaX;
        double deltaY;
        double distance;

        deltaX = Math.abs(b1.getX() - b2.getX());
        deltaY = Math.abs(b1.getY() - b2.getY());
        distance = deltaX * deltaX + deltaY * deltaY;

        return (distance < ((b1.getRadius() + b2.getRadius())
                * (b1.getRadius() + b2.getRadius())));
    }

    /**
     * @param b1
     *            the first ball
     * @param b2
     *            the second ball Updates the speed(in both x and y direction)
     *            of b1 and b2 as if an elastic collision had occurred between
     *            them
     */
    private void makeCollision(Ball b1, Ball b2) {

        /* Player ball colliding with non-player ball */
        // I used a XOR operator here, and I am so proud of finding an actual use for XOR
        if ((this.playerBallList.contains(b1)
                ^ this.playerBallList.contains(b2))) {
            // If it is the first ball that is the player's and the second not
            if (this.playerBallList.contains(b1)) {
                // add the second ball to the player list
                this.playerBallList.add(b2);
                // and change its color
                b2.setColor(Color.gray);
                // and add its distance to the map
                float dist[] = { b2.getX() - this.playerBall.getX(),
                        b2.getY() - this.playerBall.getY() };
                this.playerBallDistance.put(b2, dist);

            } else /*
                    * If it is the second ball that is the player's and the
                    * first not
                    */ {
                // add the first ball to the player list
                this.playerBallList.add(b1);
                // and change its color
                b1.setColor(Color.gray);
                // and add its distance to the map
                float dist[] = { b1.getX() - this.playerBall.getX(),
                        b1.getY() - this.playerBall.getY() };
                this.playerBallDistance.put(b1, dist);
            }

        } else /* Normal elastic collision */ {
            /* v1f = [v1(m1 - m2) + 2(m2v2)] / (m1 + m2) */
            float newXVel1 = (b1.getxSpeed() * (b1.getMass() - b2.getMass())
                    + (2 * b2.getMass() * b2.getxSpeed()))
                    / (b1.getMass() + b2.getMass());
            float newYVel1 = (b1.getySpeed() * (b1.getMass() - b2.getMass())
                    + (2 * b2.getMass() * b2.getySpeed()))
                    / (b1.getMass() + b2.getMass());

            /* v2f = [v2(m2 - m1) + 2(m1v1)] / (m1 + m2) */
            float newXVel2 = (b2.getxSpeed() * (b2.getMass() - b1.getMass())
                    + (2 * b1.getMass() * b1.getxSpeed()))
                    / (b1.getMass() + b2.getMass());
            float newYVel2 = (b2.getySpeed() * (b2.getMass() - b1.getMass())
                    + (2 * b1.getMass() * b1.getySpeed()))
                    / (b1.getMass() + b2.getMass());

            b1.setxSpeed(newXVel1);
            b1.setySpeed(newYVel1);

            b2.setxSpeed(newXVel2);
            b2.setySpeed(newYVel2);

        }
    }

    /*
     * private boolean checkAndDoWallCollision(Ball b1) { boolean inWall =
     * false;
     *
     * if (b1.getX() - b1.getRadius() < 0) { b1.setX(b1.getRadius());
     * b1.setxSpeed(-b1.getxSpeed()); inWall = true; } else if (b1.getX() +
     * b1.getRadius() > CollectionOfBalls.WINDOWWIDTH) {
     * b1.setX(CollectionOfBalls.WINDOWWIDTH - b1.getRadius());
     * b1.setxSpeed(-b1.getxSpeed()); inWall = true; }
     *
     * if (b1.getY() - b1.getRadius() < 0) { b1.setY(b1.getRadius());
     * b1.setySpeed(-b1.getySpeed()); inWall = true; } else if (b1.getY() +
     * b1.getRadius() > CollectionOfBalls.WINDOWHEIGHT) {
     * b1.setY(CollectionOfBalls.WINDOWHEIGHT - b1.getRadius());
     * b1.setySpeed(-b1.getySpeed()); inWall = true; }
     *
     * return inWall; }
     */

}
