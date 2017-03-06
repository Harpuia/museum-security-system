package Extension;

import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import TermioPackage.Termio;

/**
 * Created by yazid on 04-Mar-17.
 */
//Message ID = 10
public class SecuritySimulator {
    private static MessageManagerInterface messageInterface = null;

    public static void main(String args[]) {
        String MsgMgrIP;
        boolean done = false;

        Termio termio = new Termio();
        String choice;
        int intChoice;

        if (args.length == 0) {
            System.out.println("\n\nAttempting to register on the local machine...");

            try {
                // Here we create an message manager interface object. This assumes
                // that the message manager is on the local machine

                messageInterface = new MessageManagerInterface();
            } catch (Exception e) {
                System.out.println("Error instantiating message manager interface: " + e);

            }

        } else {

            // message manager is not on the local system

            MsgMgrIP = args[0];

            System.out.println("\n\nAttempting to register on the machine:: " + MsgMgrIP);

            try {
                messageInterface = new MessageManagerInterface(MsgMgrIP);
            } catch (Exception e) {
                System.out.println("Error instantiating message manager interface: " + e);

            }

        }

        if (messageInterface != null) {
            System.out.println("Registered with the message manager.");

            try {
                System.out.println("   Participant id: " + messageInterface.GetMyId());
                System.out.println("   Registration Time: " + messageInterface.GetRegistrationTime());

            } catch (Exception e) {
                System.out.println("Error:: " + e);
            }

            System.out.println("\nInitializing Security Simulation::");
            System.out.println("Beginning Simulation... ");
            while (!done) {
                try {
                    System.out.println("Enter a choice: 1=door, 2=window, 3=movement");
                    System.out.println("Select an Option: \n");
                    System.out.println("1: simulate door breach");
                    System.out.println("2: simulate window breach");
                    System.out.println("3: simulate movement detection");
                    System.out.println("X: Stop System\n");
                    System.out.print("\n>>>> ");
                    choice = termio.KeyboardReadString();
                    if (choice.equals("X")) {
                        Halt();
                        System.out.println( "\nSecurity simulator stopped... Exit security monitor window to return to command prompt." );
                        done = true;
                        Halt();
                    } else {
                        intChoice = termio.ToInteger(choice);
                        //1: door, 2:window, 3:movement
                        switch (intChoice) {
                            case 1:
                                messageInterface.SendMessage(new Message(10, "D"));
                                System.out.println("Door message sent at " + System.currentTimeMillis());
                                break;
                            case 2:
                                messageInterface.SendMessage(new Message(10, "W"));
                                System.out.println("Window message sent at " + System.currentTimeMillis());
                                break;
                            case 3:
                                messageInterface.SendMessage(new Message(10, "M"));
                                System.out.println("Movement message sent at " + System.currentTimeMillis());
                                break;
                            case 99:
                                Halt();
                                break;
                            default:
                                System.out.println("Error, invalid choice.");
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Unable to register with the message manager.\n\n");
        }
    }

    public static void Halt() {
        System.out.println("***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***");

        // Here we create the stop message.
        Message msg;
        msg = new Message((int) 99, "XXX");

        // Here we send the message to the message manager.
        try {
            messageInterface.SendMessage(msg);
            try {
                messageInterface.UnRegister();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            System.out.println("Error sending halt message:: " + e);
        }
    }
}
