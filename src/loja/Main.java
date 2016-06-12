package loja;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Random;
import java.util.logging.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Main extends JPanel implements Runnable, KeyListener, MouseMotionListener, MouseListener, ActionListener {
    // variables

    public int boxSize_x = 596;
    public int boxSize_y = 335;//335
    private Thread th;
    BufferedImage ikona2 = null;
    JMenuBar menuBar;
    JMenu options,about;
    JMenuItem autori,ndihme;
    JCheckBoxMenuItem sound;

    private final Player player;
    private final Shot[] shots;
    private final Asteroid[] asteroid = new Asteroid[5];
    private final int max_delta = 20;   // largesia maksimale e asteroideve nga pozita normale

    // konstantat e shpejtesise
    private final int shotSpeed = -10;
    private final int playerSpeed = 3;

    // leviz majtas-djathtas ?
    private boolean playerMoveLeft,playerMoveRight;

    JFrame f;
    BufferedImage prapavija = null;

    private int nr_mbledhesave ,shuma , shuma_momentale;

    private int points = 0;// sa pike i ka , secilën herë që e qëllon i fiton 10 poena, nëse humb , i zbriten 5

    private String mesazhi = "";
    private String mesazhi_momental = "";
    private String gjendja1 = "Level 1";
    private String timer = "";
    private int level = 1;
    long koha_old;

    public Main() {

        f = new JFrame();
        f.setResizable(false);

        //krijohet lojtari/anija ne poziten perkatese fillestare
        player = new Player(((int) (boxSize_x / 2)) - 45, boxSize_y - 100, boxSize_x); //player = new Player(150, 280, boxSize);

        //krijojme asteroidat ne pozitat perkatese
        gjeneroNumrat();
        shots = new Shot[8];
        koha_old = System.currentTimeMillis();

        f.setBackground(Color.black);
        f.setVisible(true);
        f.setTitle("Asteroidët");
        f.setLocation(-3, 0);

        // menytë
        menuBar = new JMenuBar();

        options = new JMenu("Opsionet");
        options.setMnemonic(KeyEvent.VK_O); // Alt + O hapet kjo meny
        options.addActionListener(this);

        sound = new JCheckBoxMenuItem("Zëri ON/OFF");
        sound.setState(false);
        sound.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));   // me klikimin e shkronjes S (de)aktivizohet
        options.add(sound);

        menuBar.add(options);

        about = new JMenu("Për...");
        about.setMnemonic(KeyEvent.VK_P);   // Alt + P hapet kjo meny
        autori = new JMenuItem("Autori");
        autori.addActionListener(this);

        ndihme = new JMenuItem("Ndihmë");
        ndihme.addActionListener(this);
        ndihme.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0)); // me F1 hapet ndihma
        about.add(autori);
        about.add(ndihme);
        menuBar.add(about);

        f.setJMenuBar(menuBar);
        //end menytë

        try {

            BufferedImage ikona = ImageIO.read(this.getClass().getResourceAsStream("/images/ikona.png"));
            f.setIconImage(ikona);      //e marrim ikonen edhe e vendosim ne frame
            ikona2 = ImageIO.read(this.getClass().getResourceAsStream("/images/ikona2.png"));  // ikona e vogel, te dritarja e ndihmes
            prapavija = ImageIO.read(this.getClass().getResourceAsStream("/images/prapavija.jpg")); //marrim prapavijen
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

        f.getContentPane().add(this);  // e fusim kete panel ne dritare
        f.setSize(boxSize_x, boxSize_y + 65 + 20);  // madhesia e dritares , per 65 per shkak te shiritit te zi ne fund, 20 per shkak te menyse 

        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);

        //  E fshehim mausin
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        f.getContentPane().setCursor(blankCursor);
        // end

        setFocusable(true);    // ky panel lejohet te kete fokus
        requestFocusInWindow(); // menjehere vendosja fokusin

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        th = new Thread(this);
        th.start();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == autori) {
            JOptionPane.showMessageDialog(this, "Autor: Kushtrim Pacaj");
        } else if (e.getSource() == ndihme) {
            JOptionPane.showMessageDialog(this, "Lëvizja: \nMe shigjeta në tastierë, apo me maus \n\nGjuajtja: \nMe SPACEBAR apo left-click në maus", "Ndihmë", JOptionPane.PLAIN_MESSAGE, new ImageIcon(ikona2));

        }
    }

    public void checkLevel() {
        if (points >= 1 && points <= 10) {
            level = 1;
            gjendja1 = "Level " + level;
        } else if (points > 10) {
            level = 2;
            gjendja1 = "Level " + level;
        }
    }

    private void gjeneroNumrat() {

        int[] asteroid_number = new int[5];  // 5 numrat ne fytyra te 5 asteroideve

        asteroid_number[0] = (int) (Math.random() * 15 + 1);  // te parit po ia japim nje vlere
        // tjeret po i gjenerojme ne menyre te rastesishme, por qe mos te perseritet ndonje qe eshte gjeneruar me heret        
        for (int i = 1; i != 5; i++) {
            int random = 0;
            boolean ok = false;
            while (!ok) { //perderisa nuk e e kemi gjeneruar nje numer qe nuk eshte gjeneruar me heret
                ok = true;  //supozojme se numri i gjeneruar eshte unik
                random = (int) (Math.random() * 15 + 1);
                for (int j = 0; j != i; j++) {
                    if (asteroid_number[j] == random) {
                        ok = false; //nese eshte gjeneruar me heret 
                    }
                }
            }
            asteroid_number[i] = random;
        }  // end for

        //nese nuk ka asteroida (kur startohet loja), po i krijojme si objekte, perndryshe veq iau ndryshojme numrat
        if (asteroid[0] == null) {
            int next = 20;
            for (int i = 0; i != 5; i++) {
                asteroid[i] = new Asteroid(next, 30, asteroid_number[i], max_delta);
                next += 120; // distanca ne mes tyre eshte 120
            }
        } else {
            for (int i = 0; i != 5; i++) {
                // iau ndryshojme imazhet
                asteroid[i].setNumber(asteroid_number[i]);
            }
        }
        // tani gjenerojme nr_mbledhesave;
        nr_mbledhesave = (int) (Math.random() * 2 + 2);

        // i zgjedhim ne menyre te rastesishme "nr_mbledhesave" numra nga 0-4 ( indexa te asteroideve), dmth zgjedhim se cilet asteroida do i mbledhim
        int[] mbledhesat = new int[nr_mbledhesave];
        mbledhesat[0] = (int) (Math.random() * 5);
        for (int i = 1; i != nr_mbledhesave; i++) {
            int random = 0;
            boolean ok = false;
            while (!ok) {
                ok = true;
                random = (int) (Math.random() * 5);
                for (int j = 0; j != i; j++) {
                    if (mbledhesat[j] == random) {
                        ok = false;
                    }
                }

            }
            mbledhesat[i] = random;
        }

        // tash i mbledhim mbledhesat, edhe llogarisim shumen
        for (int i = 0; i != nr_mbledhesave; i++) {
            shuma += asteroid_number[mbledhesat[i]];
            System.out.print(asteroid_number[mbledhesat[i]] + ", ");
        }
        System.out.println(" Shuma = " + shuma);

        //nganjehere ndodh qe psh shuma 9 , sepse mbledhesat jane 8 dhe 1  ,
        //por ndodh qe gjenerohet edhe vetë shuma psh kemi asteroid me nr 9, ne ate rast e therrasim prape kete metode
        boolean ok = true;
        for (int i = 0; i != 5; i++) {
            if (shuma == asteroid_number[i]) {
                ok = false;
            }
        }
        if (!ok) {
            shuma = 0;
            gjeneroNumrat();
        } else {
            mesazhi = "Goditni dy ose më shumë asteroidë, ashtu që shuma e tyra të jetë " + shuma;
            mesazhi_momental = "";
        }

    }

    //Fisher-Yates
    void shuffle2Arrays(int[] ar, boolean[] br) {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);

            int a = ar[index];
            boolean b = br[index];
            ar[index] = ar[i];
            br[index] = br[i];
            ar[i] = a;
            br[i] = b;
        }
    }

    public void shuffleAsteroids() {
        long koha_new = System.currentTimeMillis();
        if (koha_new - koha_old > 5000) {
            // marrim numrat dhe gjendjet e asteroideve
            int[] asteroid_numbers = new int[5];
            boolean[] asteroid_hits = new boolean[5];

            for (int i = 0; i != 5; i++) {
                asteroid_numbers[i] = asteroid[i].getNumber();
                asteroid_hits[i] = asteroid[i].getHit();
            }
            shuffle2Arrays(asteroid_numbers, asteroid_hits);

            //tash i vendosim numrat edhe hits ne asteroida
            for (int i = 0; i != 5; i++) {
                asteroid[i].setNumber(asteroid_numbers[i]);
                asteroid[i].setHit(asteroid_hits[i]);
            }

            koha_old = koha_new;
        } else {
            long diferenca = 5000 - (koha_new - koha_old);
            timer = diferenca / 1000 + "." + ((diferenca) % 1000) / 100 + "";
        }
    }

    public void run() {
//        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        while (true) {

            //levizim asteroidat
            for (int i = 0; i != 5; i++) {
                asteroid[i].move();
            }
            if (level == 2) {
                // shikojme timer-in , se a duhet t'i bejme shuffle
                shuffleAsteroids();
            }
            // shikojme secilin plumb
            for (int i = 0; i < shots.length; i++) {
                //tani shikojme per plumbin e i-të, nëse  e ka goditur ndonje asteroid
                // nuk eshte null, dmth ka gjuajtje/plumb ne indeksin e i-te te vargut
                if (shots[i] != null) {
                    //blabla:

                     shots[i].moveShot(shotSpeed); // i thojmë plumbit qe ta leviz vetveten
                     //per plumbin e i-të shiqojmë per secilin asteroid se a ka goditje , 5 =  nr i asteroidave
                    for (int j = 0; j != 5; j++) {

                        if (shots[i].getYPos() < asteroid[j].getYPos() + (int) (asteroid[j].getRadius() / 2 + 10) && shots[i].getYPos() > asteroid[j].getYPos()
                                && shots[i].getXPos() > asteroid[j].getXPos() && shots[i].getXPos() < asteroid[j].getXPos() + asteroid[j].getRadius() && !asteroid[j].getHit()) {
                            // nëse mberrijme deri ketu, plumbi i i-te ka goditur asteroidin e j-te
                            shots[i] = null; //zhduke plumbin, se e ka goditur
                            asteroid[j].setHit(true);  // i thome asteroidit se  eshte goditur, dmth ne vizatimin e rradhes zhduket ( nuk vizatohet ) 
                            shuma_momentale += asteroid[j].getNumber();  // e shtojme ne shume, numrin ne fytyre te asteroidit

                            //shiqojme se a ia ka qelluar numrave qe e japin shumen
                            if (shuma_momentale == shuma) {
                                //ka fituar, lajmero dhe reseto lojen
                                //gjendja1 = "Level 1";
                                points += 1;
                                checkLevel();
                                resetoLojen();
                                gjeneroNumrat(); //gjenero numrat per nivelin e ardhshem
                            } else if (shuma_momentale > shuma) {
                                //  e ka tejkalu shumene e duhur
                                // lajmero per humbje , dhe reseto lojen
                                gjendja1 = "Keni humbur";
                                points = 0;
                                level = 1;
                                timer = "";//reseto mbajtesin e sekondave
                                resetoLojen();
                                gjeneroNumrat();
                            } else {
                                mesazhi_momental = "Shuma momentale e asteroidëve të goditur është : \t" + shuma_momentale;
                            }
                            break;

                        } else if (shots[i].getYPos() < 0) {
                            // e ka kaluar pjesen e siperme te kornizzes
                            shots[i] = null;
                            break;
                        }
                    }

                }
            }

            // shiqo se a duhet levizur lojtaret
            if (playerMoveLeft) {
                player.moveX(playerSpeed * (-1));
            } else if (playerMoveRight) {
                player.moveX(playerSpeed);
            }

            // ri-vizato pamjen e lojes
            repaint();

            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {

            }

//            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        }

    }

    public void resetoLojen() {

        mesazhi = "";
        mesazhi_momental = "";
        shuma = 0;
        shuma_momentale = 0;
        koha_old = System.currentTimeMillis();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            playerMoveLeft = true;
        } else if (key == KeyEvent.VK_RIGHT) {
            playerMoveRight = true;
        } else if (key == KeyEvent.VK_SPACE) {
            // e ka prekur "FIRE" , e gjenerojme plumbin
            for (int i = 0; i < shots.length; i++) {
                if (shots[i] == null) { // e gjejme vendin e pare bosh ne varg, edhe e fusim "plumbin"
                    shots[i] = player.generateShot();
                    if (sound.getState()) {
                        playAudio();
                    }
                    break;
                }
            }
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            playerMoveLeft = false;
        } else if (key == KeyEvent.VK_RIGHT) {
            playerMoveRight = false;
        }

    }

    public void playAudio() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/sounds/laser.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
        }
    }

    @Override
    public void paint(Graphics g) {

        super.repaint();
        f.repaint();

        g.drawImage(prapavija, 0, 0, null);
        player.drawPlayer(g);
        for (Asteroid a : asteroid) {
            a.drawAsteroid(g); // per çdo asteroid , vizatoje
        }
        for (Shot shot : shots) {
            if (shot != null) {
                shot.drawShot(g);
            }
        }
        g.setColor(Color.white);
        g.drawString(mesazhi, 10, boxSize_y + 13);
        g.drawString(mesazhi_momental, 10, boxSize_y + 28);
        g.drawString(gjendja1, 490, boxSize_y + 13);
        g.drawString("Pikët : " + points, 490, boxSize_y + 28);
        g.drawString(timer, 550, boxSize_y + 28);

    }

    /**
     * Leviz anijen kozmike kur te leviz mausi!
     * @param event
     */
    @Override
    public void mouseMoved(MouseEvent event) {

        player.setX(event.getX());
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        player.setX(event.getX());
    }

    //------------------------------------------------------------------------
    //  FIREEEEEEEEEEEEEEE !
    //------------------------------------------------------------------------
    @Override
    public void mousePressed(MouseEvent event) {
        // System.out.println(event.getX());
        for (int i = 0; i < shots.length; i++) {
            if (shots[i] == null) { // e gjejme vendin e pare bosh ne varg, edhe e fusim "plumbin"
                shots[i] = player.generateShot();
                if (sound.getState()) {
                    playAudio();
                }
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
    }


    @Override
    public void mouseClicked(MouseEvent event) {
    }


    @Override
    public void mouseExited(MouseEvent event) {

    }

    @Override
    public void mouseEntered(MouseEvent event) {

    }

    public static void main(String[] args) {
        new Main();

    }

}
