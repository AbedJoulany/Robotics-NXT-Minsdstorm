package ex2.behaviors.menu_actions;

import ex2.DebugNotes;
import ex2.SharedState;
import lejos.nxt.LCD;

import lejos.nxt.Button;


public class MapAreaBehaviour extends BaseBehavior {
	public  MapAreaBehaviour() {
		super(SharedState.MA_MAP);		
	}
	private boolean started = false; 
	
    public void action() {
       if (!this.started) {
	        LCD.clear();	        
	        LCD.drawString("Mapping", 0, 0);
	        LCD.drawString("Press to start", 0, 2);
	        Button.waitForAnyPress(); 
	        LCD.drawString("              ", 0, 2);
	        
	        this.started = true;
	        robot().startScan();  
	        		   
    	}
       
        while (robot().isScanning()) {
        	Thread.yield();
        }
        
        DebugNotes.msgWithWait("Time:"+ robot().getMovingTime());
        SharedState.setMenuSelection(SharedState.MA_MAIN);        
    }
}