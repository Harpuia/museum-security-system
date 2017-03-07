package SystemB;

import SystemC.MaintenanceUtils;
import InstrumentationPackage.Indicator;
import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

import java.util.HashMap;

import static java.lang.Thread.sleep;

/**
 * the fire monitor thread sending and receiving message with message bus.
 */
public class FireMonitor implements Runnable {
    private final int FIRE_MSGID = 6;
    private final int SPRINKLER_MSGID = 7;
    private final int HALT_MSGID = 99;

    /** The system sleeps 500 milliseconds between every message read. */
    private final int delay = 500;
    private MessageManagerInterface messageManagerInterface;
    private String messageManagerIP;
    private boolean registered = true;
    private MessageWindow messageWindow;

    /** The key is message id, and the value is the message. */
    private HashMap<Integer, Message> dataFromSensor = new HashMap<>();

    /** The shared state */
    private FireState state;

    /** The system uses a boolean variable to receive the shut down message */
    private boolean shutdown = false;

    /**The monitor shows an indicator.*/
    private Indicator fireIndicator;

    /**
     * To halt the system by being called in the FireConsoleLauncher
     */
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

    /**
     * Constructor without ip address
     * @param state to be passed by the main thread
     */
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

    /**
     * Constructor with ip address, being called when the system is started in a distributed fashion.
     * @param msgIpAddress ip address of message manager
     * @param state to be passed by the main thread
     */
    public FireMonitor(String msgIpAddress, FireState state) {
        messageManagerIP = msgIpAddress;
        try {
            messageManagerInterface = new MessageManagerInterface(messageManagerIP);
        } catch (Exception e) {
            System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
            registered = false;
        }

        this.state = state;
        dataFromSensor.put(HALT_MSGID, null);
        dataFromSensor.put(FIRE_MSGID, null);
    }

    /**
     * check if the monitor is registered
     * @return the result
     */
    public boolean isRegistered() {
        return (registered);
    }

    /**
     * To fetch a queue from message manager, and store the data in the hash map.
     */
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

            /** Note: the key is instantiated in the constructor to identify
             *  what message id it accepts.
             */
            if (dataFromSensor.containsKey(messageId)) {
                dataFromSensor.put(messageId, message);
            }
        }
    }

    /**
     * The operations to be done when the monitor receives a fire.
     */
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
            messageWindow = new MessageWindow("Fire Monitor", 0, 0);
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

                /** The system checks the fire alarm periodically, and show in the indicator.*/
                if (!state.getHasAlarm()) {
                    fireIndicator.SetLampColorAndMessage("FIRE ALARM OFF", 1);
                }

                /** The system checks when the sprinkler gets changed, and sends to the controller. */
                if (state.sprinklerChanged) {
                    this.sendCommandToSprinklerController();
                    state.sprinklerChanged = false;
                }

                /** Set the value to null to prepare for next receiving. */
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

    /**
     * This method gets called when the sprinkler gets changed.
     * It sends "S1" when the sprinkler gets on,
     * and sends "S2" when the sprinkler gets off.
     */
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
