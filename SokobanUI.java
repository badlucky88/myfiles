
import java.io.*;
import java.util.*;

/**
 * A text-based user interface for a Sokoban puzzle.
 * 
 * @author Dr Mark C. Sinclair
 * @version September 2021
 * Additions / modifications denoted by NEW.
 * @Modified by Hugh Woodford
 * @Modified December 2021
 */

/**
 * NEW Stack declared
 * 
 */

public class SokobanUI {
    
    private Scanner            scnr           = null;
    private Sokoban            puzzle         = null;
    private Player             player         = null;
    private Stack<Direction>   stack          = null;
    private static final String  FILENAME = "screens/screen.1";

    private static final boolean   traceOn = false; // for debugging
    /**
     * Default constructor
     * NEW added stack of directions, new stack is instantiated on new game
       */
    public SokobanUI() {
        scnr   = new Scanner(System.in);
        puzzle = new Sokoban(new File(FILENAME));
        player = new RandomPlayer();
        stack = new Stack<Direction>();
    }

    /**
     * Main control loop.  This displays the puzzle, then enters a loop displaying a menu,
     * getting the user command, executing the command, displaying the puzzle and checking
     * if further moves are possible
     */
    public void menu() {
        String command = "";
        System.out.print(puzzle);
        while (!command.equalsIgnoreCase("Quit") && !puzzle.onTarget())  {
            displayMenu();
            command = getCommand();
            execute(command);
            System.out.print(puzzle);
            if (puzzle.onTarget())
                System.out.println("puzzle is complete");
            trace("onTarget: "+puzzle.numOnTarget());
        }
    }

    /**
     * Display the user menu
     */
    private void displayMenu()  {
        System.out.println("Commands are:");
        System.out.println("   Move North         [N]");
        System.out.println("   Move South         [S]");
        System.out.println("   Move East          [E]");
        System.out.println("   Move West          [W]");
        System.out.println("   Player move        [P]");
        System.out.println("   Undo move          [U]");
        System.out.println("   Restart puzzle [Clear]");
        System.out.println("   Save to file    [Save]");
        System.out.println("   Load from file  [Load]");
        System.out.println("   To end program  [Quit]");    
    }

    /**
     * Get the user command
     * 
     * @return the user command string
     */
    private String getCommand() {
        System.out.print ("Enter command: ");
        return scnr.nextLine();
    }

    /**
     * Execute the user command string
     * 
     * @param command the user command string
     * NEW Direction commands added (north,sout,east,west) as well as save, load, undo and clear
     */
    private void execute(String command) {
        if (command.equalsIgnoreCase("Quit")) {
            System.out.println("Program closing down");
            System.exit(0);
        } else if (command.equalsIgnoreCase("N")) {
            north();
        } else if (command.equalsIgnoreCase("S")) {
            south();
        } else if (command.equalsIgnoreCase("E")) {
            east();
        } else if (command.equalsIgnoreCase("W")) {
            west();
        } else if (command.equalsIgnoreCase("P")) {
            playerMove();
        } else if (command.equalsIgnoreCase("U")) {
            undo();
        } else if (command.equalsIgnoreCase("Clear")) {
            clear();
        } else if (command.equalsIgnoreCase("Save")) {
            save();
        } else if (command.equalsIgnoreCase("Load")) {
            load();
        } else {
            System.out.println("Unknown command (" + command + ")");
        }
    }

    /**NEW Clear the game and create refresh stack, then inform user game reset was successful**/
    private void clear(){
        puzzle = new Sokoban (new File(FILENAME));
        stack = new Stack<Direction>();
        System.out.println ("Game reset");

        
    }

    /** NEW save the game by writing the stack of Directions to file and inform the user
       try and catch used to help identify any associated errors**/
    private void save(){     
         try {
        PrintStream ps = new PrintStream(new File("save.txt"));
        for (Direction d : stack)
        ps.println(d.toString());
        ps.close();
        System.out.println("game saved to file");
        } catch (IOException e) {
        System.out.println("an input output error occurred");
        }    
    }

    /**NEW load the game using a scanner on the saved txt file. This converts the string to its enum value
       by creating objects of direction (d) using the valueOf command then pushing the converted direction
       value into the stack. User is then informed if load was successful, and try and catch is used to 
       help identify associated IO errors**/
    private void load(){
            try {
      Scanner fscnr = new Scanner(new File("save.txt"));
      clear();
      while (fscnr.hasNextLine()) {
        Direction d = Direction.valueOf(fscnr.nextLine());        
        puzzle.move(d);
        stack.push(d);
      }
      fscnr.close();
      System.out.println("game loaded from file");
    } catch (IOException e) {
      System.out.println("an input output error occurred");
    } 
    }

    /**NEW undo the last move, if stack is empty do nothing, else pop the latest move from the stack
       and rebuild the stack using oldStack to prevent loss of stack data**/
    private void undo(){
        if (stack.empty())
          return;
        Direction top = stack.pop();
        Stack<Direction> oldStack = stack;
        clear();
        for (Direction d : oldStack) {
            puzzle.move(d);
            stack.push(d);
        }
    }

    /**
     *NEW  Move the actor North - used an if/else to stop invalid moves being pushed into the stack,
     *this is not necessary to play the game but stops any problems with load() where invalid moves would 
     *be recorded and cause a violation of Sokoban.canMove on load() and cause a fatal system error.
     */
    private void north() {
        
        if (!puzzle.canMove (Direction.NORTH)) {
            return;
        }
            else { 
        move(Direction.NORTH);        
        stack.push(Direction.NORTH);}

    }
    /**
     * NEW Move the actor South -same as North
     */
    private void south() {
        if (!puzzle.canMove (Direction.SOUTH)) {
            return;
        }
            else { 
        move(Direction.SOUTH);
        stack.push(Direction.SOUTH);
    }
    }

    /**
     * NEW Move the actor East - same as North
     */
    private void east() {
        if (!puzzle.canMove (Direction.EAST)) {
            return;
        }
            else { 
        move(Direction.EAST);
        stack.push(Direction.EAST);
    }
    }

    /**
     * Move the actor West - same as North
     */
    private void west() {
        if (!puzzle.canMove (Direction.WEST)) {
            return;
        }
            else { 
        move(Direction.WEST);
        stack.push(Direction.WEST);
    }

    }

    /**
     * NEW Move the actor according to the computer player's choice and 
     * push move to stack. The random player will only make valid move choices so the defensive code used
     * above for NSEW is not required.
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
     */
    private void move(Direction dir) {
        if (!puzzle.canMove(dir)) {
            System.out.println("invalid move");
            return;
        }
        puzzle.move(dir);
        if (puzzle.onTarget())
            System.out.println("game won!");
    }
    /**Main Driver for textUI game
     * 
     */
    public static void main(String[] args) {
        SokobanUI ui = new SokobanUI();
        ui.menu();
    }

    /**
     * A trace method for debugging (active when traceOn is true)
     * 
     * @param s the string to output
     */
    public static void trace(String s) {
        if (traceOn)
            System.out.println("trace: " + s);
    }


    
}
