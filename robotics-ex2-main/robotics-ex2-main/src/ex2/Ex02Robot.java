

package ex2;

import ex2.behaviors.*;
import ex2.behaviors.menu_actions.*;
import ex2.robot.AreaMapperRobot;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Ex02Robot {	
	
	public static void main(String[] args) {	
		try {
			AreaMapperRobot robot = AreaMapperRobot.getInstance();			
			robot.init( 5.6, 			// wheelDiameter
						11.0, 			// trackWidth
						SensorPort.S1, // lightSensorPort
						SensorPort.S4, // touchSensorPort
						SensorPort.S2, // soundSensorPort
						SensorPort.S3, // ultrasonicSensorPort
						Motor.C,        // leftMotor
						Motor.B			 // rightMotor
			);
			
	        Behavior[] behaviors = {
	        		new MainMenu(),  
	        		new MapAreaBehaviour(),
	        		new NavigateBehaviour()
	        };
	        Arbitrator arbitrator = new Arbitrator(behaviors);
	        arbitrator.start();
		} catch(Exception e) {		
			LCD.drawString(e.getMessage(), 0, 0);
		}
	}  
}
