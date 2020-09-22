/*
 * Tetris
 * 
 * TCSS 305a017
 * 
 * Blue Cyber and Evil A.I. mode art was old Haswell / Brodwell series Intel box art.
 * The icon in help was made by me in gimp.
 * The normal mode art was made by my wife in Photo shop.
 * The Cyber & evil A.I. music is "Tetris" by DR. P.
 * the normal mode music is the original tetris theme song.
 * the sound effects were down loaded from the kersplat open source sound library. 
 */
package view;

import com.sun.media.codec.audio.mp3.JavaDecoder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import javax.media.Codec;
import javax.media.PlugInManager;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import model.Board;
import model.MovableTetrisPiece;
import sound.MusicPlayer;



/**
 * A class to hold and provide functionality for the
 * display panels. 
 * 
 * @author Daniel Stocksett / stockd
 * @version 12-2-17
 *
 */
public class TetrisGui implements Observer {
    /**
     * Default board size.
     */
    public static final int DEFAULT_X = 10;
    /**
     * Default board size.
     */
    public static final int DEFAULT_Y = 20;
    /**
     * Default small dimensions.
     */
    private static final Dimension S_SIZE = new Dimension(450, 490);
    /**
     * Multiplier to build panel dimensions.
     */
    private static final int SMALL = 20;
    /**
     * Multiplier to build panel dimensions.
     */
    private static final int MEDIUM = 30;
    /**
     * Multiplier to build panel dimensions.
     */
    private static final int LARGE = 40;
    /**
     * Default timer value.
     */
    private static final int TIMER_ADD = 800;
    /**
     * Divisor used to set picture dimensions.
     */
    private static final int FOUR = 4;
    /**
     * helps coordinate pause function.
     */
    private MenuListener myPause;
    /**
     * A GamePane object for displaying the game.
     */
    private final JPanel myMainPanel;
    /**
     * A SidePane object to display everything but the game.
     */
    private final JPanel mySidePanel;
    /**
     * A JFrame to attach everything to.
     */
    private final JFrame myFrame;
    /**
     * A Board object for game mechanics. 
     */
    private Board myBoard;
    /**
     * Changes state of menu buttons enable / disable.
     */
    private boolean myStartButtonEnabled;
    /**
     * Start menu.
     */
    private JMenu myItemMenu;   
    /**
     * Keeps track of lines cleared.
     */
    private int myLines;
    /**
     * Stores the score.
     */
    private int myScore;
    /**
     * Holds the timer values passed to the game panel.
     */
    private int mySendTimer;
    /**
     * Adjusts timer speed for game panel.
     */
    private int myCounter;
    /**
     * Stores a BOARD dimension.
     */
    private int myXAdded;
    /**
     * Stores a BOARD dimension.
     */
    private int myYAdded;
    /**
     * Game menu.
     */
    private JMenu myGameOptions;
    /**
     * Menu class object.
     */
    private Menues myMenues; 
    /**
     * Stores the icon.
     */
    private ImageIcon myIcon1;
    /**
     * Lets other components know it is safe to run.
     */
    private Boolean myStart;
    /**
     * Music player class object.
     */
    private MusicPlayer myPlayer;
    /**
     * Random for A.I. cheating time.
     */
    private Random myKeepTime;
    /**
     * Time between cheats.
     */
    private int myCheater;
    /**
     * Menu listener class object.
     */
    private MenuListeners myListeners = new MenuListeners();
    /**
     * true if cheating.
     */
    private boolean myCheat;




    /**
     * Constructor.
     */
    public TetrisGui() {
        myFrame = new JFrame("Tetris");
        myMainPanel = new GamePane();
        mySidePanel = new SidePane();
        myBoard = new Board();
        myMenues = new Menues();

     
    }
    /**
     * Called by main to start all GUI.
     */
    public void start() {
        final int two = 200;
        final int one = 100;
        
        myStartButtonEnabled = true;
        myItemMenu = new JMenu("Start Menu");        
        myLines = 0;
        myScore = 0;
        mySendTimer = TIMER_ADD;
        myCounter = FOUR;
        myXAdded = DEFAULT_X;
        myYAdded = DEFAULT_Y;
        myPause = new MenuOpen();
        myStart = false;
        myKeepTime = new Random();
        myCheater = myKeepTime.nextInt(two) + one;
        myCheat = false;

        ((GamePane) myMainPanel).setBoard(myBoard);
        myMenues.playMusic();
        myBoard.addObserver(this);
        myFrame.setSize(S_SIZE);

        final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = myFrame.getSize().width;
        final int height = myFrame.getSize().height;

        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setLocation((int) (size.getWidth() - width) / 2,
                            (int) ((size.getHeight() - height) / 2));
        myFrame.add(myMainPanel,  BorderLayout.WEST);
        myFrame.add(mySidePanel, BorderLayout.EAST);
        myFrame.setJMenuBar(myMenues.menuBar());
        myFrame.setVisible(true);
        myFrame.setResizable(false);

    }

