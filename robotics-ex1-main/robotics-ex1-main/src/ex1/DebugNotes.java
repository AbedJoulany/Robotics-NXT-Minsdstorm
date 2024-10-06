package ex1;
import lejos.nxt.Button;
import lejos.nxt.LCD;
public class DebugNotes {
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
	
}
