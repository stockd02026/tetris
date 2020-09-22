/*
 * Tetris
 * 
 * TCSS 305a017
 */
package view;


/**
 * A JPanel class to hold other JPanel inner class objects.
 * 
 * @author Daniel Stocksett / stockd
 * @version 12-2-17
 *
 */
public class StringStore {
    /**
     * String name.
     */
    private String myLabel;
    /**
     * String help file.
     */
    private String myHelp;
    /**
     * Constructor.
     * 
     * @param theLabel String
     * @param theHelp String
     */
    public StringStore(final String theLabel, final String theHelp) {
        myLabel = theLabel;
        myHelp = theHelp;
    }

    /**
     * Getter for name.
     * @return Returns a string
     */
    public String getLable() {
        return myLabel;
        
    }
    /**
     * getter for help text.
     * @return Returns a string
     */
    public String getHelp() {
        return myHelp;
        
    }
}
