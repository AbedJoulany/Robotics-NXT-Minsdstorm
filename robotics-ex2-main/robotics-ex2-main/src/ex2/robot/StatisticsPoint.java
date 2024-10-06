package ex2.robot;
import ex2.robot.IRobotControl.State;

public class StatisticsPoint {	
	private double timePoint;
	private State state ; 
	private float x ; 
	private float y ;
	private double course ;
	private float heading  ;
    private double lightValue;
    private double rangeValue;
    
  
	public StatisticsPoint(double timePoint, State state, float x, float y,double course, float heading,double lightValue, double rangeValue) {
		this.timePoint = timePoint;
		this.state = state;
		this.x = x;
		this.y = y;
		this.course = course;
		this.heading = heading;
		this.lightValue = lightValue;
		this.rangeValue = rangeValue;
	}

	
	
	 public String toString() {
        return timePoint +","+ state+ ","+ x + "," + y + "," + course +"," + heading+ "," + lightValue+ "," + rangeValue;
    }
	 
	 
	
    
}
