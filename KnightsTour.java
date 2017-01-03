// The "KnightsTour" class.
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import java.io.*;
public class KnightsTour extends Frame
{

    //Game variables
    boolean[] [] board = new boolean [8] [8];
    int[] knight = new int [2];
    int[] [] undoMoves;
    int moveCount;

    //Graphics Elements
    Image offScreenImage;
    Graphics offScreenBuffer;
    Image[] knightPiece;
    Image[] visitedSquare;

    int currKnightStyle, currSquareStyle;

    //Interface elements
    MenuItem newOption, exitOption;
    MenuItem typeMoveOption, undoOption;
    MenuItem helpOption, aboutOption;
    MenuItem[] knightStyles, squareStyles;

    //Timer Elements
    Timer timer;
    boolean timerOn;
    int time;
    // Greatest score variables
    static String winnerName = "";
    static int winnerScore = 2147483647;
    String name = JOptionPane.showInputDialog (this, "Please enter your name:");

    public KnightsTour ()  //Constructor method: No parameters and no return.
    {
	super ("KnightsTour");  // Set the frame's name
	setSize (800, 800);     // Set the frame's size

	final int NUMKNIGHTSTYLES = 4; //Constsants representing the number of styles of knight and squares available to choose from
	final int NUMSQUARESTYLES = 4;

	final int NUMUNDOS = 3; //The number of undos allowed. Only this needs to be changed to change the number of undos.
	undoMoves = new int [NUMUNDOS] [2];

	//Setting up the menu
	newOption = new MenuItem ("New");
	newOption.setShortcut (new MenuShortcut (KeyEvent.VK_N));
	exitOption = new MenuItem ("Exit");
	exitOption.setShortcut (new MenuShortcut (KeyEvent.VK_X));
	Menu gameMenu = new Menu ("Game");
	gameMenu.add (newOption);
	gameMenu.add (exitOption);

	typeMoveOption = new MenuItem ("Type Move");
	typeMoveOption.setShortcut (new MenuShortcut (KeyEvent.VK_T));
	undoOption = new MenuItem ("Undo");
	undoOption.setShortcut (new MenuShortcut (KeyEvent.VK_Z));
	Menu knightStyleMenu = new Menu ("Knight Styles");
	knightStyles = new MenuItem [NUMKNIGHTSTYLES];
	for (int i = 0 ; i < NUMKNIGHTSTYLES ; i++) //Since the number of styles can change, this makes sure that there are enough menu options for each one.
	{
	    knightStyles [i] = (new MenuItem ("Style " + (i + 1)));
	    knightStyleMenu.add (knightStyles [i]);
	}
	Menu squareStyleMenu = new Menu ("Square Styles");
	squareStyles = new MenuItem [NUMSQUARESTYLES];
	for (int i = 0 ; i < NUMSQUARESTYLES ; i++)
	{
	    squareStyles [i] = (new MenuItem ("Style " + (i + 1)));
	    squareStyleMenu.add (squareStyles [i]);
	}
	Menu optionMenu = new Menu ("Options");
	optionMenu.add (typeMoveOption);
	optionMenu.add (undoOption);
	optionMenu.add (knightStyleMenu);
	optionMenu.add (squareStyleMenu);

	helpOption = new MenuItem ("Help");
	helpOption.setShortcut (new MenuShortcut (KeyEvent.VK_F1));
	aboutOption = new MenuItem ("About");
	Menu helpMenu = new Menu ("Help");
	helpMenu.add (helpOption);
	helpMenu.add (aboutOption);

	MenuBar mainMenu = new MenuBar ();
	mainMenu.add (gameMenu);
	mainMenu.add (optionMenu);
	mainMenu.setHelpMenu (helpMenu);
	setMenuBar (mainMenu);

	//Initializing game variables
	newGame ();

	//Loading media elements
	MediaTracker tracker = new MediaTracker (this);
	knightPiece = new Image [NUMKNIGHTSTYLES];
	for (int i = 0 ; i < NUMKNIGHTSTYLES ; i++)
	{
	    knightPiece [i] = Toolkit.getDefaultToolkit ().getImage ("KnightStyles\\KnightPiece" + i + ".gif");
	    tracker.addImage (knightPiece [i], 0);
	}
	visitedSquare = new Image [NUMSQUARESTYLES];
	for (int i = 0 ; i < NUMSQUARESTYLES ; i++)
	{
	    visitedSquare [i] = Toolkit.getDefaultToolkit ().getImage ("SquareStyles\\VisitedSquare" + i + ".gif");
	    tracker.addImage (visitedSquare [i], 0);
	}
	try
	{
	    tracker.waitForAll ();
	}
	catch (InterruptedException e)
	{
	}
	currKnightStyle = 0;
	currSquareStyle = 0;

	timerOn = false;
	time = 0;
	timer = new Timer (100, new TimerEventHandler ());
	timer.start ();

	show ();                // Show the frame

    } // Constructor


