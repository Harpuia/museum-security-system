package SystemA;

import SystemC.MaintenanceUtils;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import TermioPackage.Termio;

/**
 * Created by yazid on 04-Mar-17.
 */
public class SecuritySimulator {
    /**
     * Runs the Security Simulator
     *
     * @param args IP address of the Message Manager
     */
    public static void main(String args[]) {
        //Message variables
        MessageManagerInterface messageInterface = null;
        String MsgMgrIP;

        //Loop and input variables
        boolean done = false;
        Termio termio = new Termio();
        String choice;
        int intChoice;

        //Connecting to the Message Manager
        if (args.length == 0) {
            System.out.println("\n\nAttempting to register on the local machine...");
            try {
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
            //Sending alive message
            MaintenanceUtils.SendAliveSignal(">> Security Simulator", ">> System A security simulator", messageInterface);
            System.out.println("Registered with the message manager.");
            try {
                System.out.println("   Participant id: " + messageInterface.GetMyId());
                System.out.println("   Registration Time: " + messageInterface.GetRegistrationTime());
            } catch (Exception e) {
                System.out.println("Error:: " + e);
            }

            //Looping
            while (!done) {
                try {
                    //Displaying the prompt
                    System.out.println("Enter a choice: 1=door, 2=window, 3=movement");
                    System.out.println("Select an Option: \n");
                    System.out.println("1: simulate door breach");
                    System.out.println("2: simulate window breach");
                    System.out.println("3: simulate movement detection");
                    System.out.print("\n>>>> ");

                    //Reading user choices
                    choice = termio.KeyboardReadString();
                    intChoice = termio.ToInteger(choice);

                    //1: door, 2:window, 3:movement
                    switch (intChoice) {
                        case 1:
                            //Door breach
                            messageInterface.SendMessage(new Message(10, "D"));
                            System.out.println("Door message sent at " + System.currentTimeMillis());
                            break;
                        case 2:
                            //Window breach
                            messageInterface.SendMessage(new Message(10, "W"));
                            System.out.println("Window message sent at " + System.currentTimeMillis());
                            break;
                        case 3:
                            //Movement detected
                            messageInterface.SendMessage(new Message(10, "M"));
                            System.out.println("Movement message sent at " + System.currentTimeMillis());
                            break;
                        default:
                            System.out.println("Error, invalid choice.");
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Unable to register with the message manager.\n\n");
        }
    }
}
