/*
 * Tetris
 * 
 * TCSS 305a017
 */
package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import model.Board;
import sound.MusicPlayer;
import sound.SoundPlayer;

/**
 * A class that extends JPanel and displays the game board.
 * 
 * @author Daniel Stocksett / stockd
 * @version 12-2-17
 *
 */
public class GamePane extends JPanel {
    /**
     * Initial timer delay.
     */
    private static final int DELAY = 1000;
    /**
     * Serial id.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Initial game board dimensions.
     */
    private static final Dimension S_SIZE = new Dimension(240, 420);
    /**
     * Initial size multiplier.
     */
    private static final int SMALL = 20;
    /**
     * Larger size multiplier.
     */
    private static final int LARGE = 80;
    /**
     * Medium size multiplier.
     */
    private static final int MEDIUM = 30;
    /**
     * Variable to hold current game size.
     */
    private int mySizer;
    /**
     * Variable to hold current game over x centering.
     */
    private int myGameOver;
    /**
     * Board passed from TetrisGui.
     */
    private Board myBoard;
    /**
     * String representation of the game board.
     */
    private String myPieces;
    /**
     * Timer object.
     * 
     * will need to change later, do not make final.
     */
    private Timer myTimer;
    /**
     * Game over indicator.
     */
    private Boolean myBool;
    /**
     * Key listener for the JPanel.
     */
    private KeyListener myKey;
    /**
     * Boolean to keep pieces from free spinning 
     * when up key is held down.
     */
    private boolean mySpin;
    /**
     * Synchronizes pause feature.
     */
    private boolean myPause;
    /**
     * Listener for timer.
     */
    private ActionListener myListener; 
    /**
     * Used to reset the timer for line clear effects.
     */
    private int myLines;
    /**
     * Stores the lines for clear effect.
     */
    private Integer[] myEfect;
    /**
     * Stores timer values for effects.
     */
    private int myTimeInt;
    /**
     * Image being painted.
     */
    private BufferedImage myIcon;
    /**
     * Music player.
     */
    private MusicPlayer myPlayer;
    /**
     * Sound player.
     */
    private SoundPlayer mySounds;
    /**
     * Mode select.
     */
    private Boolean myMode;
    /**
     * Game is cheating if true.
     */
    private int myCheat;
    /**
     * Makes music repeat.
     */
    private int myCount;
    /**
     * Kills the music but not the sound.
     */
    private Boolean myKillTheMusic;

