package Extension;
import TermioPackage.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Yuchao on 03/03/2017.
 */
public class SecurityConsole implements Runnable {
    private final String LOOPBACK_IP = "127.0.0.1";
    private String msgMgrIP;
    private Termio userInput = new Termio();
    private SecurityState state;
    private BufferedReader myReader =
            new BufferedReader(new InputStreamReader(System.in));
    volatile boolean shutdown = false;


    public void shutdown() {
        shutdown = true;
    }

    public BufferedReader getMyReader() {
        return myReader;
    }

    public SecurityConsole (String msgMgrIP, SecurityState state) {
        this.msgMgrIP = msgMgrIP;
        this.state = state;
    }

    @Override
    public void run() {
        while (true) {
            String option = null;
            if(!state.getHasAlarm()) {
                System.out.println("\n\n\n\n");
                System.out.println("Security System Command Console:");
                if (!msgMgrIP.equals(LOOPBACK_IP)) {
                    System.out.println("Using message manager at:" + msgMgrIP);
                } else {
                    System.out.println("Using local message manager");
                }

                System.out.println("System security state: Not disarmed");
                System.out.println("Select an option:");
                System.out.println("1: Arm the system");
                System.out.println("2: Disarm the system");
                if (state.getSprinklerOn()) {
                    System.out.println("3: Turn off the sprinkler");
                }
                System.out.println("X: Stop system");
                System.out.print("\n>>>> ");
            } else {
                SecurityConsoleLauncher.clearScreen();
                System.out.println("***FIRE ALARM RECEIVED - TURNING ON THE SPRINKLER in 10 secs...***");
                System.out.println("Press any key to confirm, or press Z to cancel");
                System.out.print( "\n>>>> " );
            }


            try {
                // wait until we have data to complete a readLine()
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

            if (!state.getHasAlarm()) {
                if (option.equals("1")) {
                    if (state.getIsArmed()) {
                        System.out.println("System is already armed. Press enter and input again.");
                    } else {
                        state.setIsArmed(true);
                        System.out.println("System is successfully armed. " +
                                "Security intrusions will be reported to the user.");
                        System.out.println("Press enter to return to the menu.");
                    }
                } else if (option.equals("2")) {
                    if (!state.getIsArmed()) {
                        System.out.println("System is already disarmed. Press enter and input again.");
                    } else {
                        state.setIsArmed(false);
                        System.out.println("System is successfully disarmed. " +
                                "Security intrusions will NOT be reported to the user.");
                        System.out.println("Press enter to return to the menu.");
                    }
                } else if (option.equals("3") && state.getSprinklerOn()) {
                    state.setSprinklerOn(false);
                    System.out.println("Sprinkler has successfully been turned off.");
                    System.out.println("Press enter to return to the menu.");
                } else if (option.equals("X")) {
                    // TODO: Halt
                    System.out.println("\nConsole stopped... Exit monitor window to return to command prompt.");
                    break;
                } else {
                    System.out.println("Wrong input. Press enter and try again.");
                }
            }


            else {
                if (option.equals("Z")) {
                    state.setHasAlarm(false);
                    System.out.println("Sprinkler turning on has been cancelled. Fire alarm is stopped.");
                    System.out.println("Press enter to return to the main menu.");
                } else {
                    state.setSprinklerOn(true);
                    state.setHasAlarm(false);
                    System.out.println("Sprinkler has successfully been turned on, and the fire alarm is closed.");
                    System.out.println("Press enter to return to the main menu.");
                }
            }


            try {
                myReader.readLine();
                SecurityConsoleLauncher.clearScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