    /**
     * Observer method.
     */
    @Override
    public void update(final Observable theO, final Object theArg) {

        if (theArg instanceof String) {
            ((GamePane) myMainPanel).setUpdate(theArg.toString());
            if (myCheat) {
                fakeTimer();
            }
        } else if (theArg instanceof MovableTetrisPiece) {
            ((SidePane) mySidePanel).setUpdate(theArg.toString());
        } else if (theArg instanceof Boolean) {
            ((GamePane) myMainPanel).setBool(theArg);
            myStartButtonEnabled = true;
            myLines = 0;
            myScore = 0;
            myMenues.setStartEnabler(); 
            myStart = false;
        } else if (theArg instanceof Integer[]) { 
            final Integer[] size = (Integer[]) theArg;
            myLines += size.length;
            setScore(size.length);
            ((GamePane) myMainPanel).clearLine((Integer[]) theArg);
            timeCalc();
            ((SidePane) mySidePanel).setLines(myLines, myScore);

        }
    }
    /**
     * Uses the number of actions returned by the board
     * and a random int between 200 - 300
     * to decide when to cheat.
     */
    private void fakeTimer() {
        final int one = 100;
        final int two = 200;
        final int three = 3;
        
        myCheater--;
        if (myCheater == 0) {
            ((GamePane) myMainPanel).setCheat(three);
            myCheater = myKeepTime.nextInt(two) + one;
        }
    }
    /**
     * 
     * Calculates and sets the timer length for GamePane.
     */
    private void timeCalc() {
        final int div = 9;
        final int inc = 5;
        if (myCounter < myLines) {
            mySendTimer = (int) ((mySendTimer * div) / (2 * inc));
            myCounter += inc;
            final int toReturn = mySendTimer;
            ((GamePane) myMainPanel).setTimer(toReturn);

        }
    }

