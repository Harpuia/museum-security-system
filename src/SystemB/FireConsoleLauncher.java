package SystemB;

import java.util.Date;

import static java.lang.Thread.sleep;

/**
 * This class is a wrapper process for both console thread and monitor thread.
 */
public class FireConsoleLauncher {
    private final String LOOPBACK_IP = "127.0.0.1";

    /**
     * This constant is used when we are listening the fire alarm.
     */
    private final int SLEEP_MILLISECONDS = 500;

    /**
     * A remaining time used to compare to the timeout.
     */
    private int countDown;

    /**
     * Both monitor and console threads are instantiated in the launcher.
     */
    private FireMonitor monitor;
    private FireConsole console;

    /**
     * The instance of the thread-safe critical data.
     * This instance is passed into every threads that they can all access.
     */
    private FireState state = new FireState();

    /**
     * A wrapper method to construct a monitor thread
     *
     * @param args The command line arguments
     * @return An initiated FireMonitor object
     */
    private FireMonitor constructMonitor(String[] args) {
        if (args.length != 0) {
            return new FireMonitor(args[0], state);
        } else {
            return new FireMonitor(state);
        }
    }

    /**
     * A wrapper method to construct a console thread
     *
     * @param args The command line arguments
     * @return An initiated FireConsole object
     */
    private FireConsole constructConsole(String[] args) {
        if (args.length != 0) {
            return new FireConsole(args[0], state);
        } else {
            return new FireConsole(LOOPBACK_IP, state);
        }
    }

    /**
     * A static method to clear the console.
     */
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

    /**
     * Listen to the fire alarm to become true
     * This method is implemented in a busy waiting fashion.
     * We check if the alarm arrives every 200 milliseconds.
     */
    private void listenToAlarm() {
        try {
            while (!state.getHasAlarm()) {
                sleep(SLEEP_MILLISECONDS);
            }
        } catch (InterruptedException e) {
            System.out.println("The console has been ended...");
        }
    }

    /**
     * Another method listening to the fire alarm being turned off in ten seconds.
     *
     * @param lastFireDetected Last time the system detects the fire
     */
    private void listenToAlarmInTenSec(long lastFireDetected) {
        try {
            /**
             * Specifically, this method will end when the fire alarm being turned off,
             * or the user doesn't give any response in 10 seconds.
             */
            while ((state.getHasAlarm()) && (System.currentTimeMillis() - lastFireDetected) <= 10000) {
                sleep(SLEEP_MILLISECONDS);
            }
        } catch (InterruptedException e) {
            System.out.println("The console has been ended...");
        }
    }

    /**
     * This method checks if the halt message arrives from the console.
     * If the system receives the halt message, it closes both the console
     * thread and the monitor thread. Finally the whole process will be closed.
     */
    private void checkHalt() {
        if (state.getIsStop()) {
            console.shutdown();
            monitor.shutdown();
            System.exit(0);
        }
    }

    /**
     * The whole workflow of the "main" thread.
     * The system is constantly listening the state of fire alarm.
     *
     * @param args The command line arguments.
     */
    private void launch(String[] args) {
        monitor = constructMonitor(args);
        console = constructConsole(args);

        /** The user is given 10secs time to decide when fire alarm arrives.*/
        countDown = 10000;

        if (monitor.isRegistered()) {
            /**
             * Note the monitor implements Runnable,
             * and the console extends Thread.
             */
            new Thread(monitor).start();
            console.start();

            while (true) {
                /** The system is checking if halt signal arrives between listening operations. */
                checkHalt();
                listenToAlarm();
                long lastFireDetected = System.currentTimeMillis();
                checkHalt();

                /**
                 * Every time when the system receives the fire alarm,
                 * it shutdowns the console thread, and restarts a new console
                 * to interrupt the blocking of readLine().
                 */
                if (state.getHasAlarm()) {
                    console.shutdown();
                    console = constructConsole(args);
                    console.start();
                }

                /** We listen user's operation for 10 seconds.*/
                listenToAlarmInTenSec(lastFireDetected);

                /** If the user doesn't respond in 10 seconds, the sprinkler is turned on.*/
                if (state.getHasAlarm()) {
                    /** Note: when the sprinkler is on, the system automatically turns off the alarm.*/
                    state.setSprinklerOn(true);
                    monitor.sendCommandToSprinklerController();
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

    public static void main(String[] args) {
        (new FireConsoleLauncher()).launch(args);
    }
}