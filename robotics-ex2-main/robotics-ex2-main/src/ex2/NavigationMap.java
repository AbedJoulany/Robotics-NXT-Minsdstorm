package ex2;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.navigation.Pose;
import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;

import java.io.IOException;

public class NavigationMap {


	private String fileName;
	Rectangle bounds;
	LineMap map;
	Waypoint[] wayPoints = new Waypoint[3];
	
	public final static int FIRST_LINE = 0;
	public final static int SECOND_LINE = 1;
	public final static int CENTER_POINT = 2;
	public NavigationMap() {
		this.fileName = "map.bin";
	}
	
	public NavigationMap(String fileName) {
		this.fileName = fileName;
	}
	
	public void init() {
		DataInputStream dis = null;
		 try {
			File f = new File(fileName);
			dis = new DataInputStream( new  FileInputStream(f));
			int numPoints = dis.readInt();
			//DebugNotes.msgWithWait("numPoints:" + numPoints);
			Point[] pointsArray = new Point[numPoints];
			for (int i = 0; i < numPoints; i++) {
				pointsArray[i] = new Point(dis.readFloat(), dis.readFloat() );				
            }
			Point start = new Point(dis.readFloat(), dis.readFloat() );			
			Point end = new Point(dis.readFloat(), dis.readFloat() );			
			Point center = new Point(dis.readFloat(), dis.readFloat() );
			dis.close(); 
			
	        buildMap(numPoints, pointsArray);	        
	        setWayPoints(start, end, center);
	        
		 } catch (IOException  e) {
            System.err.println("Error: " + e.getMessage());
        } 
	}
	
	private void buildMap(int numPoints, Point[] pointsArray) {		
			
		Line[] lines = new Line[numPoints];
		
		bounds = new Rectangle(0,0,0,0);
		for (int i = 0; i < numPoints; i++) {
		    Point point1 = pointsArray[i];
		    Point point2 = pointsArray[(i+1)% numPoints];
		    lines[i] = new Line(
		        (float)point1.getX(), (float)(point1.getY()+0.001),
		        (float)point2.getX(),  (float)(point2.getY()-0.001)
		    );
		    updateBounds(point1, bounds);
		    updateBounds(point2, bounds);
		}

		map = new LineMap(lines, bounds);
		//testParticles(bounds, 10);		
	}
	
	private void testParticles(Rectangle bounds, int border) {

		DebugNotes.msgWithWait("bxy:" +bounds.x+" " + bounds.y);
		DebugNotes.msgWithWait("bwh:" +bounds.width+" " + bounds.height);
		
		Rectangle innerRect = new Rectangle(bounds.x + border, bounds.y + border,
				bounds.width - border * 2, bounds.height - border * 2);
		
		DebugNotes.msgWithWait("lxy:" +innerRect.x +" " + innerRect.y);
		DebugNotes.msgWithWait("wh:" +innerRect.width +" " +innerRect.height);
		for (int i=0; i<5;i++) {
            
		   float x = innerRect.x + (((float) Math.random()) * innerRect.width);
		   float y = innerRect.y + (((float) Math.random()) * innerRect.height);
		   DebugNotes.msgWithWait("xy:" + Math.round(x)  + "," +  Math.round(y));
		   DebugNotes.msgWithWait("mIn:" +map.inside(new Point(x, y)));
		 }

	}
	public void setWayPoints(Point start, Point end, Point center) {			
        wayPoints[FIRST_LINE] = new Waypoint(start.getX()+10,start.getY(), 179);                
        wayPoints[SECOND_LINE] = new Waypoint(end.getX(),end.getY(), 181);        
        wayPoints[CENTER_POINT] = new Waypoint(center.getX(),center.getY(),90);
        
        // TODO:: remove !!!!!
		/*
		wayPoints[FIRST_LINE] = new Waypoint(-38,15, 179);    
        wayPoints[SECOND_LINE] = new Waypoint(-60,15, 181);
        wayPoints[CENTER_POINT] = new Waypoint(-80,48,90);
        */
		/*
		wayPoints[FIRST_LINE] = new Waypoint(-30,20, 179);    
        wayPoints[SECOND_LINE] = new Waypoint(-60,20, 181);
        wayPoints[CENTER_POINT] = new Waypoint(-95,119,90);
        */
        
	}
	
	public Waypoint getWayPoint(int index) {
		if (index>=FIRST_LINE && index<=CENTER_POINT)
			return wayPoints[index];
		return null;
	}
	
    
    private static void updateBounds(Point point, Rectangle bounds) {
    	double x = point.getX();
        double y = point.getY();
        
        if (x < bounds.getX()) {
            double newWidth = bounds.getWidth() + Math.abs(bounds.getX() - x);
            bounds.setRect(x, bounds.getY(), newWidth, bounds.getHeight());
        }
                
        if (x > bounds.getX() + bounds.getWidth()) {
            double newWidth = Math.abs(x - bounds.getX());
            bounds.setRect(bounds.getX(), bounds.getY(), newWidth, bounds.getHeight());
        }
        
        if (y < bounds.getY()) {
            double newHeight = bounds.getHeight() + Math.abs(bounds.getY() - y);
            bounds.setRect(bounds.getX(), y, bounds.getWidth(), newHeight);
        }
        
        if (y  > bounds.getY() + bounds.getHeight()) {
            double newHeight = Math.abs(y - bounds.getY());
            bounds.setRect(bounds.getX(), bounds.getY(), bounds.getWidth(), newHeight);
        }

        //DebugNotes.msgWithWait("pxy:" +point.x+" " + point.y);
        //DebugNotes.msgWithWait("bxy:" +bounds.x+" " + bounds.y);
        //DebugNotes.msgWithWait("bwh:" +bounds.width+" " + bounds.height);
     }

    
    public void displayMap() {
    	LCD.clear();
    	int row = 1;
    	LCD.drawString("center ("+ Math.round(this.wayPoints[CENTER_POINT].getX()) + "," + Math.round(this.wayPoints[CENTER_POINT].getY()) + ")", 0,0);
    	/*for (Line line : map.getLines()) {
    		LCD.drawString("("+ Math.round(line.x1) + "," + Math.round(line.y1) + ") - (" +  Math.round(line.x2) + "," + Math.round(line.y2) + ")"   , 0, row++);
        }*/
    	LCD.drawString("l2 ("+ Math.round(this.wayPoints[SECOND_LINE].getX()) + "," + Math.round(this.wayPoints[SECOND_LINE].getY()) + ")", 0,1);

    	Button.waitForAnyPress(); 
        LCD.clear();
    }

	public LineMap getLineMap() {
		return map;
	}
	
	public Pose getInitialPose() {
		return new Pose((float)wayPoints[SECOND_LINE].getX(), (float)wayPoints[SECOND_LINE].getY(), 0);
	}
	
	
	public Waypoint getFirstLine() {
		return wayPoints[FIRST_LINE];
	}
	
	public Waypoint getTargeWaypoint() {
		return wayPoints[CENTER_POINT];
	}
    
    


}

