package ex1.behaviors.menu_actions;

import ex1.DebugNotes;
import ex1.SharedState;
import ex1.robot.strategies.ErrorBasedStrategy;
import ex1.robot.strategies.FuzzyStrategy;
import ex1.robot.RobotControl;
import lejos.nxt.LCD;
import lejos.util.Delay;
import lejos.nxt.Button;


public class SimpleLineFolower extends BaseBehavior {
	public  SimpleLineFolower() {
		super(SharedState.MA_SIMPLE_LINE_FOLLOWER);		
	}
	private boolean started = false; 
	private boolean detecting = false;
    public void action() {
    	
      
        if (!this.started) {    			  
	        LCD.clear();	        
	        LCD.drawString("Following (Prop)", 0, 0);
	        LCD.drawString("Press to start", 0, 2);
	        Button.waitForAnyPress(); 
	        LCD.drawString("              ", 0, 2);	        
	        robot().followLine(  
	        		new RobotControl(new ErrorBasedStrategy())     
			);	
	        this.started = true;
    	}
		 
      
        while (robot().isMoving()) {
        	Thread.yield();
        }
        
        DebugNotes.msgWithWait("Time:"+ robot().getMovingTime());
        SharedState.setMenuSelection(SharedState.MA_MAIN);
        
    }
}