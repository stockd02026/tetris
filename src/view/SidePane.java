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
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * A JPanel class to hold other JPanel inner class objects.
 * 
 * @author Daniel Stocksett / stockd
 * @version 12-2-17
 *
 */
public class SidePane extends JPanel {

    /**
     * Serial id.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Default background for preview panel.
     */
    private static final String FILE = "./src/resources/mfks.jpg";
    /**
     * Size used to initialize the sub panels.
     */
    private static final Dimension ALL_SIZE = new Dimension(200, 130);
    /**
     * Size used to initialize this panel.
     */
    private static final Dimension SIZE = new Dimension(200, 400);
    /**
     * String constant used in ScorePane.
     */
    private static final String SCORE = "Score: ";
    /**
     * String constant used in ScorePane.
     */
    private static final String LEVEL = "Level:  ";
    /**
     * Text for score panel.
     */
    private static final String LINES = "Lines Cleared: ";
    /**
     * Text for score panel.
     */
    private static final String LINES_TO_LVL = "Lines to next level: ";
    /**
     * Text for score panel.
     */
    private static final int LVL_LINES = 5;
    /**
     * Stores a ScorePane JPanel object.
     */
    private final ScorePane myScore;
    /**
     * Stores a PreviewPane JPanel object.  
     */
    private final PreviewPane myView;
    /**
     * Stores a Instructions JPanel object.
     */
    private final Instructions myRules;
    /**
     * Stores the preview piece as a string.
     */
    private String myPiece;
    /**
     * Variable to store the current score.
     */
    private int myCount;
    /**
     * Number of cleared lines.
     */
    private int myLines;
    /**
     * Current level.
     */
    private int myLevel;
    /**
     * Lines remaining in the level.
     */
    private int myLinesToLevel;
    /**
     * Stores the background for preview panel.
     */
    private BufferedImage myIcon;
    /**
     * Helps change game mode.
     */
    private Boolean myCyber;
    /**
     * Helps change game mode.
     */
    private Boolean myAI;
    /**
     * Helps change game mode.
     */
    private Color myNdColor;
    /**
     * Helps change game mode.
     */
    private Color myColor;

