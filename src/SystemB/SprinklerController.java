package SystemB;

import SystemC.MaintenanceUtils;
import InstrumentationPackage.Indicator;
import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

/**
 * Created by Yuchao on 04/03/2017.
 */
public class SprinklerController {
    public static void main(String args[]) {
        final int SPRINKLER_MSGID = 7;
        final int HALT_MSGID = 99;
        final int DELAY = 500;

        int queueLength;
        String msgMgrIP;
        Message message = null;
        MessageQueue messageQueue = null;
        MessageManagerInterface messageManagerInterface = null;
        boolean sprinklerState = false;

        if (args.length == 0) {
            System.out.println("Attempting to register on the local machine...");
            try {
                messageManagerInterface = new MessageManagerInterface();
            } catch (Exception e) {
                System.out.println("Error instantiating message manager interface: " + e);
            }
        } else {
            msgMgrIP = args[0];
            System.out.println("Attempting to register on the machine:: " + msgMgrIP);
            try {
                messageManagerInterface = new MessageManagerInterface(msgMgrIP);
            } catch (Exception e) {
                System.out.println("Error instantiating message manager interface: " + e);
            }
        }

        if (messageManagerInterface != null) {
            //Sending alive message
            MaintenanceUtils.SendAliveSignal(">> Sprinkler Controller", ">> Initial system sprinkler controller", messageManagerInterface);
            System.out.println("Registered with the message manager.");
            float WinPosX = 0;
            float WinPosY = 0;
            MessageWindow messageWindow = new MessageWindow("Sprinkler Controller Status Console", WinPosX, WinPosY);
            Indicator sprinklerIndicator = new Indicator("Sprinkler OFF", WinPosX, WinPosY + messageWindow.Height());
            messageWindow.WriteMessage("Registered with the message manager.");
            try {
                messageWindow.WriteMessage("Participant id: " + messageManagerInterface.GetMyId());
                messageWindow.WriteMessage("Registration Time: " + messageManagerInterface.GetRegistrationTime());
            } catch (Exception e) {
                System.out.println("Error:: " + e);
            }

            while (true) {
                try {
                    messageQueue = messageManagerInterface.GetMessageQueue();
                } catch (Exception e) {
                    messageWindow.WriteMessage("Error getting message queue::" + e);
                }

                if (messageQueue == null) {
                    System.exit(0);
                }

                queueLength = messageQueue.GetSize();
                for (int i = 0; i < queueLength; i++) {
                    message = messageQueue.GetMessage();
                    if (message.GetMessageId() == SPRINKLER_MSGID) {
                        if (message.GetMessage().equals("S1")) {
                            sprinklerState = true;
                            messageWindow.WriteMessage("Received sprinkler on message");
                            // Confirm message
                        } else if (message.GetMessage().equals("S0")) {
                            sprinklerState = false;
                            messageWindow.WriteMessage("Received sprinkler off message");
                        }
                    } else if (message.GetMessageId() == HALT_MSGID) {
                        try {
                            messageManagerInterface.UnRegister();
                        } catch (Exception e) {
                            messageWindow.WriteMessage("Error unregistering: " + e);
                        }
                        messageWindow.WriteMessage("\n\nSimulation Stopped.\n");
                        sprinklerIndicator.dispose();
                    }
                }

                if (sprinklerState) {
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
