package Extension;

import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Yuchao on 04/03/2017.
 */
public class FireDetectorSimulator {
    public static void main (String[] args) {
        final int FIRE_MSGID = 6;
        final int DELAY = 2500;

        String msgMgrIP;
        Message msg = null;
        MessageQueue msgQueue = null;
        MessageManagerInterface msgMgrInterface = null;
        BufferedReader myReader =
                new BufferedReader(new InputStreamReader(System.in));
        String msgText = "FIRE DETECTED!!! ALARM WILL SHOW IN THE CONSOLE\n";

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

        while(true) {
            try {
                System.out.println("Press any key to simulate a fire event:");
                myReader.readLine();
                msg = new Message (FIRE_MSGID, msgText);
                msgMgrInterface.SendMessage(msg);
            } catch (Exception e) {
                System.out.println("Error posting fire alarm::" + e);
                break;
            }
        }
    }
}
