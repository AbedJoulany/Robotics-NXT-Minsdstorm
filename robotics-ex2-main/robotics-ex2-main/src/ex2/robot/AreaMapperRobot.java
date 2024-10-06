package ex2.robot;

import lejos.nxt.LightSensor;
import lejos.nxt.TouchSensor;
import lejos.nxt.SoundSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.LCD;
import java.util.Hashtable;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.FixedRangeScanner;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.RangeScanner;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;



import java.util.Timer;
import java.util.TimerTask;


import ex2.robot.IRobotControl;
import ex2.robot.IRobotControl.State;
import ex2.DataLogger;
import ex2.DebugNotes;
import ex2.SharedState;
import ex2.NavigationMap;
import ex2.robot.NavigationController;
import java.util.Random;


public class AreaMapperRobot {
	
	public enum SensorType {
		LIGHT_SENSOR,
		TOUCH_SENSOR,	 
		SOUND_SENSOR,
		ULTRASONIC_SENSOR,
	}
	
	private static final int NUM_PARTICLES = 300;
	private static final int BORDER = 5;
	private static final AreaMapperRobot instance = new AreaMapperRobot();
	Hashtable<SensorType, SensorPort> sensors = new Hashtable<SensorType, SensorPort>();
	
	private IRobotControl robotController;
	private LightSensor lightSensor;
	private TouchSensor touchSensor;
	private SoundSensor soundSensor;
	private UltrasonicSensor ultrasonicSensor;
	
	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;
	
	private RangeProvider rangeProvider;
	private Thread rangeProviderThread;
	private double wheelDiameter;
	private double trackWidth;
	
	private long startMovementTime = 0;
	private long endMovementTime = 0;
	
	private double course = 0; //average heading
	
	private DataLogger logger;
	private DifferentialPilot pilot;
	private OdometryPoseProvider poseProvider;
	private FixedRangeScanner scanner;
	private Timer timer;
	private static final Random random = new Random();
	public static AreaMapperRobot getInstance() {
        return instance;
    }
	
