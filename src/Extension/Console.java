package Extension;

import TermioPackage.Termio;

import static java.lang.Thread.sleep;

/**
 * Created by Yuchao on 03-Mar-17.
 */
public class Console extends MaintainableDevice {
    protected Termio UserInput = new Termio();
    protected BaseMonitor monitor;
    boolean hasAlarm = false;
    boolean done;
    protected int i = 0;


    public Console() {
        this.monitor = new BaseMonitor (this);
    }

    public void printMsg () {
        System.out.println (i);
        try {
            sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void consoleLogic(String[] args) {
       // monitor.start();
        try {
            sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println ("Main");

        /*
        if (args.length != 0) {
            monitor = new BaseMonitor (args[0]);
        } else {
            monitor = new BaseMonitor();
        }

        if (monitor.isRegistered()) {
            monitor.start();
            while (true) {
                //TODO: Provide the main menu

                if (args.length != 0) {
                    System.out.println ("Using message manager at:" + args[0]
                            + "\n");
                } else {
                    System.out.println("Using local message manager");
                }
            }
        }
        */
    }

    public static void main (String[] args) {
        (new Console()).consoleLogic(args);
    }
}