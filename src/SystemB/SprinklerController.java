package SystemB;

import Extension.MaintenanceUtils;
import InstrumentationPackage.Indicator;
import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

/**
 * Created by Yuchao on 04/03/2017.
 */
public class SprinklerController {
    public static void main (String args[]) {
        final int SPRINKLER_MSGID = 7;
        final int HALT_MSGID = 99;
        final int DELAY = 2500;

        int queueLength;
        String msgMgrIP;
        Message msg = null;
        MessageQueue msgQueue = null;
        MessageManagerInterface msgMgrInterface = null;
        boolean sprinklerState = false;

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
            //Sending alive message
            MaintenanceUtils.SendAliveSignal("Sprinkler Controller", "The fire sprinkler controller.", msgMgrInterface);
            System.out.println("Registered with the message manager.");
            float WinPosX = 0.0f;
            float WinPosY = 0.9f;
            MessageWindow msgWin = new MessageWindow("Sprinkler Controller Status Console", WinPosX, WinPosY);
            Indicator sprinklerIndicator = new Indicator("Sprinkler OFF", msgWin.GetX(), msgWin.GetY() + msgWin.Height());
            msgWin.WriteMessage("Registered with the message manager.");
            try {
                msgWin.WriteMessage("Participant id: " + msgMgrInterface.GetMyId());
                msgWin.WriteMessage("Registration Time: " + msgMgrInterface.GetRegistrationTime());
            } catch (Exception e) {
                System.out.println("Error:: " + e);
            }

            while(true) {
                try {
                    msgQueue = msgMgrInterface.GetMessageQueue();
                } catch (Exception e) {
                    msgWin.WriteMessage("Error getting message queue::" + e);
                }

                if(msgQueue == null) {
                    System.exit(0);
                }

                queueLength = msgQueue.GetSize();
                for (int i = 0; i < queueLength; i++) {
                    msg = msgQueue.GetMessage();
                    if (msg.GetMessageId() == SPRINKLER_MSGID) {
                        if (msg.GetMessage().equalsIgnoreCase("S1")) {
                            sprinklerState = true;
                            msgWin.WriteMessage("Received sprinkler on message");
                            // Conform message
                        } else if (msg.GetMessage().equalsIgnoreCase("S0")) {
                            sprinklerState = false;
                            msgWin.WriteMessage("Received sprinkler off message");
                        }
                    } else if (msg.GetMessageId() == HALT_MSGID) {
                        try {
                            msgMgrInterface.UnRegister();
                        } catch (Exception e) {
                            msgWin.WriteMessage("Error unregistering: " + e);
                        }

                        msgWin.WriteMessage("\n\nSimulation Stopped.\n");
                        sprinklerIndicator.dispose();
                    }
                }

                if(sprinklerState) {
                    sprinklerIndicator.SetLampColorAndMessage("SPRINKLER ON", 1);
                } else {
                    sprinklerIndicator.SetLampColorAndMessage("SPRINKLER OFF", 0);
                }

                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    System.out.println("Sleep error:: " + e);
                }
            }
        } else {
            System.out.println("Unable to register with the message manager.");
        }
    }
}
