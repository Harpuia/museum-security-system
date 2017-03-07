package SystemB;

import Extension.MaintenanceUtils;
import InstrumentationPackage.Indicator;
import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

import java.util.HashMap;

import static java.lang.Thread.sleep;

/**
 * Created by yazid on 03-Mar-17.
 */
public class FireMonitor implements Runnable {
    private final int FIRE_MSGID = 6;
    private final int SPRINKLER_MSGID = 7;
    private final int HALT_MSGID = 99;
    private final int delay = 500;
    private MessageManagerInterface messageManagerInterface;
    private String msgMgrIP;
    private boolean registered = true;
    private MessageWindow messageWindow;
    private HashMap<Integer, Message> dataFromSensor = new HashMap<>();
    private FireState state;
    private boolean shutdown = false;
    private Indicator fireIndicator;

    public void shutdown() {
        messageWindow.WriteMessage("***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***");
        Message msg = new Message(HALT_MSGID, "XXX");
        try {
            messageManagerInterface.SendMessage(msg);
        } catch (Exception e) {
            System.out.println("Error sending halt message:: " + e);
        }
        this.shutdown = true;
    }

    public FireMonitor(FireState state) {
        try {
            messageManagerInterface = new MessageManagerInterface();
        } catch (Exception e) {
            System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
            registered = false;
        }

        this.state = state;
        dataFromSensor.put(HALT_MSGID, null);
        dataFromSensor.put(FIRE_MSGID, null);
    }

    public FireMonitor(String msgIpAddress, FireState state) {
        msgMgrIP = msgIpAddress;
        try {
            messageManagerInterface = new MessageManagerInterface(msgMgrIP);
        } catch (Exception e) {
            System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
            registered = false;
        }

        this.state = state;
        dataFromSensor.put(HALT_MSGID, null);
        dataFromSensor.put(FIRE_MSGID, null);
    }

    public boolean isRegistered() {
        return (registered);
    }

    public void readMessage() {
        MessageQueue messageQueue = null;
        try {
            messageQueue = messageManagerInterface.GetMessageQueue();
        } catch (Exception e) {
            messageWindow.WriteMessage("Error getting message queue::" + e);
        }

        int queueLength = messageQueue.GetSize();
        for (int i = 0; i < queueLength; i++) {
            Message message = messageQueue.GetMessage();
            int messageId = message.GetMessageId();
            if (dataFromSensor.containsKey(messageId)) {
                dataFromSensor.put(messageId, message);
            }
        }
    }

    public void controlFire() {
        Message msg = dataFromSensor.get(FIRE_MSGID);
        String fireMsgTxt = msg.GetMessage();
        messageWindow.WriteMessage(fireMsgTxt);
        state.setHasAlarm(true);
        fireIndicator.SetLampColorAndMessage("FIRE ALARM ON", 3);
    }

    @Override
    public void run() {
        if (messageManagerInterface != null) {
            //Sending alive message
            MaintenanceUtils.SendAliveSignal("Fire Monitor", "The fire monitor.", messageManagerInterface);
            messageWindow = new MessageWindow("Fire Console", 0, 0);
            messageWindow.WriteMessage("Registered with the message manager.");
            fireIndicator = new Indicator("Fire Alarm OFF", 0, 0);

            try {
                messageWindow.WriteMessage("   Participant id: " + messageManagerInterface.GetMyId());
                messageWindow.WriteMessage("   Registration Time: " + messageManagerInterface.GetRegistrationTime());
            } catch (Exception e) {
                System.out.println("Error:: " + e);
            }

            while (!shutdown) {
                readMessage();
                if (dataFromSensor.get(FIRE_MSGID) != null) {
                    controlFire();
                } else if (dataFromSensor.get(HALT_MSGID) != null) {
                    try {
                        messageManagerInterface.UnRegister();
                    } catch (Exception e) {
                        messageWindow.WriteMessage("Error unregistering: " + e);
                    }

                    messageWindow.WriteMessage("\n\nSimulation stopped.\n");
                    fireIndicator.dispose();
                    break;
                }
                if (!state.getHasAlarm()) {
                    fireIndicator.SetLampColorAndMessage("FIRE ALARM OFF", 1);
                }
                if (state.sprinklerChanged) {
                    this.sendCommandToSprinklerController();
                    state.sprinklerChanged = false;
                }
                dataFromSensor.put(FIRE_MSGID, null);
                try {
                    sleep(delay);
                } catch (InterruptedException e) {
                    System.out.println("Error::" + e);
                }
            }
        } else {
            System.out.println("Unable to register with the message manager.");
        }

    }

    public void sendCommandToSprinklerController() {
        if (state.getSprinklerOn()) {
            Message msgToSend = new Message(SPRINKLER_MSGID, "S1");
            try {
                messageManagerInterface.SendMessage(msgToSend);
            } catch (Exception e) {
                System.out.println("Error sending sprinkler control message:: " + e);
            }
        } else {
            Message msgToSend = new Message(SPRINKLER_MSGID, "S0");
            try {
                messageManagerInterface.SendMessage(msgToSend);
            } catch (Exception e) {
                System.out.println("Error sending sprinkler control message::" + e);
            }
        }
    }
}
