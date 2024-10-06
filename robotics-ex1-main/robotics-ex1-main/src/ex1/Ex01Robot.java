
package ex1;

import ex1.behaviors.menu_actions.*;
import ex1.behaviors.robot_actions.CalibrateSensors;
import ex1.behaviors.robot_actions.EmergencyExit;
import ex1.robot.LineFollowerRobot;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Ex01Robot {	
	
	public static void main(String[] args) {	
		try {
			LineFollowerRobot robot = LineFollowerRobot.getInstance();			
			robot.init( 5.65, 			// wheelDiameter
						11.1, 			// trackWidth
						SensorPort.S1, // lightSensorPort
						SensorPort.S4, // touchSensorPort
						Motor.C,        // leftMotor
						Motor.B			 // rightMotor
			);
			
	        Behavior[] behaviors = {
	        		new MainMenu(), 
	        		new CalibrateSensors(),
	        		new DetectDirections(),
	        		new FuzzyLineFolower(),
	        		new SimpleLineFolower(),
	        		new PidLineFolower(),
	        		//new ODOTests(),
	        		     		
	        };
	        Arbitrator arbitrator = new Arbitrator(behaviors);
	        arbitrator.start();
		} catch(Exception e) {		
			LCD.drawString(e.getMessage(), 0, 0);
		}
	}  
}
