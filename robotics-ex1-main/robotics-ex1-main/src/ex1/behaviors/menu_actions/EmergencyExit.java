package ex1.behaviors.menu_actions;

import lejos.robotics.subsumption.Behavior;
import lejos.nxt.Button;
import lejos.nxt.LCD;

public class EmergencyExit implements Behavior {
    private volatile boolean suppressed = false;

    @Override
    public boolean takeControl() {
        return Button.ESCAPE.isDown();  // This behavior takes control when ESCAPE button is pressed
    }

    @Override
    public void action() {
        suppressed = false;

        // Clear the display and ask for confirmation to exit
        LCD.clear();
        LCD.drawString("Confirm Exit", 0, 0);
        LCD.drawString("Press ENTER", 0, 1);
        LCD.drawString("to exit", 0, 2);

        // Wait for confirmation (ENTER button) or cancellation (any other button)
        while (!suppressed) {
            if (Button.ENTER.isDown()) {
                System.exit(0);  // Exit the program
            } else if (Button.readButtons() != Button.ID_ENTER && Button.readButtons() != Button.ID_ESCAPE) {
                suppressed = true; // Any other button cancels the exit
                LCD.clear();
            }
            try {
                Thread.sleep(500); // Sleep to slow down the loop
            } catch (InterruptedException e) {
                // Handle interruption here if necessary
            }
        }
    }

    @Override
    public void suppress() {
        suppressed = true;  // Standard way to suppress this behavior
    }
}
