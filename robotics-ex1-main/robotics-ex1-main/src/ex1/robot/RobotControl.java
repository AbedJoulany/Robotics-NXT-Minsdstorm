package ex1.robot;

import ex1.DebugNotes;
import ex1.SharedState;
import ex1.robot.strategies.IRobotStrategy;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;

import lejos.nxt.SensorPortListener;



public class RobotControl{
		

	public static final int SAMPLE_MS=500;
	private boolean active;
	private IRobotStrategy strategy;
	
	
	public RobotControl(IRobotStrategy strategy) {			
		this.strategy = strategy;
	}
	
	public void stopRobot() {	
		this.active = false;
		robot().stopMoving();		
    }
	
	public boolean isActive() {
		return this.active;
	}
	
	public void run() {
		this.active = true;
		
		initListeners();		
	}	
	
	public int getBaseSpeed() {
		return this.strategy.getBaseSpeed();
	}
	public int getAcceleration() {
		return SharedState.RB_ACCELERATION;
	}
	

	
	private void initListeners() {
		
		robot().setSensorPortListener(LineFollowerRobot.SensorType.TOUCH_SENSOR , new SensorPortListener() {
			@Override
			public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {				
				if (active && aNewValue<600) {
					DebugNotes.msg("Bump Stop" );
					stopRobot();
				}
									
			}			
		});
		
		robot().setSensorPortListener(LineFollowerRobot.SensorType.LIGHT_SENSOR , new SensorPortListener() {
			@Override
			public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {				
				if (active) {
					adjustSpeed();
				}
			}			
		});
		
		
	}
	
	private void adjustSpeed() {
		
		if (!this.active) {
			robot().setSpeed(0, 0);
		}
		
		double error = robot().getError();
		int leftSpeed = robot().getLeftSpeed();
		int rightSpeed = robot().getRightSpeed();		
		IRobotStrategy.NextStep ns = this.strategy.adjust(leftSpeed, rightSpeed, error);
		this.strategy.storeLastValues(leftSpeed, rightSpeed, error);
		LCD.drawString("Left:" + ns.leftSpeed() +  "        ", 0, 2);
		LCD.drawString("Right:" + ns.rightSpeed() +"        " , 0, 3);
		LCD.drawString("Error:" + error +		   "        " , 0, 4);
				
		robot().setSpeed(ns.leftSpeed(), ns.rightSpeed());
	}
	
	private LineFollowerRobot robot() {
		return LineFollowerRobot.getInstance();
	}
	
	

}