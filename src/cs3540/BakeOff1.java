package cs3540;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.Collections;
import processing.core.PApplet;
import java.awt.Component;
import java.awt.Point;

public class BakeOff1 extends PApplet {
	// when in doubt, consult the Processsing reference:
	// https://processing.org/reference/
	// The argument passed to main must match the class name
	public static void main(String[] args) {
		// Tell processing what class we want to run.
		PApplet.main("cs3540.BakeOff1");
	}

	int margin = 200; // set the margin around the squares
	final int padding = 50; // padding between buttons and also their width/height
	final int buttonSize = 40; // padding between buttons and also their width/height
	ArrayList<Integer> trials = new ArrayList<Integer>(); // contains the order of buttons that activate in the test
	ArrayList<Color> buttonColors = new ArrayList<Color>();
	int trialNum = 0; // the current trial number (indexes into trials array above)
	int startTime = 0; // time starts when the first click is captured
	int finishTime = 0; // records the time of the final click
	int hits = 0; // number of successful clicks
	int misses = 0; // number of missed clicks
	Robot robot; // initialized in setup
	Component canvas; // initialized in setup
	Point position; // initialized in setup
	boolean missed = false;

	int numRepeats = 1; // sets the number of times each button repeats in the test

	/**
	 * https://processing.org/reference/settings_.html#:~:text=The%20settings()%20method%20runs,commands%20in%20the%20Processing%20API.
	 */
	public void settings() {
		size(700, 700);
	}

	/**
	 * // https://processing.org/reference/setup_.html
	 */
	public void setup() {
		noCursor(); // hides the system cursor if you want
		noStroke(); // turn off all strokes, we're just using fills here (can change this if you
					// want)
		textFont(createFont("Arial", 16)); // sets the font to Arial size 16
		textAlign(CENTER);
		frameRate(60); // normally you can't go much higher than 60 FPS.
		ellipseMode(CENTER); // ellipses are drawn from the center (BUT RECTANGLES ARE NOT!)
		// rectMode(CENTER); //enabling will break the scaffold code, but you might find
		// it easier to work with centered rects

		try {
			robot = new Robot(); // create a "Java Robot" class that can move the system cursor
		} catch (AWTException e) {
			e.printStackTrace();
		}

		// ===DON'T MODIFY MY RANDOM ORDERING CODE==
		for (int i = 0; i < 16; i++) // generate list of targets and randomize the order
			// number of buttons in 4x4 grid
			for (int k = 0; k < numRepeats; k++)
				// number of times each button repeats
				trials.add(i);
		

		buttonColors.add(Color.RED);
		buttonColors.add(Color.BLUE);
		buttonColors.add(Color.CYAN);
		buttonColors.add(Color.GREEN);
		buttonColors.add(Color.MAGENTA);
		buttonColors.add(Color.ORANGE);
		buttonColors.add(Color.YELLOW);
			
		Collections.shuffle(trials); // randomize the order of the buttons
		System.out.println("trial order: " + trials); // print out order for reference
		
		surface.setLocation(0, 0);// put window in top left corner of screen (doesn't always work)
	}

	public void draw() {
		background(0); // set background to black
		textSize(16);

		if (trialNum >= trials.size()) // check to see if test is over
		{
			float timeTaken = (finishTime - startTime) / 1000f;
			float penalty = constrain(((95f - ((float) hits * 100f / (float) (hits + misses))) * .2f), 0, 100);
			fill(255); // set fill color to white
			// write to screen (not console)
			text("Finished!", width / 2, height / 2);
			text("Hits: " + hits, width / 2, height / 2 + 20);
			text("Misses: " + misses, width / 2, height / 2 + 40);
			text("Accuracy: " + (float) hits * 100f / (float) (hits + misses) + "%", width / 2, height / 2 + 60);
			text("Total time taken: " + timeTaken + " sec", width / 2, height / 2 + 80);
			text("Average time for each button: " + nf((timeTaken) / (float) (hits + misses), 0, 3) + " sec", width / 2,
					height / 2 + 100);
			text("Average time for each button + penalty: "
					+ nf(((timeTaken) / (float) (hits + misses) + penalty), 0, 3) + " sec", width / 2,
					height / 2 + 140);
			return; // return, nothing else to do now test is over
		}

		fill(255); // set fill color to white
		text((trialNum + 1) + " of " + trials.size(), 40, 20); // display what trial the user is on

		for (int i = 0; i < 16; i++)// for all button
			drawButton(i); // draw button

		stroke(255); // set line color to white
		strokeWeight(3);
	    line(mouseX, mouseY - 10, mouseX, mouseY + 10); // Horizontal line for cursor
	    line(mouseX - 10, mouseY, mouseX + 10, mouseY); // Horizontal line for cursor
		
		if(trialNum == 0) {
			
		}
		else {
			//show current timer
			textSize(32);
			fill(255);
			text((millis() - startTime)/1000f, 350, 175);
			if(missed == false) {
				fill(0, 255, 0);
				text("HIT!", 350, 125);
			}
			else if (missed == true) {
				fill(255, 0, 0);
				text("MISSED!", 350, 125);
			}	
		}

	}

	public void mousePressed() // test to see if hit was in target!
	{
		// setup for getting the exact center of window
		canvas = (Component) surface.getNative();
		position = canvas.getLocationOnScreen();
		
		if (trialNum >= trials.size()) // check if task is done
			return;

		if (trialNum == 0) // check if first click, if so, record start time
			startTime = millis();

		if (trialNum == trials.size() - 1) // check if final click
		{
			finishTime = millis();
			// write to terminal some output:
			System.out.println("we're all done!");
		}

		Rectangle bounds = getButtonLocation(trials.get(trialNum));

		// check to see if cursor was inside button
		if ((mouseX > bounds.x && mouseX < bounds.x + bounds.width)
				&& (mouseY > bounds.y && mouseY < bounds.y + bounds.height)) // test to see if hit was within bounds
		{
			System.out.println("HIT! " + trialNum + " " + (millis() - startTime)); // success
			missed = false;//print out hit on game
			hits++;
		} else {
			System.out.println("MISSED! " + trialNum + " " + (millis() - startTime)); // fail
			missed = true;//print out miss on game
			misses++;
		}

		trialNum++; // Increment trial number

		// Set mouse position to center after each click
		robot.mouseMove(position.x + width/2, position.y  + height/2);
	}

	// probably shouldn't have to edit this method
	public Rectangle getButtonLocation(int i) // for a given button ID, what is its location and size
	{
		int x = (i % 4) * (padding + buttonSize) + margin;
		int y = (i / 4) * (padding + buttonSize) + margin;

		return new Rectangle(x, y, buttonSize, buttonSize);
	}

	// you can edit this method to change how buttons appear
	public void drawButton(int i) {
		strokeWeight(0);
		Rectangle bounds = getButtonLocation(i);

		if (trials.get(trialNum) == i) // see if current button is the target
			fill(buttonColors.get(i % 7).getRed(), buttonColors.get(i % 7).getGreen(), buttonColors.get(i % 7).getBlue()); // if so, fill cyan
		else
			fill(200); // if not, fill gray

		rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public void mouseMoved() {
		// can do stuff everytime the mouse is moved (i.e., not clicked)
		// https://processing.org/reference/mouseMoved_.html
	}

	public void mouseDragged() {
		// can do stuff everytime the mouse is dragged
		// https://processing.org/reference/mouseDragged_.html
	}

	public void keyPressed() {
		// can use the keyboard if you wish
		// https://processing.org/reference/keyTyped_.html
		// https://processing.org/reference/keyCode.html
	}
}