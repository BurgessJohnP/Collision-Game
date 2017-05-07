import javax.swing.JFrame;

public class CollectionOfBalls {
    public static final int WINDOWHEIGHT = 1000;
    public static final int WINDOWWIDTH = 2000;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Gui balls = new Gui();
        frame.setSize(WINDOWWIDTH, WINDOWHEIGHT);
        frame.add(balls);
        frame.setVisible(true);

    }
}
