package ex1.behaviors.robot_actions;

import lejos.robotics.subsumption.Behavior;
import ex1.robot.LineFollowerRobot;
import lejos.nxt.Button;
import lejos.nxt.LCD;

public class EmergencyExit implements Behavior {
    

    @Override
    public boolean takeControl() {
        return Button.ESCAPE.isDown();  // This behavior takes control when ESCAPE button is pressed
    }

    @Override
    public void action() {
        LCD.clear();
        LCD.drawString("Emergency Exit ! ", 0, 0);
        LineFollowerRobot.getInstance().stopMoving();
        System.exit(0);        
    }

    public void suppress() {    
    }
}
