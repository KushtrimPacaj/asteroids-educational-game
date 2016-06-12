package loja;

import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.logging.*;

public class Asteroid {

    private int x_pos;          // pozicionet momental
    private int y_pos;
    private final int x_pos_default;  //pozicionet normale ( nuk lëviz më shumë se pë delta nga kët pozicione
    private final int y_pos_default;
    public boolean hit;         // a eshte goditur

    BufferedImage image;        //"fytyra" e astroidit , dmth imazhi me numer ne te
    private int numri;          //numri ne asteroid

    private int delta;         // distancat nga pozicioni default
    private int deltaY;
    private final int maxDelta; // distanca maksimale  
    private int njesia;         // për sa "njësi" lëviz topi ( zakonisht +1 apo -1 )
    private int njesiaY;

    public Asteroid(int x, int y, int fytyra, int max_delta) {

        x_pos_default = x;
        y_pos_default = y;
        hit = false;
        numri = fytyra;
        njesia = 1;
        njesiaY = 1;
        maxDelta = max_delta;
        delta = (int) (Math.random() * 2 * max_delta - max_delta);  // fillon ne nje distance te rastesishme nga pozita default
        deltaY = (int) (Math.random() * 2 * max_delta - max_delta);

        x_pos = x_pos_default + delta;
        y_pos = y_pos_default + deltaY;
        
        try {
            image = ImageIO.read(this.getClass().getResourceAsStream("/images/" + numri + ".png"));
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int getYPos() {
        return y_pos;
    }

    public int getNumber() {
        return numri;
    }
    int i = 0;
    int pritja = 1;   // me keto definojme shpejtesine e levizjes, psh ne kete rast pritja=1, dmth nje cikel leviz, nje jo
   
    /**
     * Përdoret për ta lëvizur asteroidin
     */
    public void move() {
        // System.out.println("delta "+delta +" asteroidi " + numri);
        if (Math.abs(delta) != maxDelta) {
            if (i == pritja) {
                // nëse distanca nga pozita default nuk eshte sa distanca maksimale, e levizim topin 
                i = 0;
                x_pos += njesia;    // levizim ne poziten x
                delta = x_pos - x_pos_default;
                //tash  njejte edhe ne y
                if (Math.abs(deltaY) != maxDelta) {
                    y_pos += njesiaY;
                    deltaY = y_pos - y_pos_default;
                } else if (deltaY == maxDelta) {
                    njesiaY = -1;
                    y_pos += njesiaY;
                    deltaY = y_pos - y_pos_default;
                } else if (deltaY == (-1) * maxDelta) {
                    njesiaY = 1;
                    y_pos += njesiaY;
                    deltaY = y_pos - y_pos_default;
                }

            } else {
                i++;
            }
        } else  {
            // nese ka mberritur skajin, nderroja drejtimin, dhe levize
            njesia = (-1)*njesia;
            x_pos += njesia;
            delta = x_pos - x_pos_default;

        }

    }



    public void setNumber(int i) {
        // e kemi numrin e ri, pra tash e resetojme hit-in, si dhe e marrim nje imazh te ri
        hit = false;
        numri = i;
        try {
            image = ImageIO.read(this.getClass().getResourceAsStream("/images/" + numri + ".png"));

        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getRadius() {
        return image.getHeight();
    }

    public void setHit(boolean b) {
        hit = b;
    }

    public boolean getHit() {
        return hit;
    }

    public int getXPos() {
        return x_pos;
    }

    public void drawAsteroid(Graphics g) {
        if (!hit) {
            g.drawImage(image, x_pos, y_pos, null);
        }

    }
}
