import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Gui extends JPanel implements Runnable {

    // Keeps follow's mouse clicks
    private Point lastPoint = new Point(0, 0);

    private static final int NUMBEROFBALLS = 30;
    private static final float MAXRADIUS = 70;
    private static final float MAXSTARTSPEED = 2;

    // Stores all "normal"(non-player) balls
    ArrayList ballList;
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

                Gui.this.playerBall.setX(Gui.this.lastPoint.x);
                Gui.this.playerBall.setY(Gui.this.lastPoint.y);

                Gui.this.lastPoint = new Point(e.getX(), e.getY());
                g.dispose();
            }
        });

        /* Creating the ball controlled by the player */
        this.playerBall = new Ball(50, 80, 20);

        /* Initializing list of non-player balls, then populating it */
        this.ballList = new ArrayList<Ball>();
        while (this.ballList.size() < NUMBEROFBALLS) {
            Ball newBall = new Ball(
                    (float) Math.random() * CollectionOfBalls.WINDOWWIDTH,
                    (float) Math.random() * CollectionOfBalls.WINDOWHEIGHT,
                    (float) Math.random() * MAXRADIUS);
            newBall.setxSpeed(
                    (float) (((Math.random() - 1) * 2) * MAXSTARTSPEED));
            newBall.setySpeed(
                    (float) (((Math.random() - 1) * 2) * MAXSTARTSPEED));

            /*
             * Only create this new ball if it does not initially collide with
             * any of the already created balls
             */
            boolean ballDoesNotCollide = true;
            for (int i = 0; i < this.ballList.size()
                    && ballDoesNotCollide; i++) {
                ballDoesNotCollide = !(this.checkCollision(newBall,
                        (Ball) this.ballList.get(i)));
            }
            if (ballDoesNotCollide) {
                this.ballList.add(newBall);
            }
        }

        this.animator = new Thread(this);
        this.animator.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        /* Draws all balls, player and non-player */
        this.playerBall.draw(g);
        for (int i = 0; i < this.ballList.size(); i++) {
            ((Ball) this.ballList.get(i)).draw(g);
        }
    }

    @Override
    public void run() {
        while (this.running) {

            /* Move each non-player ball */
            for (int i = 0; i < this.ballList.size(); i++) {
                ((Ball) this.ballList.get(i)).move();
            }

            /* Move player ball */
            this.playerBall.setX(this.lastPoint.x);
            this.playerBall.setY(this.lastPoint.y);

            /* Check for collisions between player ball and normal balls */
            for (int i = 0; i < this.ballList.size(); i++) {
                if (this.checkCollision(this.playerBall,
                        (Ball) this.ballList.get(i))) {
                    this.makeCollision(this.playerBall,
                            (Ball) this.ballList.get(i));
                }

            }

            /* Checking for collisions between two normal balls */
            for (int i = 0; i < this.ballList.size(); i++) {
                for (int j = i + 1; j < this.ballList.size(); j++) {
                    if (this.checkCollision((Ball) this.ballList.get(i),
                            (Ball) this.ballList.get(j))) {
                        this.makeCollision((Ball) this.ballList.get(i),
                                (Ball) this.ballList.get(j));

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

    /**
     * @param b1
     *            the first ball
     * @param b2
     *            the second ball
     * @return true if the distance between the centers of the balls is less
     *         than the sum of their radii, false otherwise
     */
    public boolean checkCollision(Ball b1, Ball b2) {
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
    public void makeCollision(Ball b1, Ball b2) {
        if (b1 == this.playerBall) {
            b2.setxSpeed(-b2.getxSpeed());
            b2.setySpeed(-b2.getySpeed());
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

}
