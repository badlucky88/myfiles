
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

/**
* GUI representation of Sokoban game
*
* @author Hugh Woodford
* @version 1.0 December 2021
* GUI class extends JFrame to make use of JButtons JPanels and JTextArea.
* All components of textUI are present alongside the JFrame components. These make use of java.awt and
* javax.swing which need to be imported along with awt.events manager
*/
@SuppressWarnings({ "deprecation", "serial" })
public class GUI  extends JFrame {

    //declare GUI components

    private GUICell[][]          cells = null;
    private JPanel               grid   = null;
    private JPanel               optionPanel = null;
    private JButton              clearButton = null;
    private JButton              undoButton = null;
    private JButton              saveButton = null;
    private JButton              loadButton = null;
    private JTextArea            info   = null;
    private Sokoban              puzzle;
    private Player               player;
    private Stack<Direction>     stack;
    private static final String  FILENAME = "screens/screen.1";
    private Scanner              scnr;

    /**
     * Constructor for objects of class GUI
     * Initialise components of GUI() - this is  same as textUI, but also has makeFrame();
     */

    public GUI(){
        super("Sokoban");
        //initialize game components
        scnr   = new Scanner(System.in);
        puzzle = new Sokoban(new File(FILENAME));
        player = new RandomPlayer();
        stack = new Stack<Direction>();

        //makeFrame
        makeFrame();

    }

