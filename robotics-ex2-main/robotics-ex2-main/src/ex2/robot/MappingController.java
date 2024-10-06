package ex2.robot;

import ex2.DebugNotes;
import ex2.SharedState;
import lejos.nxt.LCD;
import lejos.util.Delay;

public class MappingController implements IRobotControl{

	

	protected State state;
	protected PidController speedController = new PidController(SharedState.RB_KEEP_DISTANCE, 100); 

	public MappingController() {			
		setState(State.PENDING);
	}
	
	
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	

	@Override
	public void run(State startState) {
		this.setState(startState);
		robot().driveForward();
	}	
	

	@Override
	public synchronized void rangeChanged(int oldRange, int newRange, double headingDiff) {
		double lastError, error, correction;
		if (newRange >30 ) {
			return; //ignore large values; probably mistake 
		}
		if (oldRange>30) { //fix previous value
			oldRange = newRange;
		}
		newRange = Math.abs(Math.round(newRange * (float)Math.cos(Math.toRadians(headingDiff))));  
		oldRange = Math.abs(Math.round(oldRange * (float)Math.cos(Math.toRadians(headingDiff))));  
		
		DebugNotes.msg("dist: " + newRange + " " + "h: "+ headingDiff+"              ");
		lastError = speedController.getError(oldRange);
		error = speedController.getError(newRange);
		
		LCD.drawString("err " + error,0,2);
		LCD.drawString(" "+ this.state + "                ", 0, 3);
		
		
		switch(this.state) {
			case FIRST_LINE:
			case SECOND_LINE:
			
			case NAVIGATING_BLINE:		
			case ON_SIDE:																									
				correction = speedController.adjust(lastError, error,headingDiff);
				robot().steer(correction);
				break;						
			default:
				break;
		}
	}
	
	

	@Override
	public synchronized void bumpDetected() {
		
		switch(this.state) {
			case NAVIGATING_CENTER:
				robot().stopMoving();
				DebugNotes.msgWithWait("FAILED NAV");
				
			case THIRD_LINE:
				this.setState(State.FINISHED_MAPPING);				
				robot().endScan();
				break;
			case NAVIGATING_FLINE:
			case NAVIGATING_SLINE:
			case LOCATE_POSITION:
			case ON_SIDE:			
				this.setState(State.BUMPED);
				robot().releaseBump();				
				break;
			default:
				break;
		}
		
	}

	@Override
	public void touchReleased() {	
		switch(this.state) {
			case BUMPED:	
				this.setState(State.ON_CORNER_BACKWARD);
				robot().driveBackward();
				this.setState(State.ON_CORNER);
				robot().endCornerTurn();
				this.setState(State.ON_SIDE);
				robot().driveForward();				
				break;
			default:
				break;
		}
		
	}
	

	@Override
	public void lineDetected() {
		
		switch(this.state) {
			case STARTED:			
				this.setState(State.FIRST_LINE);
				break;
			case FIRST_LINE:				
				this.setState(State.SECOND_LINE);
				break;
			case ON_SIDE:				
				this.setState(State.THIRD_LINE);
				break;
			case THIRD_LINE:
				this.setState(State.FOURTH_LINE);
				break;
			default:
				break;
		}
	}
	

	@Override
	public void endOfLine() {
		
		switch(this.state) {
			case SECOND_LINE:
				this.setState(State.ON_SIDE);
				//robot().driveForward();				
				break;
				
			case FOURTH_LINE:
				this.setState(State.FINISHED_MAPPING);
				robot().endScan();
			default:
				break;
				
		}
		
	}
	
	@Override
	public void soundDetected() {
		
	}

	
	protected AreaMapperRobot robot() {
		return AreaMapperRobot.getInstance();
	}
	

}