    public boolean action (Event e, Object arg)  //Takes an Event and an Object as parameters, retuns a boolean. Handles menu options
    {
	for (int i = 0 ; i < knightStyles.length ; i++) //Checks if an item in the style submenus was selected, and changes the appropriate image.
	    if (e.target == knightStyles [i])
	    {
		currKnightStyle = i;
		repaint ();
	    }
	for (int i = 0 ; i < squareStyles.length ; i++)
	    if (e.target == squareStyles [i])
	    {
		currSquareStyle = i;
		repaint ();
	    }
	if (e.target == newOption)
	    newGame ();
	else if (e.target == exitOption)
	{
	    hide ();
	    System.exit (0);
	}
	else if (e.target == typeMoveOption)
	    typeMove ();
	else if (e.target == undoOption)
	    undo ();
	else if (e.target == helpOption)
	    JOptionPane.showMessageDialog (this,
		    "Welcome to the Knight's Tour!\nThe object of this game is to move the knight to every square on the board exactly once. Try to finish as quickly as possible!", "Instructions",
		    JOptionPane.INFORMATION_MESSAGE);
	else if (e.target == aboutOption)
	    JOptionPane.showMessageDialog (this,
		    "Knight's Tour\nBy Kirill and Terry", "About",
		    JOptionPane.INFORMATION_MESSAGE);
	else
	    return false;
	return true;
    }


    public void moveKnight (int boardX, int boardY)  //Moves the knight to a different square if the move is legal. Takes a paid of ints as parameters. No return value, but does modify an array.
    {
	if (Math.abs (knight [0] - boardX) + Math.abs (knight [1] - boardY) == 3) //A knight moves in an L shape: 2 squares in one direction and 1 square in a
	    if (Math.abs (knight [0] - boardX) != 3 && Math.abs (knight [1] - boardY) != 3 && !board [boardX] [boardY]) //perpendicular direction. This is checked by making sure that the knight
	    { //has an overall displacement of 3 squares, but not 3 squares in any one direction
		updateUndo (knight [0], knight [1]); //Changing the undoMoves array to account for the knight moving
		board [knight [0]] [knight [1]] = true; //A value of true means that the square has been visited by the knight and may not be revisited.
		knight [0] = boardX; //Updating the knight's postion
		knight [1] = boardY;
		moveCount++;
		repaint ();
	    }
    }


    public void updateUndo (int boardX, int boardY)  //Moves all items in undoMoves back by one, and adds the knight's last postion to the end. Takes two ints as parameters, no return value but changes an array.
    {
	for (int i = 0 ; i < undoMoves.length - 1 ; i++)
	{
	    undoMoves [i] [0] = undoMoves [i + 1] [0]; //Items in undoMoves are moves back by one, with item 0 being deleted.
	    undoMoves [i] [1] = undoMoves [i + 1] [1];
	}
	undoMoves [undoMoves.length - 1] [0] = boardX; //The newest move is added.
	undoMoves [undoMoves.length - 1] [1] = boardY;
    }


    public void undo ()  //If there are available moves to undo, moves the knight to its previous position. Unavailable moves are marked by {-1, -1}. No parameters and no return type, but does modify an array.
    {
	if (undoMoves [undoMoves.length - 1] [0] != -1 && undoMoves [undoMoves.length - 1] [1] != -1)
	{
	    board [undoMoves [undoMoves.length - 1] [0]] [undoMoves [undoMoves.length - 1] [1]] = false; //Reopening the square the knight is moving to
	    knight [0] = undoMoves [undoMoves.length - 1] [0]; //Updating the knight's position
	    knight [1] = undoMoves [undoMoves.length - 1] [1];
	    for (int i = undoMoves.length - 1 ; i > 0 ; i--)
	    {
		undoMoves [i] [0] = undoMoves [i - 1] [0]; //Similar to updateUndo, but in reverse.
		undoMoves [i] [1] = undoMoves [i - 1] [1];
	    }
	    undoMoves [0] [0] = -1; //When an undo is used up, {-1, -1}, representing no available move, takes the place of the earliest move.
	    undoMoves [0] [1] = -1;
	    moveCount--;
	    repaint ();
	}
    }


