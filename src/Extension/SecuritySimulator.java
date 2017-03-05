package Extension;

import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;

/**
 * Created by yazid on 04-Mar-17.
 */
//Message ID = 10
public class SecuritySimulator {
    public static void main(String args[]) {
        String MsgMgrIP;
        boolean done = false;

        MessageManagerInterface messageInterface = null;

        if (args.length == 0) {
            System.out.println("\n\nAttempting to register on the local machine...");

            try {
                // Here we create an message manager interface object. This assumes
                // that the message manager is on the local machine

                messageInterface = new MessageManagerInterface();
            } catch (Exception e) {
                System.out.println("Error instantiating message manager interface: " + e);

            } // catch

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
                    Thread.sleep(15000);
                    messageInterface.SendMessage(new Message(10, "M"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Unable to register with the message manager.\n\n");
        }
    }
}
