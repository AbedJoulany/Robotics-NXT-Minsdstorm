package ex1.behaviors.menu_actions;

import ex1.DebugNotes;
import ex1.SharedState;
import ex1.robot.LineFollowerRobot;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;


public class DetectDirections extends BaseBehavior{	
	private static final int ST_EMPTY = 0;
	private static final int ST_INITIALIZED = 0;
	private static final int ST_DETECTING = 2;
	private static final int ST_FOUND = 3;
	private static final int ST_BUMPED = 4;
	private static final int ST_FINISHED = 5;
	
	private volatile int state = ST_EMPTY; 
	private volatile boolean lineFound = false;
	private volatile boolean bumped = false;
	private volatile boolean listenersInitialized = false;
	
	
	private int guessedDirection =  SharedState.MD_UNKNOWN;
	private int searchInterval =  1000;

	
	long startMovingTime;
	
	//private final int MAX_SEARCH_ITERVAL = 4000;
	private final int MAX_SEARCH_ITERVAL = 20000;
			
    public DetectDirections() {
		super(SharedState.MA_DETECT_DIRECTIONS);		
	}
    
	public boolean takeControl() {
		return super.takeControl();  
    }
	
	
    public void action() {
    	//DebugNotes.msgWithWait("TAKE:" +  this.state );
    	LCD.clear();
    	LCD.drawString("DETECT DIRECTION ", 0, 0);
    	
    	
    	if (this.state == ST_EMPTY) { 
    		
			initialize();
			state = ST_DETECTING;    	        
    	}
    	
    	if (this.state == ST_DETECTING) {
    		lineFound = false;
			while (searchInterval <  MAX_SEARCH_ITERVAL) {				
				switchDirection();		
				Delay.msDelay(searchInterval);
	    		if (bumped) bumped =false;
	    		if (lineFound) {
	    			SharedState.setMovingDirection(this.guessedDirection);
	    			this.state  = ST_FOUND;
	    			break;
	    		}
	    		searchInterval *= 2;
			}
			
 			robot().setSpeed(0, 0);			
 			
			if (!lineFound) {
				LCD.drawString("FAILED! ", 0, 0);
				robot().getLineDetector().setLightOff();	
			}
    	}
    	
    	
    	if (this.state == ST_FOUND) {    		
    		rotateToBase();
    		Delay.msDelay(1000);
    		this.state = ST_FINISHED;   
    		
    	}

    	if (this.state == ST_FINISHED) {
    		this.state = ST_EMPTY;
    		robot().setSpeed(0, 0);
    		this.menuReturToLastAction();
    	}
    	
    }
     
    
    
    private void initialize() {
    	
    	LCD.drawString("Press to start", 0, 2);
        Button.waitForAnyPress(); 
        LCD.drawString("              ", 0, 2);
        
        SharedState.setMovingDirection(SharedState.MD_UNKNOWN);
        this.guessedDirection =  SharedState.MD_COUNTERCLOCKWISE;
        searchInterval = 1000;
        lineFound = false;
        bumped =false;
        this.initListeners();	
        
    }
    
    
    private void switchDirection() {
    	this.guessedDirection *=   -1;
    	
    	
    	startMovingTime  = System.currentTimeMillis();
    	switch(this.guessedDirection) {
    		case  SharedState.MD_CLOCKWISE:
    			robot().setSpeed(400, 200 ,1000);
    			break;
    		case  SharedState.MD_COUNTERCLOCKWISE:
    			robot().setSpeed(200, 400, 1000);
    			
    			break;
    		default:
    			break;    		
    	}
    }
    
    private void rotateToBase() {
    	LCD.drawString("rotateToBase "+(SharedState.movingDirection), 0, 0);
    	
    	switch(this.guessedDirection) {
			case  SharedState.MD_CLOCKWISE:
				robot().setSpeed(150, 300 ,1000);
				break;
			case  SharedState.MD_COUNTERCLOCKWISE:
				robot().setSpeed(300, 150, 1000);				
				break;
			default:
				DebugNotes.msgWithWait("UNKNOWN DIRECTION!");
				break;    		
    	} 
    	
    	
    	
    }
    
    
	private void initListeners() {	
		
		robot().setSensorPortListener(LineFollowerRobot.SensorType.TOUCH_SENSOR , new SensorPortListener() {
			@Override
			public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {
				
				if (state == ST_DETECTING && aNewValue<600) {
					state = ST_BUMPED;
					robot().setSpeed(0, 0,6000);
					robot().setSpeed(-300, -300, 1000);
					Delay.msDelay(400);
					robot().setSpeed(0, 0,6000);
					state = ST_DETECTING;
					bumped = true;
					
				}
									
			}			
		});
		
		
		robot().getLineDetector().setLightOn();	
		robot().setSensorPortListener(LineFollowerRobot.SensorType.LIGHT_SENSOR , new SensorPortListener() {
			@Override
			public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {
				DebugNotes.msg("lig:"+state);
				aNewValue = 1023 - aNewValue; 
				if (state == ST_DETECTING) {									
					if ( Math.abs(aNewValue- robot().getLineDetector().getLineValue()) < 50 ) {				
						lineFound = true;	
						robot().setSpeed(0, 0);	
					}
				}else if ( state == ST_FOUND) {	
					DebugNotes.msg("dif" + (aNewValue- robot().getLineDetector().getRequiredValue()));
					if ( (aNewValue - robot().getLineDetector().getRequiredValue()) > 50 ) {					
						robot().setSpeed(0, 0);
						
					}
				}
			}			
		});
		
	}

}