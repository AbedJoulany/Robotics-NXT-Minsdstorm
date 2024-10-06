package ex1.behaviors.menu_actions;




public class BumpStop extends BaseBehavior{
	private boolean suppressed = false;
	@Override
    public boolean takeControl() {
		return robot().getTouchSensor().isPressed(); 
    }
   
	public void action() {
        suppressed = false;

        robot().stopAll();
    	
		while (!suppressed && robot().getTouchSensor().isPressed()) {
		    Thread.yield();  // Continue yielding till the touch is not suppressed or released
		}

	}
	   
	 public void suppress() {
        suppressed = true;  
    }
}
