package ex2.behaviors.menu_actions;

import ex2.SharedState;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.util.TextMenu;


public class MainMenu extends BaseBehavior{
	

    public  MainMenu() {
		super(SharedState.MA_MAIN);		
	}

    
    public void action() {
        String[] items = { "Map", "Navigate"};
        TextMenu menu = new TextMenu(items, 1, "Main Menu");
       
        while (!Button.ESCAPE.isDown()) {
            LCD.clear();
            int selection = menu.select();
            if (Button.ESCAPE.isDown()) {
                System.exit(0);
            } else {
            	SharedState.setMenuSelection(selection);
            	break;
            }              
        }
    }

    

}