    public boolean mouseDown (Event evt, int x, int y)  //Runs whenever the mouse is clicked. Takes an event and two integers as parameters, returns a boolean.
    {
	if (x >= 270 && x <= 750 && y >= 270 && y <= 750) //Checks whether the user is clicking within the board
	{
	    int boardX = (int) ((x - 270) / 60.0); //Converting the screen coordinates into board coordinates by subtracting the offset and dividing by square size.
	    int boardY = (int) ((y - 270) / 60.0);
	    moveKnight (boardX, boardY);
	}
	return true;
    }


    public void typeMove ()
    {
	String position;
	position = JOptionPane.showInputDialog (this, "Please enter in the next postition of the knight:");
	position = position.toLowerCase ();
	char p1 = position.charAt (0);
	int p;
	String a = "abcdefgh";
	int p2 = Integer.parseInt (position.substring (1));

	if (position == null)
	{
	    JOptionPane.showMessageDialog (this, "You selected Cancel",
		    "Message", JOptionPane.WARNING_MESSAGE);
	}
	else
	{

	    if (a.indexOf (p1) != 0 && p2 > 0 && p2 < 8)
	    {
		if (JOptionPane.showConfirmDialog (this, "Are you sure about your move?",
			    "Message", JOptionPane.YES_NO_OPTION) ==
			JOptionPane.YES_OPTION)
		{
		    JOptionPane.showMessageDialog (this, "Good Luck", "Message",
			    JOptionPane.INFORMATION_MESSAGE);
		    p = p1 - 97;
		    int p3 = p2 - 1;
		    moveKnight (p, p3);
		}
	    }
	    else
	    {
		JOptionPane.showMessageDialog (this, "Invalid Input",
			"Message", JOptionPane.WARNING_MESSAGE);
	    }



	}

    }


    public void newGame ()  //Resets game variables, allowing a new game to be started. No parameters or return, but does modify an array.
    {
	//Resetting the board
	for (int i = 0 ; i < board.length ; i++)
	    for (int j = 0 ; j < board [i].length ; j++)
		board [i] [j] = false;
	//Dropping the knight on a new square
	knight [0] = (int) (Math.random () * 8);
	knight [1] = (int) (Math.random () * 8);

	//Resetting game variables
	for (int i = 0 ; i < undoMoves.length ; i++) //All items in undoMoves are replaced with {-1, -1}, clearing the move history.
	{
	    undoMoves [i] [0] = -1;
	    undoMoves [i] [1] = -1;
	}
	moveCount = 0;
	time = 0;
	repaint ();
    }


