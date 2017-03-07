package SystemB;

import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Yuchao on 04/03/2017.
 */
public class FireDetectorSimulator {
    public static void main (String[] args) {
        final int FIRE_MSGID = 6;
        final int HALT_MSGID = 99;
        final int DELAY = 2500;

        int queueLength;
        String msgMgrIP;
        Message msg;
        MessageQueue msgQueue;
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

        if(msgMgrInterface != null) {
            while (true) {
                try {
                    msgQueue = msgMgrInterface.GetMessageQueue();
                    if (msgQueue == null) {
                        System.exit(0);
                    }

                    queueLength = msgQueue.GetSize();
                    for (int i = 0; i < queueLength; i++) {
                        msg = msgQueue.GetMessage();
                        if (msg.GetMessageId() == HALT_MSGID) {
                            msgMgrInterface.UnRegister();
                            System.out.println("\n\nSimulation Stopped.\n");
                        }
                    }
                    Thread.sleep(DELAY);
                    System.out.println("Press any key to simulate a fire event:");
                    myReader.readLine();
                    msg = new Message(FIRE_MSGID, msgText);
                    msgMgrInterface.SendMessage(msg);
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
