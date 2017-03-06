package Extension;

import InstrumentationPackage.Indicator;
import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

import java.util.HashMap;

/**
 * Created by yazid on 03-Mar-17.
 */
public class SecurityMonitor extends MaintainableDevice implements Runnable {
    //Constants
    private final String WINDOW_BREACH = "Window breach!";
    private final String DOOR_BREACH = "Door breach!";
    private final String MOVEMENT_DETECTED = "Movement detected!";
    private final String DOOR_OK = "Door OK";
    private final String WINDOW_OK = "Window OK";
    private final String NO_MOVEMENT = "No movement";
    private final String SYSTEM_DISARMED = "System disarmed";
    private final long ALERT_DISPLAY_DURATION_MILLISECONDS = 10000;

    //Messaging variables
    private final int HALT_MSGID = 99;
    private MessageManagerInterface messageInterface;
    private String messageManagerIP;
    private boolean registered;
    private boolean done;
    private MessageQueue messageQueue = null;

    //Display
    MessageWindow messageWindow;
    Indicator windowIndicator;
    Indicator doorIndicator;
    Indicator movementIndicator;

    //Associated security console
    SecurityConsole associatedConsole;

    //Security status
    boolean armed = false;

    /**
     * Default constructor initializes the Message Manager Interface
     */
    public SecurityMonitor() {
        //Initializing windows
        messageWindow = new MessageWindow("Security Monitor", 0, 100);
        doorIndicator = new Indicator("Door indicator", 200, 100);
        doorIndicator.SetLampColorAndMessage(SYSTEM_DISARMED, 0);
        windowIndicator = new Indicator("Window indicator", 200, 200);
        windowIndicator.SetLampColorAndMessage(SYSTEM_DISARMED, 0);
        movementIndicator = new Indicator("Movement indicator", 200, 300);
        movementIndicator.SetLampColorAndMessage(SYSTEM_DISARMED, 0);

        //Initializing variables
        registered = false;
        done = false;
        armed = false;
        try {
            messageInterface = new MessageManagerInterface();
            registered = true;
        } catch (Exception e) {
            System.out.println("SecurityMonitor::Error instantiating message manager interface: " + e);
        }
    }

    /**
     * Constructor that registers the associated console
     *
     * @param console Security Console associated to the Security Monitor
     */
    public SecurityMonitor(SecurityConsole console) {
        this();
        this.associatedConsole = console;
    }

    /**
     * Constructor that registers the associated console and a message manager IP address
     *
     * @param msgIpAddress IP address of the message manager
     * @param console      Security Console associated to the Security Monitor
     */
    public SecurityMonitor(String msgIpAddress, SecurityConsole console) {
        this(console);
        messageManagerIP = msgIpAddress;
        try {
            messageInterface = new MessageManagerInterface(messageManagerIP);
        } catch (Exception e) {
            System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
            registered = false;
        }
    }

    public boolean isRegistered() {
        return (registered);
    }

    public void readMessageQueue() {
        try {
            messageQueue = messageInterface.GetMessageQueue();
        } catch (Exception e) {
            messageWindow.WriteMessage("Error getting message queue::" + e);
        }
    }

    public void arm() {
        armed = true;
        //Setting system state to OK
        windowIndicator.SetLampColorAndMessage(WINDOW_OK, 1);
        doorIndicator.SetLampColorAndMessage(DOOR_OK, 1);
        movementIndicator.SetLampColorAndMessage(NO_MOVEMENT, 1);
    }

    public void disarm() {
        armed = false;
        //Resetting indicators
        windowIndicator.SetLampColorAndMessage(SYSTEM_DISARMED, 0);
        doorIndicator.SetLampColorAndMessage(SYSTEM_DISARMED, 0);
        movementIndicator.SetLampColorAndMessage(SYSTEM_DISARMED, 0);
    }

    public void Halt() {
        messageWindow.WriteMessage("***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***");

        // Here we create the stop message.

        Message msg;

        msg = new Message((int) 99, "XXX");

        // Here we send the message to the message manager.

        try {
            messageInterface.SendMessage(msg);
        } catch (Exception e) {
            System.out.println("Error sending halt message:: " + e);
        }
        doorIndicator.dispose();
        windowIndicator.dispose();
        movementIndicator.dispose();
        try {
            messageInterface.UnRegister();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //Date of the last alert
        Long lastDoorAlert = null;
        Long lastWindowAlert = null;
        Long lastMovementAlert = null;

        //Message queue variables
        int messageId;
        int messageQueueSize;
        Message message;

        this.SendAliveSignal("Security Monitor", "Security monitor and its console.", messageInterface);
        if (messageInterface != null) {
            //Initial status display
            messageWindow.WriteMessage("Registered with the message manager.");
            try {
                messageWindow.WriteMessage("   Participant id: " + messageInterface.GetMyId());
                messageWindow.WriteMessage("   Registration Time: " + messageInterface.GetRegistrationTime());
            } catch (Exception e) {
                System.out.println("Error:: " + e);
            }

            //Looping
            while (!done) {
                if (armed) {
                    //Updating indicators
                    if (lastDoorAlert != null && (System.currentTimeMillis() - lastDoorAlert) > ALERT_DISPLAY_DURATION_MILLISECONDS) {
                        doorIndicator.SetLampColorAndMessage(DOOR_OK, 1);
                        lastDoorAlert = null;
                    }
                    if (lastWindowAlert != null && (System.currentTimeMillis() - lastWindowAlert) > ALERT_DISPLAY_DURATION_MILLISECONDS) {
                        windowIndicator.SetLampColorAndMessage(WINDOW_OK, 1);
                        lastWindowAlert = null;
                    }
                    if (lastMovementAlert != null && (System.currentTimeMillis() - lastMovementAlert) > ALERT_DISPLAY_DURATION_MILLISECONDS) {
                        movementIndicator.SetLampColorAndMessage(DOOR_OK, 1);
                        lastMovementAlert = null;
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Reading queue
                this.readMessageQueue();
                messageQueueSize = messageQueue.GetSize();
                for (int i = 0; i < messageQueueSize; i++) {
                    message = messageQueue.GetMessage();
                    messageId = message.GetMessageId();
                    switch (messageId) {
                        //Security alert message
                        case 10:
                            if (armed)
                                switch (message.GetMessage()) {
                                    case "W":
                                        messageWindow.WriteMessage(WINDOW_BREACH);
                                        windowIndicator.SetLampColorAndMessage(WINDOW_BREACH, 3);
                                        lastWindowAlert = System.currentTimeMillis();
                                        break;
                                    case "M":
                                        messageWindow.WriteMessage(MOVEMENT_DETECTED);
                                        movementIndicator.SetLampColorAndMessage(MOVEMENT_DETECTED, 3);
                                        lastMovementAlert = System.currentTimeMillis();
                                        break;
                                    case "D":
                                        messageWindow.WriteMessage(DOOR_BREACH);
                                        doorIndicator.SetLampColorAndMessage(DOOR_BREACH, 3);
                                        lastDoorAlert = System.currentTimeMillis();
                                        break;
                                }
                            break;
                        case 99:
                            Halt();
                            break;
                        default:
                            break;
                    }
                }
            }
        } else {
            System.out.println("Unable to register with the message manager.\n\n");
        }
    }
}
