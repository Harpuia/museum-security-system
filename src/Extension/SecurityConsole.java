package Extension;

import TermioPackage.Termio;

/**
 * Created by yazid on 03-Mar-17.
 */
public class SecurityConsole {
    public static void main(String[] args) {
        Termio termio = new Termio();
        String choice;

        SecurityMonitor securityMonitor;
        boolean done = false;
        boolean armed = false;

        securityMonitor = new SecurityMonitor();
        if (securityMonitor.isRegistered()) {
            Thread monitorThread = new Thread(securityMonitor);
            monitorThread.start();
            while (!done) {
                System.out.println("Select an Option: \n");
                System.out.print("\n>>>> ");
                if (armed) {
                    System.out.println("1: Disarm security system");
                } else {
                    System.out.println("1: Arm security system");
                }
                System.out.println("X: Stop System\n");
                System.out.print("\n>>>> ");

                choice = termio.KeyboardReadString();
                if (choice.equals("X")) {
                    securityMonitor.Halt();
                    System.out.println( "\nSecurity console Stopped... Exit security monitor mindow to return to command prompt." );
                    done = true;
                    securityMonitor.Halt();
                } else {
                    int intChoice = termio.ToInteger(choice);
                    switch (intChoice) {
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