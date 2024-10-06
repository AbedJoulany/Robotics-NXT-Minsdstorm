package ex1.robot;

import lejos.nxt.LightSensor;
import lejos.nxt.TouchSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.RegulatedMotorListener;
import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.LCD;
import lejos.nxt.Button;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

import java.util.Hashtable;
import java.util.Enumeration;
import ex1.DataLogger;
import ex1.DebugNotes;
import ex1.SharedState;
import ex1.robot.RobotControl;




public class LineFollowerRobot {
	
	public enum SensorType {
		LIGHT_SENSOR,
		TOUCH_SENSOR,	 
	}
	
	private static final LineFollowerRobot instance = new LineFollowerRobot();
	Hashtable<SensorType, SensorPort> sensors = new Hashtable<SensorType, SensorPort>();
	
	private LightSensor lightSensor;
	private TouchSensor touchSensor;
	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;
	
	private LineDetector lineDetector;
	private TachoToDistanceConverter distConverter; 
	private DataLogger logger;
	
	private DifferentialPilot pilot; 
	private RobotControl robotController = null;
	private Thread odometerThread = null ; 
	private double wheelDiameter;
	private double trackWidth;
	
	private long startMovementTime = 0;
	private long endMovementTime = 0;
	public static LineFollowerRobot getInstance() {
        return instance;
    }
	
		
	public void init(double wheelDiameter, double trackWidth, SensorPort lightSensorPort,SensorPort touchSensorPort, NXTRegulatedMotor leftMotor,  NXTRegulatedMotor rightMotor) {
		sensors.put(SensorType.LIGHT_SENSOR, lightSensorPort );
		this.lightSensor = new LightSensor(lightSensorPort);
		
		sensors.put(SensorType.TOUCH_SENSOR, touchSensorPort );
		this.touchSensor = new TouchSensor(touchSensorPort);
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.wheelDiameter= wheelDiameter;
		this.trackWidth= trackWidth;
		this.pilot = null;

		this.lineDetector = new LineDetector(this.lightSensor);
		this.distConverter = new TachoToDistanceConverter(wheelDiameter/2, 360);
		this.logger = new DataLogger("lf"+ System.currentTimeMillis()+ ".csv", 7000);	
	}

	public LightSensor getLightSensor() {
		return lightSensor;
	}
	
	public TouchSensor getTouchSensor() {
		return touchSensor;
	}

