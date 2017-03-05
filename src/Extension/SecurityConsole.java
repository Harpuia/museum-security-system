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
                System.out.println("Input command:");
                if (armed) {
                    System.out.println("1 = disarm security system");
                } else {
                    System.out.println("1 = arm security system");
                }

                choice = termio.KeyboardReadString();
                int intChoice = termio.ToInteger(choice);

                if (intChoice > 0) {
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
                            System.out.println("Error, choice invalid.");
                            break;
                    }
                } else {
                    System.out.println("Error, choice invalid.");
                }
            }
        }
    }
}