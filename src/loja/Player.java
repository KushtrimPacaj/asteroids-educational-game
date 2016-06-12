package loja;

import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.logging.*;

public class Player {

    private int x_pos;
    private final int y_pos;
    private final int boxSize_x;
    BufferedImage image;

    public Player(int x, int y, int size_x) {
        x_pos = x;
        y_pos = y;
        boxSize_x = size_x;

        try {
            image = ImageIO.read(this.getClass().getResourceAsStream("/images/anija.png"));
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void moveX(int speed) {
        x_pos += speed;
        if (x_pos +speed > boxSize_x - 85 || x_pos + speed < -15) {
            x_pos -= speed; // ne menyre qe mos me i kalu kufinjte
        }
       
    }

    public void setX(int x) {
         if (!(x > boxSize_x -45||x < 30)) {
            x_pos = x - 43; // pasi madhesia e anijes eshte rreth 90, atehere e vizatojme me majtas, ashtu qe mesi te bie ku eshte mausi
         }
    }

    public int getX() {
        return x_pos;
    }

    public Shot generateShot() {
        Shot shot = new Shot(x_pos + 43, y_pos - 15); // 43 edhe 15 jane qe me dale gjuajtja taman prej mesit te anijes
        return shot;
    }
    
    

    public void drawPlayer(Graphics g) {
        g.drawImage(image, x_pos, y_pos + 10, null);
    }
}
