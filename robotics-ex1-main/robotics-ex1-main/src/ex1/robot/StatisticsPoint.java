package ex1.robot;

public class StatisticsPoint {
	private static long timeIdx=0;
	private double timePoint;
	private double Ld ; //left distance 
	private double Rd ; //right distance
    private double Lx ; //left x
    private double Ly ; //left y
    private double Rx ; //right x
    private double Ry ; //right y
    private int leftSpeed;
    private int rightSpeed;
    private double lightValue;
    
  
	public StatisticsPoint(double ts, double ld,double rd, double lx, double ly, double rx, double ry) {
		super();
		timePoint = ts;
		Ld = ld;
		Rd = rd;
		Lx = lx;
		Ly = ly;
		Rx = rx;
		Ry = ry;
	}
	
	 public String toString() {
        return timePoint + ","+ Ld + "," + Rd +"," + Lx + "," + Ly + "," + Rx + "," + Ry + ","+ leftSpeed + "," + rightSpeed + "," + lightValue;
    }
	 
	 
	
	public void setLeftSpeed(int leftSpeed) {
		this.leftSpeed = leftSpeed;
	}
	
	public void setRightSpeed(int rightSpeed) {
		this.rightSpeed = rightSpeed;
	}
	
	public void setLightValue(double lightValue) {
		this.lightValue = lightValue;
	}
	
    
}
