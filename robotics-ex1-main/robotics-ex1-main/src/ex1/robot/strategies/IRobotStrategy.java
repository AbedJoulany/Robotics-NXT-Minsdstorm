package ex1.robot.strategies;

public interface IRobotStrategy {
	final class NextStep {
	    private final int leftSpeed;
	    private final int rightSpeed;
	   
	    public NextStep(int leftSpeed, int rightSpeed) {
	        this.leftSpeed = leftSpeed;
	        this.rightSpeed = rightSpeed;
	    }

	    public int leftSpeed() {
	        return this.leftSpeed;
	    }

	    public int rightSpeed() {
	        return this.rightSpeed;
	    }
	    
	   

	}

	public void storeLastValues(int currentLeftSpeed, int currentRightSpeed, double error);
	
	NextStep adjust(int currentLeftSpeed, int currentRightSpeed, double error);	
	
	int getBaseSpeed();	    	
}

