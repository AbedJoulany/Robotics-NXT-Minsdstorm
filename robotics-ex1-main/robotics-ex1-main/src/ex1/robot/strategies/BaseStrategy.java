package ex1.robot.strategies;

import ex1.DebugNotes;
import ex1.SharedState;
import ex1.robot.LineFollowerRobot;
import lejos.nxt.LCD;


abstract public class BaseStrategy implements IRobotStrategy{
	protected int baseSpeed; 
	private long lastStoreTs = System.currentTimeMillis();
	protected double lastError;
	protected int lastLeft;
	protected int lastRight;
	protected int samplesCount;
	
	public BaseStrategy() {
		baseSpeed= SharedState.RB_BASE_SPEED; 		
		lastError = 0;
		lastLeft = 0 ;
		lastRight= 0;
		samplesCount = 0;
	}
	
	public BaseStrategy(int bSpeed) {
		baseSpeed=bSpeed; 		
		lastError = 0;
		lastLeft = 0 ;
		lastRight= 0;
		samplesCount = 0;
	}
	
	@Override
	abstract public NextStep adjust(int currentLeftSpeed, int currentRightSpeed, double error);
	
	
	public void storeLastValues(int currentLeftSpeed, int currentRightSpeed, double error) {		
		if (System.currentTimeMillis() - lastStoreTs > 100) {
			lastLeft = currentLeftSpeed;
			lastRight = currentRightSpeed;
			lastError = error;
			samplesCount = 0;
			lastStoreTs = System.currentTimeMillis();
		} else {
			 samplesCount++;
			 lastLeft = lastLeft + (currentLeftSpeed - lastLeft) / samplesCount;
			 lastRight = lastRight + (currentRightSpeed - lastRight) / samplesCount; 
			 lastError = lastError + (error - lastError) / samplesCount; 
		}
	}
	
	public int getBaseSpeed() {
		return this.baseSpeed;
	}
	
	
	public void resetBaseSpeed() {
		this.baseSpeed = SharedState.RB_BASE_SPEED;
	}

	public void changeBaseSpeed(double percentage) {
		
		int currentMaxSpeed = Math.max(LineFollowerRobot.getInstance().getLeftSpeed(), LineFollowerRobot.getInstance().getRightSpeed());
		int currentMinSpeed = Math.max(LineFollowerRobot.getInstance().getLeftSpeed(), LineFollowerRobot.getInstance().getRightSpeed());
		double nextSpeed = this.baseSpeed * percentage;
		
		if ((percentage>1.0 && currentMaxSpeed<this.baseSpeed) || (percentage<1.0 && currentMinSpeed > this.baseSpeed)) {
			return; // ignore the change;
		}
		if (nextSpeed > SharedState.RB_MAX_BASE_SPEED) {
			nextSpeed = SharedState.RB_MAX_BASE_SPEED;
		}
		
		if (nextSpeed < SharedState.RB_MIN_BASE_SPEED) {
			nextSpeed = SharedState.RB_MIN_BASE_SPEED;
		}
		this.baseSpeed = (int)Math.ceil(nextSpeed) ;
	}
	
	
	
	protected NextStep getNextStep(double error, double speedCorrection) {
		//DebugNotes.msg("sc:" + speedCorrection );
		double absError = Math.abs(error);
		int fasterSide, slowerSide; 
		
		if (absError < 30 ) {			
			fasterSide = this.getBaseSpeed();
			slowerSide = this.getBaseSpeed() - (int)speedCorrection; 
			//this.changeBaseSpeed(1 + (absError/2));
		} else {			
			fasterSide  = this.getBaseSpeed() + (int)speedCorrection ;
			slowerSide  = this.getBaseSpeed() - (int)speedCorrection;
			//this.changeBaseSpeed(1 - (absError/2));
		}
		
		if (SharedState.getMovingDirection() == SharedState.MD_CLOCKWISE ) {
			if (error>0) {
				return  new IRobotStrategy.NextStep(fasterSide, slowerSide);
			} else {
				return  new IRobotStrategy.NextStep(slowerSide, fasterSide);
			}					
		} else {
			if (error>0) {
				return  new IRobotStrategy.NextStep(slowerSide, fasterSide);
			} else {
				return  new IRobotStrategy.NextStep(fasterSide, slowerSide);
			}
		}
	}
	
	public boolean isTowardLine(double error) {
		DebugNotes.msg("itl:" +  (Math.round(lastError * 100.0) / 100.0 ) + " " + (Math.round(error * 100.0) / 100.0 ) );
		return (lastError - error) > 0 ;
	}
	
	public double getErrorDiff(double error) {
		return error-lastError  ;
	}

	public int getLastLeft(int currentLeftSpeed) {
		return lastLeft - currentLeftSpeed;
	}

	public int getLastRight(int currentRightSpeed) {
		return lastRight - currentRightSpeed;
	}
}
