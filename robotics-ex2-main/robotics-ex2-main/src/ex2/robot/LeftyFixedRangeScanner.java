package ex2.robot;
import ex2.DebugNotes;
import lejos.robotics.FixedRangeScanner;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReadings;
import lejos.robotics.navigation.RotateMoveController;

public class LeftyFixedRangeScanner extends FixedRangeScanner{
	private float deviation= 90;
	private float relativeZero = 0;
	private float frontDiff = 0;
	
	public LeftyFixedRangeScanner(RotateMoveController aPilot, RangeFinder rangeFinder) {
		super(aPilot, rangeFinder);
		deviation=90;	
	}
	public void setDeviation(float devi) {
		this.deviation = devi;	
	}
	
	public RangeReadings getRangeValues() {
		super.getRangeValues();
		RangeReadings rr = new RangeReadings(angles.length);
		 
		for (int i = 0; i < angles.length; i++)
	    {
			float range = readings.getRange(fixAngle(angles[i]));
			if (range> -1) {
				rr.setRange(i, angles[i], range + relativeZero);
			}
	    }		 
		return rr;		
	}
	
	public float getNearestObsticle(float angle) {
		
		angle = fixAngle(angle);
        float lowerAngle = 0;
        float upperAngle = 0;
        float lowerRange = 0;
        float upperRange = 0;
        
        for (int i = 0; i < angles.length; i++) {        	        	
            if (angles[i] == angle) {               
                return readings.get(i).getRange()-frontDiff;
            } else if (angles[i] < angle) {
                lowerAngle = angles[i];
                lowerRange = readings.get(i).getRange()-frontDiff ;
            } else if (angles[i] > angle) {
                upperAngle = angles[i];
                upperRange = readings.get(i).getRange()-frontDiff;
                break;
            }
        }

        if (upperAngle == 0) {
            upperAngle = 360;
            upperRange = readings.get(0).getRange()-frontDiff;
        }
       
        float rangeDifference = upperRange - lowerRange;
        float angleDifference = upperAngle - lowerAngle;
        float relativeAngle = angle - lowerAngle;

        return lowerRange + (rangeDifference * (relativeAngle / angleDifference));
		
	}
	
	private float fixAngle(float angle ) {
		angle = (angle - deviation) % 360;		
		if (angle < 0) {
            angle += 360;
        }
		return angle;
	}
}
