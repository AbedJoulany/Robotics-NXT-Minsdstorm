package ex1.behaviors.robot_actions;
import lejos.nxt.LCD;

import lejos.nxt.Button;
import ex1.SharedState;
import ex1.behaviors.menu_actions.BaseBehavior;
import ex1.robot.LineDetector;


public class CalibrateSensors extends BaseBehavior {
	
	public  CalibrateSensors() {
		super(SharedState.MA_CALIBRATE);		
	}


    public void action() {
    	if (this.isActive() ) {
	        LCD.clear();
	        LineDetector lineDetector = robot().getLineDetector();
	        
	        LCD.drawString("Place on line:" , 0, 0);
	        Button.waitForAnyPress();
	        lineDetector.clibrateLineValue();
	        LCD.drawString("Line value: " + lineDetector.getLineValue(), 0, 3);
	        Button.waitForAnyPress();
	       
	        /*
	        LCD.drawString("Place on edge.", 0, 0);
	        Button.waitForAnyPress();
	        lineDetector.clibrateRequiredValue();
	        LCD.drawString("Edge value: " + lineDetector.getRequiredValue(), 0, 3);
	        Button.waitForAnyPress();
	        */
	        LCD.drawString("Place on empty.", 0, 0);
	        Button.waitForAnyPress();
	        lineDetector.clibrateBackgroundValue();
	        LCD.drawString("Empty value: " + lineDetector.getEmptyValue(), 0, 3);
	        Button.waitForAnyPress();
	        
	        lineDetector.clibrateRequiredValue();
	        LCD.drawString("Edge value: " + lineDetector.getRequiredValue(), 0, 3);
	        Button.waitForAnyPress();
	        
	        SharedState.setMenuSelection(SharedState.MA_MAIN);
    	}
    }


    public void suppress() {
    	super.suppress();
    	this.returnToMainMenu();
    }

    
}
