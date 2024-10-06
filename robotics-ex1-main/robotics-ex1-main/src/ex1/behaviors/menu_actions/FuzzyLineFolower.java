package ex1.behaviors.menu_actions;

import ex1.DebugNotes;
import ex1.SharedState;
import ex1.robot.strategies.ErrorBasedStrategy;
import ex1.robot.strategies.FuzzyStrategy;
import ex1.robot.RobotControl;
import lejos.nxt.LCD;
import lejos.util.Delay;
import lejos.nxt.Button;


public class FuzzyLineFolower extends BaseBehavior {
	public  FuzzyLineFolower() {
		super(SharedState.MA_FUZZY_LINE_FOLLOWER);		
	}
	private boolean started = false; 
	private boolean detecting = false;
    public void action() {
    	

       
       if (!this.started) {
	        LCD.clear();	        
	        LCD.drawString("Following (Fuzzy)", 0, 0);
	        LCD.drawString("Press to start", 0, 2);
	        Button.waitForAnyPress(); 
	        LCD.drawString("              ", 0, 2);
	        
	        
	        this.started = true;
	        robot().followLine(  
	        		new RobotControl(new FuzzyStrategy())     
			);	       
    	}
       
        while (robot().isMoving()) {
        	Thread.yield();
        }
        
        DebugNotes.msgWithWait("Time:"+ robot().getMovingTime());
        SharedState.setMenuSelection(SharedState.MA_MAIN);        
    }
}