	public void init(double wheelDiameter, double trackWidth, SensorPort lightSensorPort,SensorPort touchSensorPort, SensorPort soundSensorPort, SensorPort ultrasonicSensorPort, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		sensors.put(SensorType.LIGHT_SENSOR, lightSensorPort );
		this.lightSensor = new LightSensor(lightSensorPort);
		this.setLightOff();
		
		sensors.put(SensorType.TOUCH_SENSOR, touchSensorPort );
		this.touchSensor = new TouchSensor(touchSensorPort);
		
		sensors.put(SensorType.SOUND_SENSOR, soundSensorPort );
		this.soundSensor = new SoundSensor(soundSensorPort);
		
		sensors.put(SensorType.ULTRASONIC_SENSOR, ultrasonicSensorPort );
		this.ultrasonicSensor = new UltrasonicSensor(ultrasonicSensorPort);
		this.rangeProvider = new RangeProvider(this.ultrasonicSensor);
		
		
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.wheelDiameter= wheelDiameter;
		this.trackWidth= trackWidth;
		
		pilot = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, false);
		poseProvider = new OdometryPoseProvider(pilot);
		this.logger = new DataLogger("lf"+ System.currentTimeMillis()+ ".csv", 7000);	
		this.timer = new Timer();
	}

	
	public void startScan() {	
		robotController = new MappingController(); 
		initSensors();
		startMappingThreads();
		detectHeading(50);		
		setLightOn();
		robotController.run(State.STARTED);
		//robotController.run(State.ON_SIDE);
		this.setCourse();
	}
	
	public void endScan() {
		this.stopMoving();
		storeSensorsValues();
		this.setLightOff();
		this.logger.stop();
		
		stopRangeProvider();
		this.timer.cancel();

	}
	
	public void endNvaigation() {
		this.stopMoving();
		this.setLightOff();
		stopRangeProvider();
	}
	
	public void stopRangeProvider() {
		try {
			DebugNotes.msg("stoping RangeP");
			this.rangeProvider.stop();
			rangeProviderThread.interrupt();
			rangeProviderThread.join(1);		
			DebugNotes.msg("stopped");
		} catch (InterruptedException e) {				
			Thread.currentThread().interrupt();
		}
	}
	
	
	public void startNavigation() {
        robotController = new NavigationController(); 
        initSensors();
        setLightOn();
        robotController.run(State.LOCATE_POSITION);
	}
	
	public void goToWall() {
		this.pilot.setTravelSpeed(.3f * this.pilot.getMaxTravelSpeed());
		this.pilot.setRotateSpeed(.3f * this.pilot.getMaxRotateSpeed());
		this.pilot.setAcceleration(500);
		
		 
		RangeReading nearest = getMinRange(this.getScanner().getRangeValues());
		if (nearest !=null) {
			int travelDistance = (int)nearest.getRange(); 
			int rotateAngle = (int)(nearest.getAngle());
			//DebugNotes.msgWithWait("near: a"+ rotateAngle + ": t" + (travelDistance-10));
	        pilot.rotate(rotateAngle);
	        pilot.travel(travelDistance-10);
	        pilot.rotate(-90);
	        this.setCourse();
	        this.scanner = null;// not used anymore
	        //detectHeading();
	        startRangeProvider();
		}
	}
	
	public RangeReading getMinRange(RangeReadings rr) {
		
		float minRange=99999;
		float minAngle=99999;
		RangeReading min=null;
		for (RangeReading r :rr) {
			//DebugNotes.msgWithWait("obs:"+ r.getAngle() + "-" + r.getRange());
			if ( r.getRange() >0 &&  r.getRange() <= minRange ) {				
				minRange = r.getRange();
				minAngle = r.getAngle();
				min = r;
			}
		}
		//DebugNotes.msgWithWait("min:"+ min.getAngle() + "-" + min.getRange());
		return min;
		
	}
	
	
	public void randomStep() {
		this.pilot.setTravelSpeed(.3f * this.pilot.getMaxTravelSpeed());
		this.pilot.setRotateSpeed(.3f * this.pilot.getMaxRotateSpeed());
		this.pilot.setAcceleration(500);
		int travelDistance = 10 + random.nextInt(100); 
        int rotateAngle = 15 + random.nextInt(155);
        
        
        pilot.rotate(rotateAngle);
        if (this.getScanner() instanceof LeftyFixedRangeScanner) {
        	float nearestObs = ((LeftyFixedRangeScanner)(this.getScanner())).getNearestObsticle(rotateAngle);
        	//DebugNotes.msgWithWait("no: " + rotateAngle + " " + (nearestObs));
        	travelDistance  = (int)Math.min(travelDistance, nearestObs);
        }
        pilot.travel(travelDistance);
        
        
        /*
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
	}
	
	public void setSensorPortListener(SensorType sensorType, SensorPortListener listener) {
		if (sensorType==SensorType.ULTRASONIC_SENSOR) {
			this.rangeProvider.addListener(listener);
		} else {
			SensorPort port =sensors.get(sensorType);		
			if (port == null) {
				LCD.drawString("invalid sensor " + sensorType.toString(),0,0);
			} else {
				port.addSensorPortListener(listener);
			}	
		}
	}
	
	public void slowSpeed() {
		this.pilot.setTravelSpeed(.1f * this.pilot.getMaxTravelSpeed());
		this.pilot.setRotateSpeed(.1f * this.pilot.getMaxRotateSpeed());
		this.pilot.setAcceleration(100);
	}
	public void resetSpeed() {
		this.pilot.setTravelSpeed(.3f * this.pilot.getMaxTravelSpeed());
		this.pilot.setRotateSpeed(.3f * this.pilot.getMaxRotateSpeed());
		this.pilot.setAcceleration((int)(SharedState.RB_ACCELERATION));
	}
		
	public void driveForward() {
		this.pilot.setTravelSpeed(.3f * this.pilot.getMaxTravelSpeed());
		this.pilot.setRotateSpeed(.3f * this.pilot.getMaxRotateSpeed());
		this.pilot.setAcceleration((int)(SharedState.RB_ACCELERATION));
		storeSensorsValues();
		this.pilot.forward();

		
		//this.setSpeed(SharedState.RB_BASE_SPEED,SharedState.RB_BASE_SPEED, SharedState.RB_ACCELERATION);
	}
	
	public void spiralDrive() {
		this.setCourse();
		driveForward();			
	}
	
	
	public void driveBackward() {
		this.pilot.setTravelSpeed(.1f * this.pilot.getMaxTravelSpeed());
		this.pilot.setRotateSpeed(.1f * this.pilot.getMaxRotateSpeed());
		this.pilot.setAcceleration((int)(100));		
		this.pilot.travel(-5,true);
		while(pilot.isMoving())Thread.yield();
		while (this.getObstacleDistance()<5) {
			this.pilot.travel(-2,true);
			while(pilot.isMoving())Thread.yield();
		}
		storeSensorsValues();
		resetSpeed();
		//this.setSpeed(SharedState.RB_BASE_SPEED,SharedState.RB_BASE_SPEED, SharedState.RB_ACCELERATION);
	}
	
	
	
	public void stopMoving() {
		//this.setSpeed(0,0);
		this.pilot.stop();
	}
	
	public Pose checkLocation() {
		this.scanner = null;
		RangeReadings ranges = this.getLocalizingScanner().getRangeValues();
		return new Pose( -1*ranges.getRange(1),ranges.getRange(0), 90);
	}
	
	public void detectHeading(float initialRotation) {
		this.slowSpeed();
		this.pilot.rotate(initialRotation);	
		detectHeading();
	}
	
	public void detectHeading() {
		slowSpeed();
		RangeReadings ranges = this.getHeadindScanner().getRangeValues();
		int numReadings =  ranges.getNumReadings();
		float bestAngle = 0;
	    float minError = 999999;
	    	    	  
        for (int i = 2; i < numReadings - 2; i++) {
        	float D =  ranges.getRange(i);
        	float error = 0;
        	for (int j=-2 ; j<=2 ;j++) { 
        		if (j == 0) continue;
        		float expectedDistance =  D * (float) Math.cos(Math.toRadians( ranges.getAngle(i) -  ranges.getAngle(i+j)));
        		error += Math.pow(expectedDistance -  ranges.getRange(i+j),2);
        	}                               
            
	        if (error < minError) {
	            minError = error;	           
	            bestAngle =  ranges.getAngle(i) ;
	        }
	        
        }
        
        
     	this.pilot.rotate( bestAngle);
	    
		this.stopMoving();
	    this.setCourse();
	   
	    this.resetSpeed();
    }
	    
	public float detectHeading_DEP2() {
		//this.stopRangeProvider();
		slowSpeed();
		
		RangeReadings ranges = this.getHeadindScanner().getRangeValues();
		 int minIndex = 0;
		 float minRange = 99999;  
		 for (int i=0 ; i< ranges.getNumReadings() - 1 ; i++){
            if (ranges.getRange(i) < minRange) {
                minRange = ranges.getRange(i);
                minIndex =i;
            }
        }
		
		int count= 0;
	    int sum = 0;
	    
        for (int i=minIndex ; i< Math.min(minIndex+3,ranges.getNumReadings()- 1) ; i++){
	    //for (int i=minIndex ; i< ranges.getNumReadings()-1; i++){
        	//DebugNotes.msgWithWait( "r:" +ranges.getRange(i));
        	//DebugNotes.msgWithWait( "ra:" +ranges.getRange(i)+"-" + ranges.getAngle(i));
            if (ranges.getRange(i) == minRange){ 
               count++;
               
               sum+= ranges.getAngle(i);
            }
        }
        //DebugNotes.msgWithWait( "an" +sum/count);
    
	    this.pilot.rotate( sum/count );
	    
		
		this.stopMoving();
	    this.setCourse();
	    this.resetSpeed();
	    
	    
		//this.startRangeProvider();
		
		//DebugNotes.msgWithWait("mi"+minIndex);
		//DebugNotes.msgWithWait("dd"+sum/count);
		
		return minRange;		
	}
	
	public float detectHeading_DEP1() {	
		this.stopRangeProvider();
		RangeReadings ranges = this.getHeadindScanner().getRangeValues();
		
		int leftIndex = 0;
        int rightIndex = ranges.getNumReadings() - 1;
        float minRange = 99999;        
        
        while (leftIndex <  rightIndex) {
            if (ranges.getRange(leftIndex) < ranges.getRange(rightIndex)) {
                if (ranges.getRange(leftIndex) < minRange) {
                    minRange = ranges.getRange(leftIndex);   
                }else {
                	leftIndex++;                
                }
                while(leftIndex < rightIndex && ranges.getRange(rightIndex) > minRange){
                    rightIndex--; 
                }
                
            } else {
                if (ranges.getRange(rightIndex) < minRange) {
                    minRange = ranges.getRange(rightIndex);                     
                }else {
                	rightIndex--;
                }
                while(leftIndex < rightIndex &&  ranges.getRange(leftIndex) > minRange){
                    leftIndex++; 
                }
            }
        }
        int count=0;
        float sum=0;
        for (int i=leftIndex ; i< ranges.getNumReadings() - 1  ; i++) {
        	if (ranges.getRange(i) ==minRange ) {
	        	sum += ranges.getAngle(i);
	        	count++;
        	}
        }
              
		this.pilot.rotate( sum/count );
		
		this.pilot.stop();
		this.startRangeProvider();
		this.setCourse();
		return minRange;
		
	}
	
	public void detectHeading_DEP() {
		this.pilot.quickStop();
		this.pilot.setTravelSpeed(.1f * this.pilot.getMaxTravelSpeed());
		this.pilot.setRotateSpeed(.1f * this.pilot.getMaxRotateSpeed());	
		this.pilot.setAcceleration((int)(100));
		int  minDistance =255;
		int lastDistnace = 255;
		double minHeading = 0;
			
		for(int i=0 ; i<7 ; i++ ) {	
			this.pilot.rotate(5, true);
			while(pilot.isMoving())Thread.yield();
			Delay.msDelay(50);//wait for next sensor reading 
			int distance  = this.getObstacleDistance();
			if (lastDistnace!= 255 && Math.abs(distance - lastDistnace)>5)
				distance  = this.getObstacleDistance();
				Delay.msDelay(50);//wait for next sensor reading
			if (distance < minDistance) {
				minDistance = distance;
				minHeading = this.getHeading();
			}	
			lastDistnace = distance; 
		}
		
		lastDistnace = 255;
		for(int i=0 ; i<14 ; i++ ) {			
			this.pilot.rotate(-5, true);	
			while(pilot.isMoving())Thread.yield();
			Delay.msDelay(50);//wait for next sensor reading 
			int distance  = this.getObstacleDistance();
			if (lastDistnace!= 255 && Math.abs(distance - lastDistnace)>5) {
				distance  = this.getObstacleDistance();
				Delay.msDelay(50);//wait for next sensor reading
			}
			if (distance < minDistance  ) {
				minDistance = distance;
				minHeading = this.getHeading();
			}
			lastDistnace = distance; 
		}
			
		
		this.pilot.rotate( calcAngleDiff( minHeading , this.getHeading()), true);
		while(pilot.isMoving())Thread.yield();
		this.pilot.stop();
		this.setCourse();		
	}
	

	
	
	public  void releaseBump() {		
		stopMoving();
		storeSensorsValues();
		this.pilot.backward();
		
		//this.setSpeed(-50, -450, 2000);
	}
	
	public  void endCornerTurn() {
		stopMoving();
		storeSensorsValues();
		detectHeading();
		storeSensorsValues();
		
	}
	
	public void travel(double dist) {
		//LCD.drawString("steer " + turnRate,0,1);
		this.pilot.travel(dist);
	}
	
	public void steer(double turnRate) {
		//LCD.drawString("steer " + turnRate,0,1);
		this.pilot.steer(turnRate);
	}

	public void rotate(double angle) {
		//LCD.drawString("steer " + turnRate,0,1);
		this.pilot.rotate(angle);
	}
	
	
	private void initSensors() {
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();		
		
		
		this.setSensorPortListener(AreaMapperRobot.SensorType.SOUND_SENSOR , new SensorPortListener() {			
						
			public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {				
				LCD.drawString("sound: " + aNewValue , 0, 1);
				if (aNewValue<600) {	
				

					robotController.soundDetected();
				}
			}			
		});
		
		
		this.setSensorPortListener(AreaMapperRobot.SensorType.TOUCH_SENSOR , new SensorPortListener() {			
			private boolean bumped = false;			
			public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {	
				
				if (aNewValue<600) {
					bumped = true;
					robotController.bumpDetected();
				} else if (bumped &&  aNewValue>600) {
					bumped = false;
					robotController.touchReleased();
				}								
			}			
		});
		
		this.setSensorPortListener(AreaMapperRobot.SensorType.LIGHT_SENSOR , new SensorPortListener() {
			private boolean onLine = false;
			@Override
			public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {
				aNewValue = 1024-aNewValue;
				//LCD.drawString("light:"+ aNewValue , 0, 1);
				if (!onLine && aNewValue<380.0)  {
					onLine = true;
					robotController.lineDetected();
				} else if (onLine &&  aNewValue>450) {
					onLine = false;
					robotController.endOfLine();
				}	
			}			
		});
		
		
		this.setSensorPortListener(AreaMapperRobot.SensorType.ULTRASONIC_SENSOR , new SensorPortListener() {									
			
			@Override
			public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {	
				LCD.drawString("course:"+ course , 0, 1);
				double headingDiff = getHeadingDiff();	
				robotController.rangeChanged(aOldValue, aNewValue, headingDiff);												
			}			
		});
		
		

	}
	
	public void startMappingThreads() {
		startRangeProvider();
		
		startStatisticsCollection();
	}
	public void startRangeProvider() {
		this.rangeProviderThread = new Thread(this.rangeProvider);		
		rangeProviderThread.start();
	}
	public void startStatisticsCollection() {		 
		TimerTask task = new TimerTask() {	           
            public void run() {
            	storeSensorsValues();
            }
        };
	    this.timer.scheduleAtFixedRate(task, 0, 250);	    
	}
	
	
	public LightSensor getLightSensor() {
		return lightSensor;
	}
	
	public TouchSensor getTouchSensor() {
		return touchSensor;
	}
	
	
	public UltrasonicSensor getUltrasonicSensor() {
		return ultrasonicSensor;
	}
	public FilteredRangeFinder getRangeSensor() {
		ultrasonicSensor.continuous();
		return new FilteredRangeFinder(ultrasonicSensor);
	}

	public double getHeadingDiff() {	
		return calcAngleDiff( this.course , this.getHeading());
		
	}
	
	public double calcAngleDiff(double a , double b) {	
		double diff = a - b;
		if (diff >= 180)
			return diff - 360;
		return  diff;
	}
	 
	
	
	public double getHeading() {	
		return  poseProvider.getPose().getHeading();
		/*
		double heading = poseProvider.getPose().getHeading();
		heading = (heading) % 360;
        if (heading < 0) {
            heading += 360;
        }	
        return heading;
        */
	}
	
	
	public void setCourse() {			
		this.course = this.getHeading();
		
		DebugNotes.msg("course:" +this.course);
		
	}

	public double getMovingTime() {
		return (double)(this.endMovementTime - this.startMovementTime)/1000;
	}
	
	public boolean isMoving() {		
		return this.pilot.isMoving();
	}
	public boolean isScanning() {		
		return this.robotController.getState() !=State.FINISHED_MAPPING;
	}
	
	public boolean isNavigating() {		
		return this.robotController.getState() !=State.ON_TARGET;
	}
	

	public synchronized void storeSensorsValues() {
		double ts = (double)(System.currentTimeMillis() - startMovementTime)/1000;	
		Pose pose = poseProvider.getPose();
		logger.log(new StatisticsPoint(ts,
				this.robotController.getState(),
				pose.getX(), 
				pose.getY(),
				this.course,
				pose.getHeading(),
				this.getLightValue(),
				this.getObstacleDistance()
		));
	}
	
	public void setLightOn() {
		if (!this.lightSensor.isFloodlightOn()) {
			this.lightSensor.setFloodlight(true);
		}	 
	}
	
	public void setLightOff() {
		if (this.lightSensor.isFloodlightOn()) {
			this.lightSensor.setFloodlight(false);
		}	 
	}
	
	
	public int getObstacleDistance() {
		return  this.rangeProvider.getDistance();
		
	}
	
	public double getLightValue() {
		return (double)this.lightSensor.getNormalizedLightValue();
	}

	public double getLeftTachoCount() {
		return  this.leftMotor.getTachoCount();
	}
	
	
	public double getRightTachoCount() {
		return  this.rightMotor.getTachoCount();
	}


	public double getWheelDiameter() {
		return wheelDiameter;
	}


	public double getWeelBase() {
		return trackWidth;
	}

	public OdometryPoseProvider getPoseProvider() {
		return poseProvider;
	}
	
	
	public RangeScanner getLocalizingScanner() {
		if (scanner==null) {			
			this.ultrasonicSensor.continuous();
			//scanner = new LeftyFixedRangeScanner(this.pilot, this.getRangeSensor());
			scanner = new FixedRangeScanner(this.pilot, this.ultrasonicSensor);

			float[] angles = {90, 180};
			scanner.setAngles(angles);
		}
		return scanner; 
	}
	
	
	public RangeScanner getHeadindScanner() {
		if (scanner==null) {			
			this.ultrasonicSensor.continuous();
			//scanner = new LeftyFixedRangeScanner(this.pilot, this.getRangeSensor());
			scanner = new FixedRangeScanner(this.pilot, this.ultrasonicSensor);

			//float[] angles = { -40,-35 ,-30,-25, -20,-15, -10, -5, 0,5, 10,15, 20,25,30,40};
			float[] angles = {-35,-40,-45,-50,-55,-60,-65,-70,-75,-80,-85,-90, -95 ,-100,-105,-110};
			scanner.setAngles(angles);
		}
		return scanner; 
	}
	
	public RangeScanner getScanner() {
		if (scanner==null) {			
			this.ultrasonicSensor.continuous();
			//scanner = new LeftyFixedRangeScanner(this.pilot, this.getRangeSensor());
			scanner = new LeftyFixedRangeScanner(this.pilot, this.ultrasonicSensor);

			float[] angles = {0,45,90,135,180,225,270,315};			
			//float[] angles = {0};
			scanner.setAngles(angles);
		}
		return scanner; 
	}
	public MCLPoseProvider getMclPoseProvider(LineMap map) {		

		return new MCLPoseProvider(this.pilot, getScanner(), map, NUM_PARTICLES, BORDER);
    	
		
	}
	public Navigator getNavigator(PoseProvider mcl) {
		return new Navigator(this.pilot, mcl);
	}
	

	
	
	
}
