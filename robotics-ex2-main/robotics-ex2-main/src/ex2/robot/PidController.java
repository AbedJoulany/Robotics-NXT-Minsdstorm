package ex2.robot;

import ex2.DebugNotes;
import lejos.nxt.LCD;

public class PidController {
	private double requeiredValue;
	private double maxCorrection;
	
	private int ts;
	private double kP = 5;
	private double kI = 0;//0.001;
	private double kD = 1;
	
	private double integral= 0;
	private double deriv = 0;

	public PidController(double requeiredValue, double maxCorrection) {
		this.requeiredValue = requeiredValue;
		this.maxCorrection = maxCorrection;
	}
	
	public double adjust(double lastError, double error, double headingDiff) {	
		double steerCorrection;
		
		double unifiedError = 1.1 * error + 0.4 * headingDiff;
		
		
		if (unifiedError>100) {
			unifiedError = 100;
		}
		/**
		 * err=2
		 * p = 10*2 =0.20
		 * i
		 * d = -5
		 */

		deriv = lastError - error; 
		
		integral *= 0.5;
		integral += error;			
		
		
		steerCorrection =  maxCorrection * (kP * unifiedError + kI * integral + kD * deriv)/100;
		//steerCorrection =  kP * error + kI * integral + kD * deriv;
		
		
		//LCD.drawString("le:"+ lastError + " e: "+ error , 0, 1);
		
		LCD.drawString("" + (ts++)+ "P:" +  Math.round(kP * error) + " I:" + Math.round(kI * integral)  + "D:" + Math.round(kD * deriv) + "           ", 0,5);
		
		return steerCorrection;
		
		
	}
	
	public double getError(double measurement) {
		 return measurement  - requeiredValue  ;
	}

}
