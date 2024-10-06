package ex1.behaviors.menu_actions;

import ex1.DataLogger;
import ex1.DebugNotes;
import ex1.SharedState;
import ex1.robot.StatisticsPoint;
import lejos.nxt.LCD;

public class ODOTests extends BaseBehavior{
	DataLogger logger;
	public ODOTests() {
		super(SharedState.MA_ODOTEST);		
		logger = new DataLogger("lf-test-"+ System.currentTimeMillis() +".csv", 1000);
	}
	
	
	public void action() {
		 LCD.clear();	        
	     LCD.drawString("ODOTests", 0, 0);
		StatisticsPoint st  = new StatisticsPoint(1,1,1,1,2,3,4);
		DebugNotes.msg("ffdfdfd");
		logger.log(st);
		logger.flush();
		DebugNotes.msgWithWait("done");
		this.returnToMainMenu();
		
		
	}

}
