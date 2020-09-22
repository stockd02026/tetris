/*
 * Tetris
 * 
 * TCSS 305a017
 */
package view;

import java.awt.EventQueue;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 * A JPanel class to hold other JPanel inner class objects.
 * 
 * @author Daniel Stocksett / stockd
 * @version 12-2-17
 *
 */
public final class TetrisMain {
    /**
     * 
     */
    private TetrisMain() {
        throw new IllegalStateException();
    }

    /**
     * Sets the "look and feel" to metal.
     */
    private static void setLookAndFeel() {

        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
        } catch (final UnsupportedLookAndFeelException e) {
            System.out.println("UnsupportedLookAndFeelException");
        } catch (final ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        } catch (final InstantiationException e) {
            System.out.println("InstantiationException");
        } catch (final IllegalAccessException e) {
            System.out.println("IllegalAccessException");
        }
    }

    /**
     * @param theArgs a
     */
    public static void main(final String[] theArgs) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                setLookAndFeel();
                new TetrisGui().start();
            }
            
        }); 
        

    }

}
