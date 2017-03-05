package Extension;

import TermioPackage.Termio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Thread.sleep;

/**
 * Created by Yuchao on 03-Mar-17.
 */
public class SecurityConsoleLauncher extends MaintainableDevice {
    private final String LOOPBACK_IP = "127.0.0.1";
    private final int SLEEP_MILLISECONDS = 200;
    private BaseMonitor monitor;
    private SecurityConsole console;
    private SecurityState state = new SecurityState();
    //private Termio userInput = new Termio();


    private BaseMonitor constructMonitor(String[] args) {
        if (args.length != 0) {
            return new SecurityMonitor(args[0], state);
        } else {
            return new SecurityMonitor(state);
        }
    }

    private SecurityConsole constructConsole(String[] args) {
        if (args.length != 0) {
            return new SecurityConsole(args[0], state);
        } else {
            return new SecurityConsole(LOOPBACK_IP, state);
        }
    }

    static void clearScreen() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            //  Handle any exceptions.
        }
    }

    private void listenToAlarm(boolean hasAlarm) {
        try {
            while (state.getHasAlarm() == hasAlarm) {
                sleep(200);
            }
        } catch (InterruptedException e) {
            System.out.println("The console has been ended...");
        }
    }

    private void listenToAlarm(boolean hasAlarm, int dogFood) {
        int count = dogFood / SLEEP_MILLISECONDS;
        try {
            int i = 0;
            while ((state.getHasAlarm() == hasAlarm) && i < count) {
                sleep(SLEEP_MILLISECONDS);
                i++;
            }
        } catch (InterruptedException e) {
            System.out.println("The console has been ended...");
        }
    }

    private void handleAlarm() {
        /*
        BufferedReader myReader;
        try {
            console.getMyReader().close();
            myReader = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        */

        //clearScreen();
        // TODO: Here we assume the sprinkler is on. We will deal with corner case later.
        System.out.println("***FIRE ALARM RECEIVED - TURNING ON THE SPRINKLER in 10 secs...***");
        System.out.println("Press any key to confirm, or press Z to cancel");
        System.out.print( "\n>>>> " );

        /*
        try {
            String option = myReader.readLine();
            while (true) {
                if (option.equals("1")) {
                    state.setSprinklerOn(true);
                    System.out.println("The sprinkler is successfully turned on.");
                    break;
                } else if (option.equals("2")) {
                    break;
                } else {
                    System.out.println("Wrong input. Please try again.");
                    break;
                }
            }
            clearScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    private void launch (String[] args) {
        monitor = constructMonitor(args);
        console = constructConsole(args);

        if (monitor.isRegistered()) {
            //monitor.run();
            //console.run();
            new Thread(monitor).start();
            console.start();

            while(true) {
                listenToAlarm(false);

                if (state.getHasAlarm()) {
                    //handleAlarm();
                    console.shutdown();
                    //console.interrupt();
                    console = constructConsole(args);
                    console.start();
                }
                listenToAlarm(true, 10000);
            }

        } else {
            System.out.println("Unable start the monitor.");
        }
    }

    public static void main (String[] args) {
        (new SecurityConsoleLauncher()).launch(args);
    }
}