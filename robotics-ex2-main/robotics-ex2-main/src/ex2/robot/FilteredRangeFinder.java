package ex2.robot;
import lejos.robotics.RangeFinder;

import java.util.Arrays;

import ex2.DebugNotes;

public class FilteredRangeFinder implements RangeFinder{
	RangeFinder sensor;
	public FilteredRangeFinder(RangeFinder rangeFinder) {		
		sensor = rangeFinder;
	}
	
	@Override
	public float getRange() {
		float[] values = sensor.getRanges();		
		if (values.length==0)
			return 255;
		float range = calculateMedian(removeOutliers(values));
		//DebugNotes.msgWithWait("range:" + range);
		return range;
	}

	@Override
	public float[] getRanges() {
		return sensor.getRanges();
	}

	 
	private static  float[] removeOutliers(float[] values) {
	    
		float[] pings = new float[values.length];
        pings[0]= values[0];
        //DebugNotes.msgWithWait("ro:" +values[0] +"-"+ pings[0]);
        for (int i=1 ; i<values.length ; i++) {
        	pings[i] = (values[i]-pings[i-1]) % pings[0];
        	//DebugNotes.msgWithWait("ro:" +values[i] +"-"+ pings[i]);
        }
        
        Arrays.sort(pings);
        int q1Index = pings.length / 4;
        int q3Index = (pings.length * 3) / 4;
        float q1 = pings[q1Index];
        float q3 = pings[q3Index];
        float iqr = q3 - q1;
        float lowerBound = q1 - 1.5f * iqr;
        float upperBound = q3 + 1.5f * iqr;
        
        
        int count = 0;
        for (int i = 0; i < pings.length; i++) {
        	//DebugNotes.msgWithWait("ro:" +values[i]);
            if (pings[i] >= lowerBound && pings[i] <= upperBound) {
                count++;
            }
        }

        float[] filteredValues = new float[count];
        int index = 0;
        for (int i = 0; i < pings.length; i++) {
            if (pings[i] >= lowerBound && pings[i] <= upperBound) {
                filteredValues[index++] = pings[i];
            }
        }

        return filteredValues;
    }
    
    private static float calculateMedian(float[] values) {
        Arrays.sort(values);
        int middle = values.length / 2;
        if (values.length % 2 == 0) {
            return (values[middle - 1] + values[middle]) / 2.0f;
        } else {
            return values[middle];
        }
    }
}
