package ex1.robot;

public class TachoToDistanceConverter {
	private double wheelRadius; 
    private int countsPerRevolution; 
    
    public TachoToDistanceConverter(double wheelRadius, int countsPerRevolution) {		
		this.wheelRadius = wheelRadius;
		this.countsPerRevolution = countsPerRevolution;
	}

	public double getDistance(int tachoCount) {
        double wheelCircumference = 2 * Math.PI * wheelRadius;
        return (tachoCount / (double) countsPerRevolution) * wheelCircumference;
    }

}
