package ex1.behaviors.menu_actions;
import lejos.nxt.LCD;

import lejos.nxt.Button;
import ex1.SharedState;
import ex1.robot.LineDetector;


public class CalibrateSensors extends BaseBehavior {
	

    public boolean takeControl() {
    	 LCD.drawString("Menu item: " +  SharedState.getMenuSelection(), 0, 6);
    	 return SharedState.getMenuSelection() == SharedState.MA_CALIBRATE;
    }

    public void action() {
    	
        LCD.clear();
        LineDetector lineDetector = robot().getLineDetector();
        
        LCD.drawString("Place on line:" , 0, 0);
        Button.waitForAnyPress();
        lineDetector.clibrateLineValue();
        LCD.drawString("Line value: " + lineDetector.getRequiredValue(), 0, 3);
       
        LCD.drawString("Place on empty.", 0, 0);
        Button.waitForAnyPress();
        lineDetector.clibrateBackgroundValue();
        LCD.drawString("Empty value: " + lineDetector.getEmptyValue(), 0, 4);
        SharedState.setMenuSelection(SharedState.MA_MAIN);   
              
                 
    }

    public void suppress() {
    	        	
    }

}
