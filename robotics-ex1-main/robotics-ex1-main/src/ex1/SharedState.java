package ex1;

public class SharedState {
	
	public static final int  MA_MAIN = -1;
	public static final int  MA_CALIBRATE = 0;	
	public static final int  MA_FUZZY_LINE_FOLLOWER = 1;
	public static final int  MA_SIMPLE_LINE_FOLLOWER = 2;
	public static final int  MA_PID_LINE_FOLLOWER = 3;
	public static final int  MA_DETECT_DIRECTIONS = 4;	
	public static final int  MA_ODOTEST = 5;	
	
	public static final int MD_UNKNOWN=0;
	public static final int MD_CLOCKWISE=1;
	public static final int MD_COUNTERCLOCKWISE=-1;
	
	public static final int RB_BASE_SPEED=500;//500;
	public static final int RB_MAX_BASE_SPEED=600;
	public static final int RB_MIN_BASE_SPEED=200;
	public static final int RB_MAX_SPEED=1200;
	public static final int RB_ACCELERATION=3000;//500
	
	
    public static volatile int menuSelection = MA_MAIN; 
    public static volatile int movingDirection = MD_UNKNOWN; 
    
    public static volatile int menuReturnTo = MA_MAIN; 


    public static synchronized int getMenuSelection() {
        return menuSelection;
    }
    public static synchronized void setMenuSelection(int selection) {
    	DebugNotes.msg("sel: " + selection);
        menuSelection = selection;
    }

    public static synchronized void setMovingDirection(int direction) {
    	movingDirection = direction;
    }
    
    
    public static synchronized int getMovingDirection() {
        return movingDirection;
    }

    
    
}