    /**
     * Constructor calls initialize method to start GUI. 
     */
    public SidePane() {
        super();

        myScore = new ScorePane();
        myView = new PreviewPane();
        myRules = new Instructions();
     
        initalize();     
    }
    /**
     * Called by constructor to start GUI.
     */
    private void initalize() {
        final int height = 3;
     
        myPiece = "";
        myCount = 0;
        myLines = 0;
        myLevel = 1;
        myLinesToLevel = LVL_LINES;
        myCyber = false;
        myAI = false;
        myColor = Color.BLACK;
        myNdColor = Color.LIGHT_GRAY;

        getPic(FILE);

        setLayout(new GridLayout(height, 0));
        setBackground(Color.WHITE);
        setPreferredSize(SIZE);
        add(myScore);
        add(myView);
        add(myRules);
        setVisible(true);
    }
    /**
     * 
     * @param theCyber c
     * @param theAI a
     */
    public void setMode(final Boolean theCyber, final Boolean theAI) {

        myCyber = theCyber;
        myAI = theAI;
        setIcon();
        repaint();
    }
    /**
     * sets background for the preview.
     */
    private void setIcon() {
        if (myAI) {
            getPic("./src/resources/i3.jpg");
        } else if (myCyber) {
            getPic("./src/resources/i1.jpg");
            myColor = Color.BLUE;
            myNdColor = Color.CYAN;
        } else {
            getPic(FILE);
            myColor = Color.BLACK;
            myNdColor = Color.LIGHT_GRAY;
        }
    }
    /**
     * Helper method for set icon.
     * 
     * @param theString Accepts a string argument.
     */
    private void getPic(final String theString) {
        try {
            myIcon = ImageIO.read(new File(theString));
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }
    /**
     * Called by observer update in TetrisGui to refresh all JPanels in this class.
     * 
     * @param theArg Accepts a String argument.
     */
    public void setUpdate(final String theArg) {

        myPiece = theArg;
        myView.repaint();
    }
    /**
     * Sets the line counts for score pane.
     * 
     * @param theInt Accepts an integer argument.
     * @param theScore Accepts an integer argument.
     */
    public void setLines(final int theInt, final int theScore) {
        myLines = theInt;
        myLinesToLevel = LVL_LINES - (theInt % LVL_LINES);
        myLevel = (int) (theInt / LVL_LINES) + 1;
        myCount = theScore;
        myScore.repaint();
    }

    /**
     * Inner class also extends a JPanel to display score and level.
     */
    public final class ScorePane extends JPanel {
        /**
         * Serial id.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Constructor.
         */
        public ScorePane() {
            super();

            setSize(ALL_SIZE);
            setBackground(Color.BLACK);
            setVisible(true);
        }       
        /**
         * Override of paint component to display score and level.
         */
        @Override
        public void paintComponent(final Graphics theGraphics) {
            super.paintComponent(theGraphics);

            final int font = 18;
            final int indent = 5;
            final int scroll = 30;
            final String[] print = {SCORE, LEVEL, LINES_TO_LVL, LINES};
            final int[] number = {myCount, myLevel, myLinesToLevel, myLines};
            final Graphics2D g2d = (Graphics2D) theGraphics;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setPaint(myNdColor);
            g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, font));
            for (int i = 0; i < print.length; i++) {
                g2d.drawString(print[i] + number[i], indent, scroll * (i + 1));

            }

        }
    }

    /**
     *  Inner class to display the next piece.
     */
    public final class PreviewPane extends JPanel {

        /**
         * Serial id.
         */
        private static final long serialVersionUID = -1836108119350149577L;

        /**
         * Constructor.
         */
        public PreviewPane() {
            super();

            setSize(ALL_SIZE);
            setBackground(Color.BLACK);
            setVisible(true);
        }

        /**
         * Override of paintComponent to draw the preview pane.
         * Calls helper method. 
         */
        @Override
        public void paintComponent(final Graphics theGraphics) {
            super.paintComponent(theGraphics);

            final int x1 = 20;
            final int y1 = 10;
            final int w1 = 160;
            final int h1 = 115;
            final Graphics2D g2d = (Graphics2D) theGraphics;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);


            g2d.drawImage(myIcon, x1, y1, w1, h1, this);

            if (!myAI) {
                final Scanner scn = new Scanner(myPiece);

                for (int y = 0; scn.hasNext(); y++) {

                    final String s = scn.nextLine();

                    for (int x = 0; x < s.length(); x++) {                    
                        final char[] c = s.toCharArray();

                        if (c[x] == 'O' || c[x] == 'I') {
                            final int out = 20;

                            panelHelper(g2d, out, x, y);

                        } else if (c[x] != ' ') {
                            final int out = 40;

                            panelHelper(g2d, out, x, y);
                        }
                    }

                }
                scn.close();
            }
        }
        /**
         * Helper method called by paint component.
         * 
         * @param theG2d Accepts a Graphics argument.
         * @param theTimes Accepts an integer argument.
         * @param theX Accepts an integer argument.
         * @param theY Accepts an integer argument.
         */
        private void panelHelper(final Graphics2D theG2d,
                                 final int theTimes, final int theX, final int theY) {
            final int dim = 40;

            theG2d.setPaint(myColor);
            theG2d.fillRect(theX * dim + theTimes, theY * dim, dim, dim);
            theG2d.setColor(myNdColor);
            theG2d.setStroke(new BasicStroke(2));
            theG2d.draw(new Rectangle2D.Double(theX * dim + theTimes, theY * dim, dim, dim));
        }
    }
    /**
     * Inner class extends Jpanel to display rules.
     *
     */
    public final class Instructions extends JPanel {
        /**
         * serial id.
         */
        private static final long serialVersionUID = 5942610079001529801L;
        /**
         * String used in rules.
         */
        private static final String STRING_0 = "Move Left =         left key, a, or A.";
        /**
         * String used in rules.
         */
        private static final String STRING_1 = "Move Right =    right key, d, or D.";
        /**
         * String used in rules.
         */
        private static final String STRING_2 =  "Move Down = down key, s, or S.";
        /**
         * String used in rules.
         */
        private static final String STRING_3 = "Rotate Peice =  up key, w, or W.";
        /**
         * String used in rules.
         */
        private static final String STRING_4 = "Drop Piece =               space bar.";
        /**
         * Pause instructions.
         */
        private static final String STRING_5 = "Pause / Unpause Game =        P.";
        /**
         * mute instructions.
         */
        private static final String STRING_6 = "Stop / Start Sound =                  K.";

        /**
         * Constructor.
         */
        public Instructions() {
            super();

            setSize(ALL_SIZE);
            setBackground(Color.BLACK);
            setVisible(true);
        }
        /**
         * Override of paintComponent to display rules.
         */
        @Override
        public void paintComponent(final Graphics theGraphics) {
            super.paintComponent(theGraphics);

            final int stroke = 12;
            final int spacing1 = 10;
            final int spacing2 = 20;
            final int spacing3 = 10;
            final Graphics2D g2d = (Graphics2D) theGraphics;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setPaint(myNdColor);
            g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, stroke));
            final String[] string = {STRING_0, STRING_1,
                STRING_2, STRING_3, STRING_4, STRING_5, STRING_6};
            for (int y = 0; y < string.length; y++) {

                g2d.drawString(string[y] , spacing1, y * spacing2 + spacing3);
            }
        }
    }
}
