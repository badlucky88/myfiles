import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * The GUICell class contains the GUI elements of Cell for display on the GUI panel
 *
 * @author (Hugh Woodord)
 * @version (1.0)
 */
//Declare cell, row, and col. As GUICells are added to grid
//class must implement JButton
public class GUICell extends JButton {
    public Cell cell;    
    private final int row;
    private final int col;
    private final ImageIcon targetIcon = new ImageIcon("target.png");
    private final ImageIcon emptyIcon = new ImageIcon("empty.png");
    private final ImageIcon wallIcon = new ImageIcon("wall.png");
    private final ImageIcon boxIcon = new ImageIcon("box.png");
    private final ImageIcon actorIcon = new ImageIcon("actor.png");
    private final ImageIcon ontargetIcon = new ImageIcon("ontarget.png");

    /**
     * Constructor for objects of class GUICell
     */

    public GUICell(Cell myCell, int r, int c){

        cell = myCell;
        row = r;
        col = c;        
        setSize(new Dimension(25,25));

    }

    
    /**
     * refreshImage() gets the current cell content of the board and repaints the board based on the 
     * cell contents based on the occupant. Invalid occupant will invoke
     * SokobanException.
     */

    public void refreshImage(){

        if (cell.isEmpty()){
            if (cell.isTarget()) {
                setIcon(targetIcon);
            } else {
                setIcon(emptyIcon);
            }
        } else {
            Occupant occ = cell.getOccupant();
            if (occ.isWall()) {
                setIcon(wallIcon);
            } else if (occ.isBox()) {
                setIcon(boxIcon);
            } else if (occ.isActor()) {
                setIcon(actorIcon);
            } else if (occ.onTarget()) {
                setIcon(ontargetIcon);
            } else {

                throw new SokobanException("invalid occupant");
            }
        }
    }
}

