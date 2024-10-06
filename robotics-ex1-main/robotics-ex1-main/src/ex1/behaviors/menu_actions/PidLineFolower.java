package ex1.behaviors.menu_actions;

import ex1.DebugNotes;
import ex1.SharedState;
import ex1.robot.strategies.PidStrategy;
import ex1.robot.RobotControl;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.util.Delay;


public class PidLineFolower extends BaseBehavior {

    
    public  PidLineFolower() {
		super(SharedState.MA_PID_LINE_FOLLOWER);		
	}
	private boolean started = false; 
	private boolean detecting = false;
    public void action() {
    	SharedState.setMovingDirection(SharedState.MD_CLOCKWISE);

       if (!this.started) {
	        LCD.clear();	        
	        LCD.drawString("Following (PID)", 0, 0);
	        LCD.drawString("Press to start", 0, 2);
	        Button.waitForAnyPress(); 
	        LCD.drawString("              ", 0, 2);
	        
	        
	        robot().followLine(  
	        		new RobotControl(new PidStrategy())     
			);	
	        this.started = true;
    	}
		 
      
       while (!this.started || robot().isMoving()) {
       		Thread.yield();
       	}
       
        DebugNotes.msgWithWait("Time:"+ robot().getMovingTime());
        SharedState.setMenuSelection(SharedState.MA_MAIN);
        
    }
}