package ex1.robot.strategies;

import ex1.DebugNotes;
import lejos.nxt.LCD;

public class PidStrategy extends BaseStrategy{
	/*
	private double kP = 1.1;
	private double kI = 0.001;
	private double kD = 0.5;
	*/
	
	private double kP = 0.3;
	private double kI = 0.05;
	private double kD = 0.5;
	
	
	private double integral= 0;
	private double deriv = 0;
	
	@Override
	public NextStep adjust(int currentLeftSpeed, int currentRightSpeed, double error) {
		
		double  maxCorrection = this.getBaseSpeed()*0.8  ;
		/*if (error<0 ) {
			error /=3;
		}*/
		
		double absError = Math.abs(error);
		
		integral *= 0.5; 
		integral += error;
		
		deriv = getErrorDiff(error); 
		
		double speedCorrection =  maxCorrection * (kP * absError + kI * integral + kD * deriv)/100; 
		LCD.drawString(" "+ (kP * absError + kI * integral + kD * deriv)/100 , 0, 0);
		DebugNotes.msg("P:" +  Math.round(kP * absError) + " I:" + Math.round(kI * integral)  + "D:" + Math.round(kD * deriv));
		
		return getNextStep(error, speedCorrection);	
	}

	 
}