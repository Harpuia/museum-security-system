package SystemB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The console thread for user to input.
 */
public class FireConsole extends Thread {
    private final String LOOPBACK_IP = "127.0.0.1";
    private String msgMgrIP;

    /** The reference of the shared object */
    private FireState state;

    private BufferedReader myReader =
            new BufferedReader(new InputStreamReader(System.in));

    /** Used in halting */
    boolean shutdown = false;

    /**
     * Called from the outer "main" thread.
     */
    public void shutdown() {
        shutdown = true;
    }

    /**
     * Constructor of the fire console
     * @param msgMgrIP ip address of message manager
     * @param state shared state passed by the launcher
     */
    public FireConsole(String msgMgrIP, FireState state) {
        this.msgMgrIP = msgMgrIP;
        this.state = state;
    }

    @Override
    public void run() {
        while (true) {
            String option = null;

            /** When the fire alarm doesn't arrive, the console shows the main menu. */
            if(!state.getHasAlarm()) {
                System.out.println("\n\n\n\n");
                System.out.println("Fire System Command Console:");
                if (!msgMgrIP.equals(LOOPBACK_IP)) {
                    System.out.println("Using message manager at:" + msgMgrIP);
                } else {
                    System.out.println("Using local message manager");
                }

                /** The user is only given control to turn off the sprinkler.
                  * This ONLY happens when the sprinkler is on.
                  */
                if (state.getSprinklerOn()) {
                    System.out.println("Select an option:");
                    System.out.println("1: Turn off the sprinkler");
                }
                System.out.println("X: Stop system");
                System.out.print("\n>>>> ");
            }

            /** This branch will only be entered in the following scenarios:
             *  When the fire alarm arrives, and the console is destroyed and started again.
             *  The console will show different messages depending on the sprinkler state.
             */
            else if (!state.getSprinklerOn()) {
                FireConsoleLauncher.clearScreen();
                System.out.println("***FIRE ALARM RECEIVED - TURNING ON THE SPRINKLER in 10 secs...***");
                System.out.println("Press 1 to confirm, or press 2 to cancel:");
                System.out.print("\n>>>> ");
            } else {
                FireConsoleLauncher.clearScreen();
                System.out.println("***FIRE ALARM RECEIVED AND THE SPRINKLER IS ALREADY ON...");
                System.out.println("If no directions received in 10 seconds, the sprinkler will remain on");
                System.out.println("Press 1 to continue, or press 2 to cancel:");
                System.out.print("\n>>>> ");
            }


            try {
                /** wait until we have data to complete a readLine() */
                while (!(myReader.ready() || shutdown)) {
                    Thread.sleep(200);
                }
                option = myReader.readLine();
            } catch (InterruptedException e) {
                System.out.println("***THE CONSOLE WAS INTERRUPTED BY SOME SIGNIFICANT EVENT***");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }

            /** This branch will only be entered in the following scenarios:
             *  There is no fire alarm, and the user has input an option.
             *  The system does the corresponding operation based on user's input.
             */
            if (!state.getHasAlarm()) {
                /** Note: The sprinkler can only be altered when it is on. */
                if (option.equals("1") && state.getSprinklerOn()) {
                    state.setSprinklerOn(false);
                    System.out.println("Sprinkler has successfully been turned off.");
                    System.out.println("Press enter to return to the menu.");
                }

                /** Sending halt */
                else if (option.equalsIgnoreCase("X")) {
                    state.setIsStop(true);
                    System.out.println("\nConsole stopped... Exit monitor window to return to command prompt.");
                    break;
                } else {
                    System.out.println("Wrong input. Press enter and try again.");
                }
            }

            /** This branch will only be entered in the following scenarios:
             *  The fire alarm arrived
             *  The console restarted
             *  The user inputs his/her options
             */
            else {
                if (option.equals("2")) {
                    /** Note: If the user CANCELS the alarm manually,
                     *  the alarm will be turned off.
                     */
                    state.setHasAlarm(false);
                    state.setSprinklerOn(false);

                    System.out.println("Sprinkler turning on has been cancelled. Fire alarm is stopped.");
                    System.out.println("Press enter to return to the main menu.");
                } else if (option.equals("1")){
                    /** Note: When the sprinkler is on, the alarm is automatically turned off*/
                    state.setSprinklerOn(true);
                    state.setHasAlarm(false);

                    System.out.println("Sprinkler has successfully been turned on, and the fire alarm is closed.");
                    System.out.println("Press enter to return to the main menu.");
                } else {
                    System.out.println("Input format wrong. Please try again.");
                }
            }


            try {
                /** We add an enter between the switching of two menus. */
                myReader.readLine();
                FireConsoleLauncher.clearScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
