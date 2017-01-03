// The "KnightTour" class.
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
public class KnightTour extends Frame
{
    Timer timer;
    boolean timerOn;
    int time;
    int timeAllowed;
    Button keyboard;
    public KnightTour ()
    {
	super ("KnightTour");   // Set the frame's name
	setSize (400, 400);     // Set the frame's size
	show ();                // Show the frame

	timerOn = false;
	time = 0;
	timeAllowed = 100;   //  10 seconds in this example
	timer = new Timer (100, new TimerEventHandler ());
	timer.start ();
	setVisible (true);                // Show the frame
	keyboard = new Button ("keyboard");
	keyboard.setBounds (200, 350, 100, 30);
	add (keyboard);
    } // Constructor


    public void keyboard ()
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

	    if (a.indexOf (p1) != 0 && p2 > 0 && p < 8)
	    {
		p = p1 - 99;
		int p3 = p2 - 1;
		moveKnight (p, p3);
	    }
	    else
	    {
		JOptionPane.showMessageDialog (this, "Invalid Input",
			"Message", JOptionPane.WARNING_MESSAGE);
	    }



	}
	if (JOptionPane.showConfirmDialog (this, "Are you sure about your move?",
		    "Message", JOptionPane.YES_NO_OPTION) ==
		JOptionPane.YES_OPTION)
	    JOptionPane.showMessageDialog (this, "Good Luck", "Message",
		    JOptionPane.INFORMATION_MESSAGE);
    }


    public boolean action (Event e, Object arg)
    {
	if (e.target == keyboard)
	    keyboard ();
	return true;
    }


    public static void main (String[] args)
    {
	new KnightTour ();      // Create a KnightTour frame
    } // main method


    public boolean handleEvent (Event evt)
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


    private class TimerEventHandler implements ActionListener
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
	    repaint (130, 80, 50, 20);
	}
    }


    public void paint (Graphics g)
    {
	g.drawString ("Time: " + (time / 10.0), 100, 100);
    } // paint method



    // Handles the close button on the window


} // KnightTour class