    /**
     * Calculates and sets score for SidePane. 
     * 
     * @param theInt Accepts an int (number of lines).
     */
    private void setScore(final int theInt) {
        final int score = 100;
        final int lvl = 10;
        final int div = 5;

        myScore += ((score * (theInt - 1)) * 2) + (lvl * (int) (myLines / div) + score);

    }
    /**
     * Sets an observer to the board when called.
     */
    private void changeBoard() {
        myBoard.addObserver(this);
    }
    /**
     * Inner class for menu items.
     *  
     */
    public class Menues {
        /**
         * Constructor.
         */
        public Menues() {
            myIcon1 =  new ImageIcon("src/resources/Deadlast Games.jpg");
            myPlayer = new MusicPlayer();
        }
        /**
         * Uses the StringStore to build the help menus.
         * 
         * @return Returns a list of StringStores.
         */
        private ArrayList<StringStore> loadList() {

            final String n = "\n";
            final List<StringStore> list = new ArrayList<>();
            list.add(new StringStore("Scoring...", "SCORING RULES: \n "
                            + n
                            + "A single cleared line is worth 100 points.\n"
                            + "Each additional line cleared at the same time is worth 200. \n"
                            + "an additional 10 points is awarded per a level cleared\n"
                            + "(e.g. 3 lines cleared at level 2 is 100 "
                            + "+ (2 x 200) + (1 x 10)\n"
                            + " = 510 points)."));
            list.add(new StringStore("Game Modes...", "GAME MODES: \n"
                            + n
                            + "1) Normal mode starts at 1 second ticks "
                            + "and speeds up by 1/10th\n"
                            + " of .8 seconds every level."
                            + " An additional 1/5th of a second is added to\n"
                            + "to keep speeds reasonable.\n"
                            + n
                            + "2) Cyber mode has different backgrounds,"
                            + " sounds, music, and it does not get the extra\n"
                            + "1/5 of a second added to the timer that normal mode does.\n"
                            + n
                            + "3) Evil A.I. mode is the same as Cyber "
                            + "mode except no piece preview and the game also cheats."
                            + "/nThe A.I. will hide the game board at random."
                            + n
                            + n
                            + "Note: if game mode is changed to or from normal while"
                            + " playing, timer will \n"
                            + "not update (add / take off the 1/5 of a second)"
                            + " until the current level is passed."));
            list.add(new StringStore("About...", "ABOUT: \n"
                            + n
                            + "TCSS 305 \n"
                            + "Autumn 2017 \n"
                            + "TETRIS \n"
                            + "BY: Daniel Stocksett"));

            return (ArrayList<StringStore>) list;
        }
        /**
         * Enables / disables menu buttons.
         */
        public void setStartEnabler() {
            myItemMenu.getMenuComponent(0).setEnabled(myStartButtonEnabled);
            myItemMenu.getMenuComponent(2).setEnabled(!myStartButtonEnabled);
            myGameOptions.getMenuComponent(0).setEnabled(myStartButtonEnabled);


        }
        /**
         * Creates the menu bar.
         * 
         * @return Returns a JMenuBar.
         */
        private JMenuBar menuBar() {

            final JMenuBar mb = new JMenuBar();
            //final JMenu item = new JMenu("Start Menu");
            final JMenuItem start = new JMenuItem("New Game");
            final JMenuItem end = new JMenuItem("End Game");

            start.addActionListener(myListeners.startListener());
            end.addActionListener(myListeners.endListener());

            myItemMenu.add(start);
            myItemMenu.addSeparator();
            myItemMenu.add(end);
            myItemMenu.addMenuListener(myPause);

            mb.add(myItemMenu);
            mb.add(sizeButtons());
            mb.add(gameOPtions());
            mb.add(helpMenu());
            setStartEnabler();

            return mb;
        }  
        /**
         * Builds the help menu.
         * 
         * @return Returns the help menu.
         */
        private JMenu helpMenu() {
            final JMenu menu = new JMenu("Help");
            menu.addMenuListener(myPause);

            for (StringStore str : loadList()) {
                final JMenuItem item = new JMenuItem(str.getLable());
                item.addActionListener(myListeners.helpListener(str.getHelp()));
                
                menu.add(item);
                menu.addSeparator();
            }
            return menu;
        }
        /**
         * Builds the game options menu.
         * 
         * @return Returns the game options menu.
         */
        private JMenu gameOPtions() {
            myGameOptions = new JMenu("Game Options");
            final JMenuItem sizer = new JMenuItem("Set Board Size...");
            sizer.addActionListener(myListeners.boardSizeListener());

            myGameOptions.add(sizer);
            myGameOptions.addMenuListener(myPause);
            myGameOptions.addSeparator();
            myGameOptions.add(setPlayMode());

            return myGameOptions;
        }
        /**
         * Builds the play mode sub-menu.
         * 
         * @return returns the play mode sub menu.
         */
        private JMenu setPlayMode() {

            final JMenu item = new JMenu("Play Modes...");
            final ButtonGroup group = new ButtonGroup();

            for (LinkedMode butn : modeLoader()) {
                final JRadioButtonMenuItem rad = new JRadioButtonMenuItem(butn);
                rad.setText(butn.getName());
                group.add(rad);
                item.add(rad);
            }
            return item;
        }
        /**
         * Builds all of the menu sliders (3).
         * 
         * @param theStart Start value.
         * @param theBegain Default position.
         * @param theEnd End value.
         * @param theTicks Minor ticks or not.
         * @param theHash Use the hash map if true.
         * @return returns a slide bar.
         */
        private JSlider makeSlider(final int theStart, final int theBegain, final int theEnd,
                                   final Boolean theTicks, final Boolean theHash) {
            final JSlider slider = new JSlider(JSlider.HORIZONTAL,
                                               theStart, theEnd, theBegain);
            final Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();

            table.put(new Integer(0), new JLabel("Small"));
            table.put(new Integer(1), new JLabel("Medium"));
            table.put(new Integer(2), new JLabel("Large"));
            final int xSize = 5;
            if (theTicks) {
                slider.setMinorTickSpacing(1);
                slider.setMajorTickSpacing(xSize);
            } else {
                slider.setMajorTickSpacing(1);
                if (theHash) {
                    slider.setLabelTable(table);
                }
            }
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            return slider;
        }
        /**
         * Creates the size buttons for the menu.
         * 
         * @return Returns a JMenu with buttons.
         */
        private List<LinkedMode> modeLoader() {
            final String str = "./src/resources/i5.jpg";
            final List<LinkedMode> list = new ArrayList<>();
            list.add(new LinkedMode("Normal Mode", false, false,  new javax.swing.
                                    ImageIcon(this.getClass().
                                              getResource("/resources/mfks.jpg"))
                                    , "./src/resources/mfks.jpg"));
            list.add(new LinkedMode("Cyber Mode", true, false,  new javax.swing.
                                    ImageIcon(this.getClass().
                                              getResource("/resources/i5.jpg")),
                                   str));
            list.add(new LinkedMode("Evil A.I. Mode", true, true,  new javax.swing.
                                    ImageIcon(this.getClass().
                                              getResource("/resources/i4.jpg"))
                                    , str));

            return list;

        }
        /**
         * Select game size menu item.
         * 
         * @return returns a JMenu.
         */
        private JMenuItem sizeButtons() {
            final JMenu toReturn = new JMenu("Game Size");
            final JMenuItem size = new JMenuItem("Select Size");
            size.addActionListener(myListeners.slideListener());
            toReturn.addMenuListener(myPause);
            
            toReturn.add(size);

            return toReturn;
        }
        /**
         * Music player method.
         */
        public void playMusic() {
            final Codec c = new JavaDecoder();
            PlugInManager.addPlugIn("com.sun.media.codec.audio.mp3.JavaDecoder",
                                    c.getSupportedInputFormats(),
                                    c.getSupportedOutputFormats(null),
                                    PlugInManager.CODEC);

        }


    }

