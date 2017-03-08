package SystemB;

import SystemC.MaintenanceUtils;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * A command line application to simulate a fire event.
 * This simulator will send fire message to the message bus.
 */
public class FireDetectorSimulator {
    public static void main(String[] args) {
        final int FIRE_MSGID = 6;

        String option;
        String msgMgrIP;
        Message msg;
        MessageManagerInterface msgMgrInterface = null;
        BufferedReader myReader =
                new BufferedReader(new InputStreamReader(System.in));

        /** This message will be sent to the fire monitor. */
        String msgText = "FIRE DETECTED!!! ALARM WILL SHOW IN THE CONSOLE\n";

        /** Instantiating and registering on the message bus. */
        System.out.println("***Fire Detector Simulator***");
        if (args.length == 0) {
            System.out.println("Attempting to register on the local machine...");
            try {
                msgMgrInterface = new MessageManagerInterface();
            } catch (Exception e) {
                System.out.println("Error instantiating message manager interface: " + e);
            }
        } else {
            msgMgrIP = args[0];
            System.out.println("Attempting to register on the machine:: " + msgMgrIP);
            try {
                msgMgrInterface = new MessageManagerInterface(msgMgrIP);
            } catch (Exception e) {
                System.out.println("Error instantiating message manager interface: " + e);
            }
        }

        if (msgMgrInterface != null) {
            /** Sending alive message */
            MaintenanceUtils.SendAliveSignal(">> Fire Detector Simulator", ">> Initial system fire detector simulator", msgMgrInterface);
            while (true) {
                try {
                    System.out.println("Enter 1 to simulate a fire event:");
                    option = myReader.readLine();
                    if (option.equals("1")) {
                        /** Sending message to the fire monitor. */
                        msg = new Message(FIRE_MSGID, msgText);
                        msgMgrInterface.SendMessage(msg);
                    } else {
                        System.out.println("Wrong input. Please try again.");
                    }
                } catch (Exception e) {
                    System.out.println("Error posting fire alarm::" + e);
                    break;
                }
            }
        } else {
            System.out.println("Unable to register with the message manager.");
        }
    }
}
