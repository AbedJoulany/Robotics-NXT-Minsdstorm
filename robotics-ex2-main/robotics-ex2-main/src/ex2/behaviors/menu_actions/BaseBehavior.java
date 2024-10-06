package ex2.behaviors.menu_actions;

import ex2.SharedState;
import ex2.robot.AreaMapperRobot;
import lejos.nxt.LCD;
import lejos.robotics.subsumption.Behavior;

abstract public class BaseBehavior implements Behavior {
	
	private int currentSelection;
	private boolean _active;
	
	public BaseBehavior(int menuItem) {
		this.currentSelection = menuItem;
	}
	
	protected boolean isMenuItemSelected() {
		this.setActive(SharedState.getMenuSelection()==this.currentSelection);
		return this.isActive();
	}
	
	
	protected AreaMapperRobot robot() {		
		return AreaMapperRobot.getInstance();
	}
	
	protected void returnToMainMenu() {
		SharedState.setMenuSelection(SharedState.MA_MAIN);  
	}
	
	protected void menuReturToLastAction() {
		SharedState.setMenuSelection(SharedState.menuReturnTo);  
	}
	
	
	
	protected boolean isActive() {
		return _active;
	}

	protected void setActive(boolean _active) {
		this._active = _active;
	}

	

	public boolean takeControl() {		
		return this.isMenuItemSelected(); 
    }
	
	
	public void suppress() {
    	_active = false;
    }
	

}