    /**
     * Constructor calls to initialize GUI.
     */
    public GamePane() {
        super();

        initalize();
    }
    /**
     * Called by constructor to start GUI.
     */
    private void initalize() {
        myPieces = "";
        myListener = new GameLIstener().timeListener();
        myTimer = new Timer(DELAY, myListener);
        mySounds = new SoundPlayer();
        myLines = 0;
        myTimeInt = DELAY;
        myMode = false;
        myCheat = 0;
        myCount = 0;
        myBool = false;
        mySizer = SMALL;
        myGameOver = MEDIUM;
        myKillTheMusic = true;

        try {
            myIcon = ImageIO.read(new File("./src/resources/mfks.jpg"));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        setLayout(null);
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.WHITE);
        setPreferredSize(S_SIZE);
        setVisible(true);
    }
    /**
     * Sizes the panel and game over location.
     * 
     * @param theSize Accepts an int as a multiplier. 
     */
    public void setSizes(final int theSize) {

        setPreferredSize(new Dimension((myBoard.getWidth() * theSize) + (2 * theSize),
                                       (myBoard.getHeight() * theSize) + theSize));        
        mySizer = theSize;
        myGameOver = ((myBoard.getWidth() / 2 - (LARGE / mySizer)) * mySizer) + (SMALL / 2);
    }
    /**
     * Sets Board object to variable.
     * 
     * @param theBoard Accepts a Board argument.
     */
    public void setBoard(final Board theBoard) {
        myBoard = theBoard;

    }
    /**
     * Setter for the game timer adds 200 ms for normal mode.
     * 
     * @param theTime t
     */
    public void setTimer(final int theTime) {
        final int two = 200;
        if (!myMode) {
            myTimeInt = theTime + two;
        } else {
            myTimeInt = theTime;
        }
    }
    /**
     * Called to start the game.
     */
    public void start() {
        myBool = false;
        myTimer.start();
        myPause = true;

        if (myKey == null) {
            myKey = new GameLIstener();
            addKeyListener(myKey);
        }
    }
    /**
     * Setter used by menu listener to pause the game.
     */
    public void setPause() {
        if (myTimer.isRunning() && myPause) {
            myTimer.stop();
            myPlayer.togglePause();

            repaint();
            myPause = false;
        }
    }
    /**
     * Sets the game to cheat.
     * 
     * @param theCheat Accepts an int that 
     * the number of clock cycles to cheat for.
     * 
     */
    public void setCheat(final int theCheat) {
        myCheat = theCheat;
    }
    /**
     * Game over flag.
     * 
     * @param theBool Accepts a boolean argument.
     */
    public void setMode(final Boolean theBool) {
        myMode = theBool;
    }
    /**
     * Setter for background.
     * 
     * @param theIcon Accepts a boolean argument.
     */
    public void setPicture(final String theIcon) {
        try {
            myIcon = ImageIO.read(new File(theIcon));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Starts music if possible.
     */
    public void startSong() {

        if (myKey != null) {
            myCount = 0;
        }
    }


    /**
     * Called by the update method from observer in main to set the game board string.
     * Changes board to sub string to remove unwanted area.
     * 
     * @param theArg Accepts a String argument.
     */
    public void setUpdate(final String theArg) {
        myPieces = theArg.substring(theArg.indexOf("----------") - 1);

        repaint();
    }
    /**
     * Sets line clear effects.
     * 
     * @param theArg Accepts an array of integer.
     */
    public void clearLine(final Integer[] theArg) {
        myEfect = theArg;
        final int five = 5;
        final int time = 100;
        myTimer.stop();
        myLines = five;
        setSounds();

        myTimer = new Timer(time, myListener);
        myTimer.start(); 
    }
    /**
     * manages line clear sounds.
     */
    private void setSounds() {

        if (!myMode) {
            mySounds.play("./src/resources/reg.wav");
        } else {
            mySounds.play("./src/resources/cyb.wav");    
        }
    }
    /**
     * Sets boolean for game over.
     * 
     * @param theArg Accepts a boolean argument.
     */
    public void setBool(final Object theArg) {
        myBool = (Boolean) theArg;        
    }
    /**
     * music player setter.
     * 
     * @param thePlayer music player class object.
     */
    public void setMusic(final MusicPlayer thePlayer) {
        myPlayer = thePlayer;        
    }
    /**
     * Changes block color based on play mode.
     * 
     * @param theC Char
     * @param theX x coordinate
     * @param theY v coordinate
     * @param theG2d graphics
     */
    private void blockMode(final char[] theC, final int theX,
                           final int theY, final Graphics2D theG2d) {

        if (myCheat == 0 && myMode) {
            if (theC[theX] != ' ' && theC[theX] != '|' && theC[theX] != '-') {
                pieceMaker(theG2d, Color.CYAN, theX, theY);
            }
        } else if (!myMode) {
            paintBlocks(theC, theX, theY, theG2d);             
        }
    }
    /**
     * Sets background based on play mode or if the game is cheating.
     * 
     * @param theG2d graphics.
     */
    private void setTheBackGround(final Graphics2D theG2d) {

        if (!myMode) {
            theG2d.drawImage(myIcon, 0, 0, (mySizer * myBoard.getWidth()) + (2 * mySizer),
                             (mySizer * myBoard.getHeight()) + (2 * mySizer), this);

        } else if (myCheat != 0) {
            imagehelper("./src/resources/i4.jpg"); 
            final int start = 4;
            theG2d.drawImage(myIcon, -getWidth() / 2, 0, getWidth() * start,
                             (mySizer * myBoard.getHeight()) + (2 * mySizer), this);

            if (myCheat == 2) {
                mySounds.play("./src/resources/eai.wav");
            }
        } else {
            imagehelper("./src/resources/i5.jpg");
            final int start = 4;
            theG2d.drawImage(myIcon, -getWidth() / start, 0, getWidth() * 2,
                             (mySizer * myBoard.getHeight()) + (2 * mySizer), this);
        }
    }
    /**
     * helper for cheat background.
     * 
     * @param theString String for image file
     */
    private void imagehelper(final String theString) {
        try {
            myIcon = ImageIO.read(new File(theString));
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * Pause display method.
     * 
     * @param theG2d graphics
     */
    private void pauseGraphic(final Graphics2D theG2d) {
        theG2d.setPaint(Color.WHITE);
        theG2d.setFont(new Font(Font.SERIF, Font.BOLD, MEDIUM));
        theG2d.drawString("Paused", myGameOver + (2 * mySizer),
                          (myBoard.getHeight() / 2) * mySizer);        
    }
    /**
     * Special effects for line clear.
     * 
     * @param theG2d graphics
     */
    private void lineEffect(final Graphics2D theG2d) {
        if (!myMode) {
            lineMode(theG2d, Color.RED, Color.YELLOW);
        } else if (myCheat == 0) {
            lineMode(theG2d, Color.CYAN, Color.BLUE);
        }
    }
    /**
     * helper for line clear animations.
     * 
     * @param theG2d graphics
     * @param theTnd Color
     * @param theL1st Color
     */
    private void lineMode(final Graphics2D theG2d, 
                          final Color theTnd, final Color theL1st) {
        // TODO Auto-generated method stub
        if (myLines % 2 == 0) {
            paintLines(theG2d, theL1st);
        } else {
            paintLines(theG2d, theTnd);
        }
    }
    /**
     * Helper method for line clear animation.
     * 
     * @param theG2d graphics
     * @param thePaint Color.
     */
    private void paintLines(final Graphics2D theG2d, final Color thePaint) {
        for (int i : myEfect) {
            theG2d.setPaint(thePaint);
            theG2d.fillRect(mySizer, (myBoard.getHeight() * mySizer) - (mySizer * i),
                            mySizer * myBoard.getWidth(), mySizer);
        }
    }
    /**
     * Displays game over when called by paint component, also closes scanner
     * and stops timer.
     * 
     * @param theG2d Accepts a Graphics argument.
     * @param theScanner Accepts a scanner argument.
     */
    private void gameOver(final Graphics2D theG2d, final Scanner theScanner) {

        theG2d.setPaint(Color.WHITE);
        theG2d.setFont(new Font(Font.SERIF, Font.BOLD, MEDIUM));
        theG2d.drawString("GAME OVER", myGameOver,
                          (myBoard.getHeight() / 2) * mySizer);
        myTimer.stop();
        theScanner.close();
        myPlayer.stopPlay();

    }
    /**
     * Called by paintComponent, sorts blocks for painting.
     * 
     * @param theChar Accepts a char array argument.
     * @param theX x Accepts an integer argument.
     * @param theY y Accepts an integer argument.
     * @param theG2d Accepts a Graphics argument.
     */
    private void paintBlocks(final char[] theChar, final int theX,
                             final int theY, final Graphics2D theG2d) {
        switch (theChar[theX]) {
            case 'I':
                pieceMaker(theG2d, Color.CYAN, theX, theY);
                break;
            case 'J':
                pieceMaker(theG2d, Color.BLUE, theX, theY);
                break;
            case 'L':
                pieceMaker(theG2d, Color.ORANGE.darker(), theX, theY);
                break;
            case 'O':
                pieceMaker(theG2d, Color.YELLOW.brighter(), theX, theY);
                break;
            case 'S':
                pieceMaker(theG2d, Color.GREEN, theX, theY);
                break;
            case 'T':
                pieceMaker(theG2d, Color.MAGENTA, theX, theY);
                break;
            case 'Z':
                pieceMaker(theG2d, Color.RED, theX, theY);
                break;

            default:
        }
    }
    /**
     * Helper method for paintBlocks, makes the blocks.
     * 
     * @param theG2d Accepts a Graphics argument.
     * @param theColor Accepts a color argument.
     * @param theX Accepts an integer argument.
     * @param theY Accepts an integer argument.
     */
    private void pieceMaker(final Graphics2D theG2d,
                            final Color theColor, final int theX, final int theY) {
        final int ten = 10;

        theG2d.setPaint(theColor);
        theG2d.fillRect(theX * mySizer, theY * mySizer, mySizer, mySizer);
        theG2d.setColor(Color.DARK_GRAY);
        theG2d.setStroke(new BasicStroke(mySizer / ten));
        theG2d.draw(new Rectangle2D.Double(theX * mySizer, theY * mySizer, mySizer, mySizer));
    }
    /**
     * Override of paint component, calls various helper methods to make the board.
     * 
     * @param theGraphics Accepts a Graphics argument.
     */
    @Override
    public void paintComponent(final Graphics theGraphics) {
        super.paintComponent(theGraphics);

        final Graphics2D g2d = (Graphics2D) theGraphics;
        final Scanner scn = new Scanner(myPieces);
        final int div = 4;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        setTheBackGround(g2d);
        g2d.drawImage(myIcon, 0, 0, (mySizer * myBoard.getWidth()) + (2 * mySizer),
                      (mySizer * myBoard.getHeight()) + (2 * mySizer), this);
        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(mySizer / div));
        g2d.drawRect(mySizer - 1, mySizer - 1, myBoard.getWidth() * mySizer + 2,
                     myBoard.getHeight() * mySizer + 2);

        for (int y = 0; scn.hasNext(); y++) {
            final String s = scn.nextLine();

            for (int x = 0; x < s.length(); x++) {
                final char[] c = s.toCharArray();

                blockMode(c, x, y, g2d);             
            }
        }
        if (!myPause && !myBool) {
            pauseGraphic(g2d);
        }
        if (myBool) {
            gameOver(g2d, scn);           
        }
        if (myLines > 0) {
            lineEffect(g2d);
        }
    }
    /**
     * Inner class for listeners extends keyAdapter.
     */
    public class GameLIstener extends KeyAdapter {
        /**
         * Tells the timer to reset.
         */
        private boolean myResetTimer;
      
        /**
         * Constructor.
         */
        public GameLIstener() {
            super();
            myResetTimer = false;
            mySpin = true;
            myPause = true;


        }
        /**
         * Action listener for myTimer.
         * 
         * @return Returns an ActionListener.
         */
        public ActionListener timeListener() {
            return new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent theEvent) {
                    helpTimer();                
                    if (myCheat > 0) {
                        myCheat--;
                    }
                    if (myLines > 0) {
                        myLines--;
                        repaint();
                        myResetTimer = true;
                    } else {
                        if (myResetTimer) {
                            timerFix(myTimeInt, myListener);
                            myResetTimer = false;
                        }
                        myBoard.down();
                    }
                }
            };
        }
        /**
         * Helper method for the timer listener for music repeat.
         */
        protected void helpTimer() {
            if (myKillTheMusic) {
                soundSink();
            } else if (!myKillTheMusic) {
                myPlayer.stopPlay();
            }            
        }
        /**
         * Plays music and keeps it repeating.
         */
        private void soundSink() {
            final int play = 79000;
            final int playO = 119000;


            if (!myMode && (myCount >= play || myCount == 0) && myTimer.isRunning()) {

                musicHelper("./src/resources/tetris-gameboy-02.mp3");

            } else if (myMode && (myCount >= playO || myCount == 0) && myTimer.isRunning()) {

                musicHelper("./src/resources/Doctor P - Tetris (1).mp3");
            }
            if (myTimer.isRunning()) {
                myCount += myTimeInt;
            }
        }
        /**
         * Setter for music counter.
         * @param theCount Int for music repeat.
         */
        public void setCount(final int theCount) {
            myCount = theCount;

        }
        /**
         * Helper method for music.
         * 
         * @param theSong String for music file
         */
        private void musicHelper(final String theSong) {
            final File[] file = {new File(theSong)};

            myPlayer.stopPlay();
            myPlayer = new MusicPlayer();
            myPlayer.newList(file);

            myCount = 0;

        }
        /**
         * Fixes timer after line clear effect.
         * 
         * @param theTimeInt time to set timer to as an int.
         * @param theListener Timers listener.
         */
        protected void timerFix(final int theTimeInt, final ActionListener theListener) {
            myTimer.stop();
            myTimer = new Timer(theTimeInt, theListener);
            myTimer.start();            
        }
        /**
         * Overrides KeyAdapters keyPressed method, handles keyboard controls for the panel.
         */
        @Override
        public void keyPressed(final KeyEvent theEvent) {
            final int key = theEvent.getKeyCode();

            if (key == KeyEvent.VK_P) {

                pause();
            } else if (myPause) {
                control(theEvent);
            } 
            if (key == KeyEvent.VK_K && myTimer.isRunning()) {
                if (myKillTheMusic) {
                    myKillTheMusic = false;
                } else if (!myKillTheMusic) {
                    myKillTheMusic = true;
                    myCount = 0;
                }
            }
        }
        /**
         * Pause Game and music.
         */
        public void pause() {

            if (myPause && myTimer.isRunning()) {
                myTimer.stop();
                myPlayer.togglePause();
                repaint();
                myPause = false;
            } else {
                myTimer.start();
                myPause = true;
                repaint();
                myPlayer.togglePause();
            }
        }
        /**
         * Helper method called by keyPressed to sort key inputs
         * and call the myBoard with actions.
         * 
         * @param theEvent Accepts a KeyEvent argument.
         */
        private void control(final KeyEvent theEvent) {

            final int key = theEvent.getKeyCode();

            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                myBoard.left();
            } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                myBoard.right();
            } else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                stopSpin();
            } else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                myBoard.down();
            } else if (key == KeyEvent.VK_SPACE) {
                stopDrop();
            }
        }
        /**
         * Helper method called by control to keep pieces from 
         * spinning wildly when rotate is called by keyPressed.
         */
        private void stopSpin() {
            if (mySpin) {
                myBoard.rotate();
                mySpin = false;
            }
        }
        /**
         * Helper method called by control to keep pieces from falling while button is held.
         */ 
        private void stopDrop() {
            if (mySpin) {
                myBoard.drop();
                mySpin = false;
            }
        }

        /**
         * Overrides keyReleased sets the boolean that stops pieces from spinning.
         * 
         * @param theEvent Accepts a KeyEvent argument.
         */
        public void keyReleased(final KeyEvent theEvent) {
            if (!mySpin) {
                mySpin = true;
            }
        }
    }
}