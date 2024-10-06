package ex1.robot;

import ex1.DebugNotes;
import lejos.util.Delay;

public class Odometer implements Runnable {   
   

    // Wheel positions
    private double Lx = -robot().getWeelBase() / 2.0;
    private double Ly = 0.0;
    private double Rx = robot().getWeelBase() / 2.0;
    private double Ry = 0.0;
    
    private double lastLeftDistance = 0.0;
    private double lastRightDistance = 0.0;

    private volatile boolean running = true; // Control flag

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
    	
        while (running) {
            
            double leftDistance = robot().getLeftDistance();  
            double rightDistance = robot().getRightDistance();
            
            if (leftDistance> lastLeftDistance && rightDistance>lastRightDistance ) { 
            	updateWheelPosition(leftDistance-lastLeftDistance, rightDistance-lastRightDistance);
            }
            
            lastLeftDistance = leftDistance;
            lastRightDistance = rightDistance;
            
            Delay.msDelay(200);
           
        }       
    }


    private void updateWheelPosition(double l, double r) {
        if (Math.abs(r - l) < 0.001) {
            // Move straight
            double forwardX = -Ly;
            double forwardY = Lx;

            double length = Math.sqrt(forwardX * forwardX + forwardY * forwardY);
            forwardX /= length;
            forwardY /= length;

            Lx += forwardX * l;
            Ly += forwardY * l;
            Rx += forwardX * r;
            Ry += forwardY * r;
        } else {
            // Move in an arc
            double rl = robot().getWeelBase() * l / (r - l);
            double theta = l / rl;

            double Px = Lx + rl * ((Lx - Rx) / robot().getWeelBase());
            double Py = Ly + rl * ((Ly - Ry) / robot().getWeelBase());

            double cosTheta = Math.cos(theta);
            double sinTheta = Math.sin(theta);

            // Translate to origin
            double LxTranslated = Lx - Px;
            double LyTranslated = Ly - Py;
            double RxTranslated = Rx - Px;
            double RyTranslated = Ry - Py;

            // Rotate
            Lx = LxTranslated * cosTheta - LyTranslated * sinTheta + Px;
            Ly = LxTranslated * sinTheta + LyTranslated * cosTheta + Py;
            Rx = RxTranslated * cosTheta - RyTranslated * sinTheta + Px;
            Ry = RxTranslated * sinTheta + RyTranslated * cosTheta + Py;
        }
        
        robot().setPose(l,r, Lx, Ly,  Rx,  Ry);
    }

    private LineFollowerRobot robot() {
		return LineFollowerRobot.getInstance();
	}

}
