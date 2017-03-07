package SystemA;

import TermioPackage.Termio;

/**
 * Created by yazid on 03-Mar-17.
 */
public class SecurityConsole {
    public static void main(String[] args) {
        //Input variables
        Termio termio = new Termio();
        String choice;

        //Security variables
        SecurityMonitor securityMonitor;
        if (args.length > 0) {
            securityMonitor = new SecurityMonitor(args[0]);
        } else {
            securityMonitor = new SecurityMonitor();
        }
        boolean done = false;
        boolean armed = false;

        //Starting the security monitor
        if (securityMonitor.isRegistered()) {
            Thread monitorThread = new Thread(securityMonitor);
            monitorThread.start();
            while (!done) {
                //Displaying input prompt
                System.out.println("Select an Option: \n");
                System.out.print("\n>>>> ");
                if (armed) {
                    System.out.println("1: Disarm security system");
                } else {
                    System.out.println("1: Arm security system");
                }
                System.out.println("X: Stop System\n");
                System.out.print("\n>>>> ");

                //Reading input
                choice = termio.KeyboardReadString();

                //Parsing choices
                if (choice.equals("X")) {
                    securityMonitor.halt();
                    System.out.println( "\nSecurity console Stopped... Exit security monitor mindow to return to command prompt." );
                    done = true;
                    securityMonitor.halt();
                } else {
                    //Converting/checking numeric value
                    int intChoice = termio.ToInteger(choice);
                    switch (intChoice) {
                        //Switch between armed/disarmed
                        case 1:
                            if (armed) {
                                securityMonitor.disarm();
                                armed = false;
                            } else {
                                securityMonitor.arm();
                                armed = true;
                            }
                            break;
                        default:
                            System.out.println("Error, invalid choice.");
                            break;
                    }
                }
            }
        }
    }
}