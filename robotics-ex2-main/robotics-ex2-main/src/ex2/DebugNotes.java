package ex2;
import lejos.nxt.Button;
import lejos.nxt.LCD;
public class DebugNotes {
	
	private static volatile boolean debug=false;
	public static synchronized void setDegug(boolean en) {
		debug = en;
	}
	public static synchronized void msg(String message) {
		LCD.drawString("                                                    ", 0, 6);
		LCD.drawString(message, 0, 6);
    }
	
	public static synchronized void msgWithWait(String message) {
		LCD.drawString("                                                    ", 0, 6);
		LCD.drawString(message, 0, 6);
		Button.waitForAnyPress();
    }
	
	public static synchronized void clear() {
		LCD.drawString("                                                    ", 0, 6);
		Button.waitForAnyPress();
    }
	
	public static synchronized void log(String message) { 
		if (debug) {
			System.out.println(message);
		}
		//LCD.drawString("                                                    ", 0, 6);
		//LCD.drawString(message, 0, 6);
	}
		
	
}