	public DifferentialPilot getPilot() {
		if (this.pilot == null) {
			this.pilot =  new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor);
		}
		return this.pilot;
	}
	
	public void setSensorPortListener(SensorType sensorType, SensorPortListener listener) {
		SensorPort port =sensors.get(sensorType);		
		if (port == null) {
			LCD.drawString("invalid sensor " + sensorType.toString(),0,0);
		} else {
			port.addSensorPortListener(listener);
		}		
	}
	
	
	
	
	

	

	public NXTRegulatedMotor getLeftMotor() {
		return leftMotor;
	}

	public NXTRegulatedMotor getRightMotor() {
		return rightMotor;
	}

	public LineDetector getLineDetector() {
		return lineDetector;
	}
	
	public void followLine(RobotControl controll) {		
		
		
		this.robotController = controll;
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
		lineDetector.setLightOn();	
		
		
		startMoving();
		robotController.run();
		  
	}
	
	public void findMovingDirection() {
		startMovementTime = System.currentTimeMillis();
		this.odometerThread = new Thread(new Odometer());
		this.odometerThread.start();
		
		setSpeed(robotController.getBaseSpeed(), robotController.getBaseSpeed(),600);
		Delay.msDelay(200);
		setSpeed(100, 300,1000);
		Delay.msDelay(300);
		setSpeed(300, 300,1000);
		for(int i=0; i<10 ; i++) {
			Delay.msDelay(100);
			if (this.getError()<50) {
				SharedState.setMovingDirection(SharedState.MD_COUNTERCLOCKWISE);
				setSpeed(300, 100,1000);
				Delay.msDelay(300);
				return;
			}
		}
		
		
		setSpeed(300, 100,1000);
		Delay.msDelay(800);
		setSpeed(300, 300,1000);
		for(int i=0; i<10 ; i++) {
			Delay.msDelay(100);
			if (this.getError()<50) {
				SharedState.setMovingDirection(SharedState.MD_CLOCKWISE);
				setSpeed(100, 300,1000);
				Delay.msDelay(300);
				return;
			}
		}
		
		
	}
	
	public void startMoving() {
		findMovingDirection();	
		setSpeed(robotController.getBaseSpeed(), robotController.getBaseSpeed(),robotController.getAcceleration());
		
	}
	
	public void stopMoving() {
		setSpeed(0,0);	
		endMovementTime = System.currentTimeMillis();
		while(isMoving())
	    {
	      this.leftMotor.waitComplete();
	      this.rightMotor.waitComplete();
	    }
		lineDetector.setLightOff();
		logger.flush();
		
		try {
			this.odometerThread.interrupt();
			odometerThread.join();			
		} catch (InterruptedException e) {				
			Thread.currentThread().interrupt();
		}
		DebugNotes.msgWithWait("Time:" +  (System.currentTimeMillis() -startMovementTime)/1000);
			
	}
	
	public double getMovingTime() {
		return (double)(this.endMovementTime - this.startMovementTime)/1000;
	}
	
	public boolean isMoving() {
		if (this.robotController==null)
			return false;
		return this.robotController.isActive() && this.getLeftMotor().isMoving() && this.getRightMotor().isMoving();
	}
	public void setSpeed(int leftSpeed, int rightSpeed, int acceleration) {
		leftMotor.forward();
		rightMotor.forward();
		leftMotor.setAcceleration(acceleration);
		rightMotor.setAcceleration(acceleration);
		setSpeed(leftSpeed, rightSpeed);
	}
	
	public void setSpeed(int leftSpeed, int rightSpeed) {
		
		
		if (leftSpeed>0) {	
			leftMotor.forward();
			leftMotor.setSpeed(leftSpeed);	
		} else if (leftSpeed<0) {
			leftMotor.backward();
			leftSpeed = Math.abs(leftSpeed);
			leftMotor.setSpeed(leftSpeed);
		} else {
			leftMotor.stop(true);
			leftMotor.setSpeed(0);	
		}
		
		if (rightSpeed>0) {
			rightMotor.forward();
			rightMotor.setSpeed(rightSpeed);	
		} else if (rightSpeed<0) {
			rightMotor.backward();
			rightSpeed = Math.abs(rightSpeed);
			rightMotor.setSpeed(rightSpeed);	
		} else {
			rightMotor.stop(true);
			
		}
		/*
		while (Math.abs(leftMotor.getSpeed()-leftSpeed)>5 || Math.abs(rightMotor.getSpeed()-rightSpeed)>5) {
			DebugNotes.msg("acc delay!");
			Delay.msDelay(50);
			DebugNotes.clear();
		}*/
	}
	
	public void setPose(double l, double r, double lx, double ly, double rx, double ry) {
		double ts = (double)(System.currentTimeMillis() -startMovementTime)/1000;
		StatisticsPoint sp = new StatisticsPoint(ts, l, r, lx,   ly,   rx,   ry);
		sp.setLeftSpeed(this.getLeftSpeed());
		sp.setRightSpeed(this.getRightSpeed());
		sp.setLightValue(this.lineDetector.getError());
		logger.log(sp);
	}
	
	public int getLeftSpeed() {
		return leftMotor.getSpeed();
	}
	
	public int getRightSpeed() {
		return rightMotor.getSpeed();
	}
	
	public double getError() {
		return lineDetector.getError();
	}


	public double getLeftDistance() {
		return  distConverter.getDistance(this.leftMotor.getTachoCount());
	}
	
	
	public double getRightDistance() {
		return  distConverter.getDistance(this.rightMotor.getTachoCount());
	}


	public double getWheelDiameter() {
		return wheelDiameter;
	}


	public double getWeelBase() {
		return trackWidth;
	}

	
	
	
}
