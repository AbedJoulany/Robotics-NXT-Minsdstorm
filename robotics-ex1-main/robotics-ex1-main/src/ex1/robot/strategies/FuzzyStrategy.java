package ex1.robot.strategies;

import ex1.DebugNotes;

public class FuzzyStrategy  extends BaseStrategy{
	
	public NextStep adjust(int currentLeftSpeed, int currentRightSpeed, double error) {
		double  maxCorrection = this.getBaseSpeed() *0.5  ;
		double absError = Math.abs(error);
		double speedCorrection = 0.0;
		
		if (absError < 30) {
			speedCorrection  = 0;
		} else if (error < 60) { 
			speedCorrection = error < 0 ? 0.005f * maxCorrection : 0.1f * maxCorrection;
		} else if (absError < 80) { // negative values are on the line  - minor change is required
			speedCorrection = error < 0 ? 0.05f * maxCorrection : 0.15f * maxCorrection;//0.28f 

		} else  {
			speedCorrection = error < 0 ? 0.1f * maxCorrection :  maxCorrection;//0.63f *

		}
		
	
		return getNextStep(error, speedCorrection);		
	}

	/*
	@Override
	public NextStep adjust(int currentLeftSpeed, int currentRightSpeed, double error) {
		double  maxCorrection = this.getBaseSpeed() *0.6  ;
		double absError = Math.abs(error);
		double speedCorrection = 0.0;
		
		if (absError < 10) {
			speedCorrection  = 0;
		} else if (absError < 30) { 
			speedCorrection  = 0.05 * maxCorrection;
		} else if (error < 50) { // negative values are on the line  - minor change is required
			speedCorrection  = 0.1 * maxCorrection;		
		}else if (error < 90) {
			speedCorrection  =  0.2 * maxCorrection;
		
		} else  {
			speedCorrection  =  maxCorrection;
		}
		
	
		return getNextStep(error, speedCorrection);		
	}
	*/

	

}
