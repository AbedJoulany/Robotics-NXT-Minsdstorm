package ex1.robot.strategies;

import ex1.DebugNotes;

public class ErrorBasedStrategy extends BaseStrategy{
	
	@Override
	public NextStep adjust(int currentLeftSpeed, int currentRightSpeed, double error) {
		
		double speedCorrection;
		double ngativeMaxCorrection = this.getBaseSpeed() * 0.1;  ;
		double positiveMaxCorrection = this.getBaseSpeed() * 0.5;
		double absError = Math.abs(error);
		
		double periodicDiff = this.getErrorDiff(error);
		if ( Math.abs(periodicDiff) < 30) {
			this.changeBaseSpeed(1.3);
		}
		if (periodicDiff>70) {
			this.resetBaseSpeed();
		}
		
		if (error<0) {
			speedCorrection = Math.ceil(absError/100 * ngativeMaxCorrection);
		}else {
			speedCorrection = Math.ceil(absError/100 * positiveMaxCorrection);		
		}
		
		
		return getNextStep(error, speedCorrection);		
	}

	

}
