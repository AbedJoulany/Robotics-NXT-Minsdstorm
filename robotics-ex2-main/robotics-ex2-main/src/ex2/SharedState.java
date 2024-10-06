package ex2;

public class SharedState {
	
	public static final int  MA_MAIN = -1;
	
	public static final int  MA_MAP = 0;
	public static final int  MA_NAVIGATE = 1;

	

	
	public static final double RB_KEEP_DISTANCE=10.0; 
	public static final int RB_BASE_SPEED=300; 
	public static final int RB_ACCELERATION=5000;
	
	
    public static volatile int menuSelection = MA_MAIN; 
    public static volatile int menuReturnTo = MA_MAIN; 


    public static synchronized int getMenuSelection() {
        return menuSelection;
    }
    public static synchronized void setMenuSelection(int selection) {
    	DebugNotes.msg("sel: " + selection);
        menuSelection = selection;
    }

   

    
    
}