    /**
     * makeFrame() draws the game window and adds the grid of GUICells, adds command buttons and 
     * event listeners for these buttons. KeyListeners are also added to arrow keys up/down/left/right and 
     * make a player move based on this key input.Note use of @Override in added listeners to help avoid any 
     * potential conflicts with parent class (JFrame)
     */
    private void makeFrame(){
        //make grid of GUICell, using numcol and numrow        
        grid = new JPanel(new GridLayout(puzzle.getNumRows(),puzzle.getNumCols()));
        cells = new GUICell[puzzle.getNumRows()][puzzle.getNumCols()];
        for (int row=0; row<puzzle.getNumRows(); row++) {
            for (int col=0; col<puzzle.getNumCols(); col++) {
                cells[row][col] = new GUICell(puzzle.getCell(row, col),row,col);
                grid.add(cells[row][col]);
            }
        }
        //initialise cell graphical content
        refreshCells();
        ///initialize and name command Buttons
        clearButton = new JButton("Clear");
        undoButton = new JButton("Undo");
        saveButton = new JButton("Save");
        loadButton = new JButton("Load");
        //initialize JPanel "optionPanel" which we will add button commands to      
        optionPanel = new JPanel();  
        //add buttons       
        optionPanel.add (clearButton);
        //add actionlistener to each button with corresponding command.
        clearButton.addActionListener (new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent a) {
                    clear();                    
                }
            });
        optionPanel.add (undoButton);
        undoButton.addActionListener (new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent b) {
                    undo();                     
                }
            });
        optionPanel.add (saveButton);
        saveButton.addActionListener (new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent c) {
                    save();                    
                }
            });
        optionPanel.add (loadButton);
        loadButton.addActionListener (new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent d) {
                    load();                     
                }
            });
        //add grid then add optionpanel to frame and position buttons south
        add(grid, BorderLayout.NORTH);       
        add(optionPanel, BorderLayout.CENTER);
        //add status info bar to bottom, this wil infomr the user when they have entered a command
        //and other events e.g. gamewin.
        info = new JTextArea();        
        add (new JScrollPane(info), BorderLayout.SOUTH);
        //set window properties
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);   
        setSize(550,500);
        setVisible(true);
        //make sure window spawns somewhere sensible on the screen
        setLocation(300,100);
        requestFocusInWindow();
        //mouse listener grabs focus of the grid of GUICells allowing key command 
        //for move to function correctly
        addMouseListener(new MouseAdapter() {
               @Override
               public void mouseEntered(MouseEvent e) {
                   requestFocusInWindow();
               }

               @Override
               public void mouseExited(MouseEvent e) {

               }
           });

        //Key Listeners for arrow keys to give move commands    
        addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                }
                //loop for key input, carry out direction() methods    
                @Override
                public void keyPressed(KeyEvent e){
                    if (e.getKeyCode() == KeyEvent.VK_UP){
                        north();

                    }
                    else if (e.getKeyCode() == KeyEvent.VK_DOWN){
                        south();

                    }
                    else if (e.getKeyCode() == KeyEvent.VK_LEFT){
                        west();

                    }
                    else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
                        east();

                    }
                    else if (e.getKeyCode() == KeyEvent.VK_R){
                        playerMove();
                    }
                    else setInformation("Invalid Key, use Arrow Keys to move");
                }

                @Override
                public void keyReleased(KeyEvent e){
                }

            });
        setInformation("Use Arrow Keys to move, or press R to make a random move!");
    }

    /**
     * NEW - Clear - same as textUI, but new GUI instance created too.
     */
    void clear(){

        puzzle = new Sokoban (new File(FILENAME));
        stack = new Stack<Direction>();    
                
        setInformation("Game reset");
        refreshCells();
    }

    /**
     * NEW - Undo - same as textUI
     */
    void undo(){
        
        if (stack.empty())
            return;
        Direction top = stack.pop();
        Stack<Direction> oldStack = stack;
        clear();
        setInformation("Move Undone"); 
        for (Direction d : oldStack) {
            puzzle.move(d);            
            stack.push(d);       
        }    
        refreshCells();
        
    }

    /**
     * NEW - save() same as textUI save()
     */
    void save(){
        try {
            PrintStream ps = new PrintStream(new File("save.txt"));
            for (Direction d : stack)
                ps.println(d.toString());
            ps.close();
            setInformation("Game Saved");
        } 
        catch (IOException e) {
            setInformation("An input output error occurred");
        }  
    }

    /** 
     * NEW - load() same as textUI load()
     */
    
    void load(){

        try {
            Scanner fscnr = new Scanner(new File("save.txt"));
            clear();
            while (fscnr.hasNextLine()) {
                Direction d = Direction.valueOf(fscnr.nextLine());
                puzzle.move(d);                  
                stack.push(d);
                setInformation("Game Loaded");
            }
            fscnr.close();   

            
            
        } catch (IOException e) {
            setInformation("an input output error occurred");
        } 
        refreshCells();        
    }

    /**
     * NEW - setInformation sets the text on the JTextArea
     */
    void setInformation (String i){
        info.setText(i);   
    }

    /**
     *Move the actor north, same as text UI but added JTextArea feedback
     */
    private void north() {

        if (!puzzle.canMove (Direction.NORTH)) {
            setInformation("Move not valid");
        }
        else {             
            move(Direction.NORTH);        
            stack.push(Direction.NORTH);
            if (puzzle.onTarget()){
                setInformation("Game won!");}
            else {
                setInformation("Moved North");
            }
        }

    }

    /**
     *Move the actor south, same as text UI but added JTextArea feedback
     */
    private void south() {
        if (!puzzle.canMove (Direction.SOUTH)) {
            setInformation("Move not valid");
        }
        else { 
            move(Direction.SOUTH);
            stack.push(Direction.SOUTH);
            if (puzzle.onTarget()){
                setInformation("Game won!");}
            else {
                setInformation("Moved South");
            }
        }
    }

    /**
     *Move the actor east, same as text UI but added JTextArea feedback
     */
    private void east() {
        if (!puzzle.canMove (Direction.EAST)) {
            setInformation("Move not valid");
        }
        else { 
            move(Direction.EAST);
            stack.push(Direction.EAST);
            if (puzzle.onTarget()){
                setInformation("Game won!");}
            else {
                setInformation("Moved East");
            }
        }
    }

    /**
     *Move the actor west, same as text UI but added JTextArea feedback
     */
    private void west() {
        if (!puzzle.canMove (Direction.WEST)) {
            setInformation("Move not valid");
        }
        else { 
            move(Direction.WEST);
            stack.push(Direction.WEST);
            if (puzzle.onTarget()){
                setInformation("Game won!");}
            else {
                setInformation("Moved West");}
        }

    }

    /**
     * Move the actor according to the computer player's choice and 
     * push move to stack
     */
    private void playerMove() {
        Vector<Direction> choices = puzzle.canMove();
        Direction         choice  = player.move(choices);
        move(choice);
        stack.push(choice);

    } 

    /**
     * If it is safe, move the actor to the next cell in a given direction
     * 
     * @param dir the direction to move
     * 
     * NEW - inform the user via JTextArea, and if move is legal will call to 
     * refreshCells() for repainting.
     */
    private void move(Direction dir) {
        if (!puzzle.canMove(dir)) {
            setInformation("Invalid move");
            return;
        }
        puzzle.move(dir);
        refreshCells();
        if (puzzle.onTarget())
            setInformation("Game won!");
    } 

    /**
     * refreshCells() looks through every cell in GUI and refreshes the image for 
     * each GUI cell based upon their occupant
     */    
    private void refreshCells() {

        for (int row = 0; row < puzzle.getNumRows(); row++) {
            for (int col = 0; col < puzzle.getNumCols(); col++) {
                cells[row][col].refreshImage();
            }
        }
    }
}