    /**
     *  Inner class of listeners.
     */
    public class MenuListeners {

        /**
         * listener for the screen size.
         * 
         * @return returns a ActionListener.
         */
        private ActionListener slideListener() {
            return new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent theEvent) {
                    final JSlider slider = myMenues.makeSlider(0, 0, 2, false, true);
                    final JPanel panel = new JPanel();
                    panel.add("Choose Game size", slider);
                    JOptionPane.showOptionDialog(panel, panel, "Set Game Size",
                                                                JOptionPane.OK_CANCEL_OPTION, 
                                                                JOptionPane.QUESTION_MESSAGE,
                                                                null, null, null);
                    final int choice = slider.getValue();
                    if (choice == 0) {
                        sizerHelper(choice, SMALL);
                    } else if (choice == 1) {
                        sizerHelper(choice, MEDIUM);
                    } else {
                        sizerHelper(choice, LARGE);
                    }
                }
            };
        }
        /**
         * Helper method for the size listener.
         * 
         * @param theChoice Selected size.
         * @param theSizer the multiplier for frame size.
         */
        private void sizerHelper(final int theChoice, final int theSizer) {
            final int sml = 90;
            final int big = 20;
            final int side = 210;
            myFrame.setResizable(true);
            myFrame.setSize((myXAdded * theSizer) + (theSizer * 2) + side,
                            (theChoice * big) + sml + (theSizer * myYAdded));

            final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
            final int width = myFrame.getSize().width;
            final int height = myFrame.getSize().height;

            myFrame.setLocation((int) (size.getWidth() - width) / 2,
                                (int) ((size.getHeight() - height) / 2));
            if (theChoice == 0) {
                ((GamePane) myMainPanel).setSizes(SMALL);
            } else if (theChoice == 1) {
                ((GamePane) myMainPanel).setSizes(MEDIUM);
            } else {
                ((GamePane) myMainPanel).setSizes(LARGE);
            }

            myFrame.setResizable(false);
        }
        /**
         * End game button listener.
         * 
         * @return Returns an action listener.
         */
        private ActionListener endListener() {
            return new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent theEvent) {
                    ((GamePane) myMainPanel).setBool(true);
                    myMainPanel.repaint();
                    myStartButtonEnabled = true;
                    myLines = 0;
                    myScore = 0;
                    myMenues.setStartEnabler(); 
                    myPlayer.stopPlay();
                    myStart = false;
                }
            };
        }

        /**
         * Action listener for the start button.
         * 
         * @return r
         */
        private ActionListener startListener() {
            return new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent theEvent) {
                    ((GamePane) myMainPanel).setMusic(myPlayer);
                    ((GamePane) myMainPanel).startSong();
                    myBoard.newGame();
                    ((GamePane) myMainPanel).start();
                    myStartButtonEnabled = false;
                    ((SidePane) mySidePanel).setLines(myLines, myScore);
                    myMenues.setStartEnabler();
                    myStart = true;
                }
            };
        }
        /**
         * Listener for the game boards dimensional adjustment.
         * 
         * @return Returns an action listener.
         */
        private ActionListener boardSizeListener() {
            return new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent theEvent) {
                    sliderHelp();
                }
            };
        }
        /**
         * Helper method for board size listener.
         */
        protected void sliderHelp() {
            final int x = 10; 
            final int y = 20;
            final JSlider slideX = myMenues.makeSlider(x, x, x * 2, true, false);
            final JSlider slidwY = myMenues.makeSlider(y, y, y + 2, false, false);
            final JPanel panel = new JPanel();
            panel.add("Choose X size", slideX);
            panel.add("Choose Y Size", slidwY);
            JOptionPane.showOptionDialog(panel, panel, "Set X value",
                                                        JOptionPane.OK_CANCEL_OPTION, 
                                                        JOptionPane.QUESTION_MESSAGE,
                                                        null, null, null);
            myXAdded = slideX.getValue();
            myYAdded = slidwY.getValue();
            myBoard = new Board(slideX.getValue(), slidwY.getValue());
            ((GamePane) myMainPanel).setBoard(myBoard);
            changeBoard();
            sizerHelper(0, SMALL);            
        }
        /**
         * Listener for the help menu.
         * 
         * @param theHelp iString
         * @return Returns an action listener.
         */
        private ActionListener helpListener(final String theHelp) {
            return new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent theEvent) {
                    final Image largeImage =
                                    myIcon1.getImage().getScaledInstance(80,
                                                                         60,
                                                                         java.awt.
                                                                         Image.SCALE_SMOOTH);
                    final ImageIcon largeIcon = new ImageIcon(largeImage);

                    JOptionPane.showMessageDialog(null, theHelp, "help", 
                                                  JOptionPane.INFORMATION_MESSAGE, largeIcon);
                }
            };
        }
    }
    /**
     * 
     * Inner class for menu listener for pausing the game.     
     */
    public class MenuOpen implements MenuListener {

        /**
         * Only method used, could not find an adapter to extend.
         */
        @Override
        public void menuSelected(final MenuEvent theE) {
            ((GamePane) myMainPanel).setPause();
        }
        @Override
        public void menuDeselected(final MenuEvent theE) {
        }
        @Override
        public void menuCanceled(final MenuEvent theE) {
        }

    }
    /**
     * 
     * Inner class for AbstractAction.
     */
    private final class LinkedMode extends AbstractAction {

        /**
         * Serial id.
         */
        private static final long serialVersionUID = 8558581119919740252L;
        /**
         * Button label.
         */
        private String myName;
        /**
         * Mode choice.
         */
        private Boolean myCyber;
        /**
         * Enables evil A.I.
         */
        private Boolean myAI;
        /**
         * Icon for button.
         */
        private String myImage;
        /**
         * Constructor.
         * 
         * @param theName String.
         * @param theCyber Boolean
         * @param theAI Boolean
         * @param theIcon Icon.
         * @param theImage File name.
         */
        private LinkedMode(final String theName,
                           final Boolean theCyber, final Boolean theAI,
                           final ImageIcon theIcon, final String theImage) {
            myName = theName;
            myCyber = theCyber;
            myAI = theAI;
            myImage = theImage;
            final ImageIcon icon = (ImageIcon) theIcon;
            final Image largeImage =
                            icon.getImage().getScaledInstance(15,
                                                              -1, java.awt.Image.SCALE_SMOOTH);
            final ImageIcon largeIcon = new ImageIcon(largeImage);
            putValue(Action.SMALL_ICON, largeIcon);
            putValue(Action.SELECTED_KEY, true);
        }
        /**
         * Getter for button name.
         * 
         * @return Returns a string.
         */
        public String getName() {
            return myName;

        }
        /**
         * Action performed method sets the other classes to their play mode.
         */
        @Override
        public void actionPerformed(final ActionEvent theE) {

            ((GamePane) myMainPanel).setPicture(myImage);
            ((GamePane) myMainPanel).setMode(myCyber);
            ((SidePane) mySidePanel).setMode(myCyber, myAI);

            if (myAI) {
                myCheat = true;
            } else {
                myCheat = false;
            }
            if (myStart) {
                ((GamePane) myMainPanel).startSong();           
            } 
            myMainPanel.repaint();
        }
    }
}