    public void paint (Graphics g)  //Draws on the screen. Takes a graphics object as its parameter, and does not return anything.
    {
	Font defaultFont = new Font ("Times New Roman", 0, 12); //Declares a font to default.
	// Set up the offscreen buffer the first time paint() is called

	if (offScreenBuffer == null)
	{
	    offScreenImage = createImage (size ().width, size ().height);
	    offScreenBuffer = offScreenImage.getGraphics ();
	    defaultFont = offScreenBuffer.getFont (); //Sets the default font to be the actual default font
	}

	offScreenBuffer.clearRect (0, 0, size ().width, size ().height);
	offScreenBuffer.setFont (defaultFont);

	if (moveCount != 63) //After 63 legal moves, the knight will have reached every square on the board, meeting the win condition.
	{
	    //Drawing the board
	    for (int i = 0 ; i < 8 ; i++) //Paints every other square light grey, like on an actual chessboard
		for (int j = 0 ; j < 8 ; j++)
		{

		    if ((i + j) % 2 == 0)
			offScreenBuffer.setColor (Color.white);
		    else
			offScreenBuffer.setColor (Color.lightGray);
		    offScreenBuffer.fillRect (270 + (60 * i), 270 + (60 * j), 60, 60);
		    if (board [i] [j]) //If the square has been visited, an X is drawn on it.
			offScreenBuffer.drawImage (visitedSquare [currSquareStyle], 270 + (60 * i), 270 + (60 * j), null);
		}
	    offScreenBuffer.setColor (Color.black);
	    //Adding numbers and letters around the board
	    for (int i = 1 ; i <= 8 ; i++)
	    {
		offScreenBuffer.drawString (Integer.toString (i), 255, 245 + (60 * i));
		offScreenBuffer.drawString (Character.toString ((char) (96 + i)), 235 + (60 * i), 260); //'a' is equivalent to 97
	    }

	    //Drawing the Knight
	    offScreenBuffer.drawImage (knightPiece [currKnightStyle], 270 + (60 * knight [0]), 270 + (60 * knight [1]), null);

	    offScreenBuffer.drawRect (270, 270, 480, 480); //Creating the outline for the board

	    offScreenBuffer.setFont (new Font ("Times New Roman", 0, 20));
	    offScreenBuffer.drawString (("Moves so far: " + moveCount), 50, 600); //Shows the player's current move count and time
	    offScreenBuffer.drawString (("Time: " + (time / 10.0) + "s"), 50, 650);

	    offScreenBuffer.drawString ("High Scores", 80, 230); //setting up the high scores table
	    offScreenBuffer.drawRect (25, 250, 220, 300);

	    offScreenBuffer.setFont (new Font ("Castellar", 0, 40)); //Title text
	    offScreenBuffer.drawString ("Knight's Tour", 250, 150);
	}
	else //When thre user completes the tour, a congratulation message is displayed.
	{
	    timer.stop ();
	    int timefinish = time; // The time that the player used to finish the game
	    write (name, timefinish);
	    output ();
	    System.out.println (winnerName + winnerScore);
	    offScreenBuffer.setFont (new Font ("Lucida Console", 0, 48));
	    offScreenBuffer.drawString ("Congrats! you win!", 100, 400);
	    offScreenBuffer.drawString ("Your time: " + (time / 10.0) + "s", 100, 450);
	}

	g.drawImage (offScreenImage, 0, 0, this);
    } // paint method



    public boolean handleEvent (Event evt)  // Handles the close button on the window. Takes an event as its parameter and does not return anything.
    {
	if (evt.id == Event.WINDOW_DESTROY)
	{
	    hide ();
	    System.exit (0);
	    return true;
	}


	// If not handled, pass the event along
	return super.handleEvent (evt);
    }


    public class TimerEventHandler implements ActionListener
    {
	// The following method is called each time a timer event is
	// generated (every 100 milliseconds in this example)
	// Put your code here that handles this event
	public void actionPerformed (ActionEvent event)
	{
	    // Increment the time (you could also count down)
	    time++;

	    // Beep every second for this demo
	    // You probably don't want this annoying beep in your game
	    // Repaint area around timer display only
	    repaint (  /*0, 500, 270, 300*/);
	}
    }


    public static void write (String name, int score)
    {
	try
	{
	    BufferedWriter w = new BufferedWriter (new FileWriter ("score.txt", true));
	    w.write (name);
	    w.newLine ();
	    w.write ("" + score);
	    w.newLine ();
	    w.close ();
	}
	catch (IOException e)
	{
	}
	catch (Exception e)
	{
	}
    }


    public static void output ()
    {
	try
	{
	    BufferedReader r = new BufferedReader (new FileReader ("score.txt"));
	    String playerName = " ", score = "";
	    int thisscor = 0;

	    playerName = r.readLine ();
	    score = r.readLine ();
	    thisscor = Integer.parseInt (score);
	    while (score != null)
	    {

		if (thisscor == winnerScore)
		{

		    winnerName = winnerName + ", " + playerName;
		}
		else
		    if (thisscor < winnerScore)
		    {
			winnerScore = thisscor;
			winnerName = playerName;
		    }
		playerName = r.readLine ();
		score = r.readLine ();
		thisscor = Integer.parseInt (score);
	    }
	}
	catch (NumberFormatException e)
	{
	}
	catch (IOException e)
	{
	    System.out.println (e.getMessage () + " FilenotFOund");
	}
	catch (Exception e)
	{
	    System.out.println (e.getMessage () + " method erro");
	}

    }


    public static void main (String[] args)
    {
	new KnightsTour ();     // Create a KnightsTour frame
    } // main method
} // KnightsTour class
