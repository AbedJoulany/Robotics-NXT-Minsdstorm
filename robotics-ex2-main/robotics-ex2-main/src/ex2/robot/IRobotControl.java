package ex2.robot;

import ex2.robot.IRobotControl.State;

public interface IRobotControl {

	public enum State {
		PENDING,
		STARTED,	 
		FIRST_LINE,
		SECOND_LINE,
		THIRD_LINE,
		FOURTH_LINE,
		ON_SIDE,
		BUMPED,
		ON_CORNER_BACKWARD,
		ON_CORNER,
		FINISHED_MAPPING,
		LOCATE_POSITION,
		PENDING_NAVIGATION_START_COMMAND,
		PENDING_FLINE_START_COMMAND,
		NAVIGATING_FLINE,
		NAVIGATING_SLINE,
		NAVIGATING_BLINE,
		NAVIGATING_CENTER,
		FLINE_END_NAVIGATION,
		SLINE_END_NAVIGATION,
		ON_CENTER,
		ON_TARGET
	}
	
	
	void run(State startState);

	void rangeChanged(int oldRange, int newRange, double heading);

	void bumpDetected();

	void touchReleased();

	void lineDetected();

	void soundDetected();
	
	void endOfLine();
	
	State getState();
	

}