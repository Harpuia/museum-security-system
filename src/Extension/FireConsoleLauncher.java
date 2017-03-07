package Extension;

import static java.lang.Thread.sleep;

/**
 * Created by Yuchao on 03-Mar-17.
 */
public class FireConsoleLauncher extends MaintainableDevice {
    private final String LOOPBACK_IP = "127.0.0.1";
    private final int SLEEP_MILLISECONDS = 200;
    private int dogFood;
    private BaseMonitor monitor;
    private FireConsole console;
    private FireState state = new FireState();

    //private Termio userInput = new Termio();


    private BaseMonitor constructMonitor(String[] args) {
        if (args.length != 0) {
            return new FireMonitor(args[0], state);
        } else {
            return new FireMonitor(state);
        }
    }

    private FireConsole constructConsole(String[] args) {
        if (args.length != 0) {
            return new FireConsole(args[0], state);
        } else {
            return new FireConsole(LOOPBACK_IP, state);
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

    private void listenToAlarmInTenSec(boolean hasAlarm) {
        try {
            while ((state.getHasAlarm() == hasAlarm) && dogFood > 0) {
                sleep(SLEEP_MILLISECONDS);
                dogFood -= SLEEP_MILLISECONDS;
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
        dogFood = 10000;

        if (monitor.isRegistered()) {
            new Thread(monitor).start();
            console.start();

            while(true) {
                listenToAlarm(false);

                if (state.getHasAlarm()) {
                    console.shutdown();
                    console = constructConsole(args);
                    console.start();
                }
                listenToAlarmInTenSec(true);
                if (dogFood <= 0) {
                    state.setSprinklerOn(true);
                    state.setHasAlarm(false);
                    System.out.println("Sprinkler has successfully been turned on, and the fire alarm is closed.");
                    System.out.println("Press enter to return to the main menu.");
                    console.shutdown();
                    console = constructConsole(args);
                    console.start();
                }
            }

        } else {
            System.out.println("Unable start the monitor.");
        }
    }

    public static void main (String[] args) {
        (new FireConsoleLauncher()).launch(args);
    }
}