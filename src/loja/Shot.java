package loja;

import java.awt.Color;
import java.awt.Graphics;

public class Shot {

    private final int x_pos;
    private int y_pos;
    private final int radius = 4;

    public Shot(int x, int y) {
        x_pos = x;
        y_pos = y;
    }

    public int getYPos() {
        return y_pos;
    }

    public int getXPos() {
        return x_pos;
    }

    public void moveShot(int speed) {
        y_pos += speed;
    }

    public void drawShot(Graphics g) {
        g.setColor(Color.green);
        g.fillOval(x_pos, y_pos, radius, radius + 2);
    }
}
