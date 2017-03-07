package SystemB;

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
    private final int delay = 1000;
    private MessageManagerInterface msgMgrInterface;
    private String msgMgrIP;
    private boolean registered = true;
    private MessageWindow msgWin;
    private HashMap<Integer, Message> dataFromSensor = new HashMap<>();
    private FireState state;
    private boolean shutdown = false;


    public void shutdown() {
        msgWin.WriteMessage("***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***");
        Message msg = new Message(HALT_MSGID, "XXX");
        try {
            msgMgrInterface.SendMessage(msg);
        } catch(Exception e) {
            System.out.println("Error sending halt message:: " + e);
        }
        this.shutdown = true;
    }

    public FireMonitor(FireState state) {
        try {
            msgMgrInterface = new MessageManagerInterface();
        } catch (Exception e) {
            System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
            registered = false;
        }

        this.state = state;
        dataFromSensor.put (HALT_MSGID, null);
        dataFromSensor.put (FIRE_MSGID, null);
    }

    public FireMonitor(String msgIpAddress, FireState state) {
        msgMgrIP = msgIpAddress;
        try {
            msgMgrInterface = new MessageManagerInterface (msgMgrIP);
        } catch (Exception e) {
            System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
            registered = false;
        }

        this.state = state;
        dataFromSensor.put (HALT_MSGID, null);
        dataFromSensor.put (FIRE_MSGID, null);
    }

    public boolean isRegistered () {
        return(registered);
    }

    public void halt () {
        msgWin.WriteMessage( "***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***" );
        Message msg = new Message (HALT_MSGID, "XXX" );

        try {
            msgMgrInterface.SendMessage (msg);
        } catch (Exception e) {
            System.out.println("Error sending halt message:: " + e);
        }
    }

    public void readMsg () {
        MessageQueue msgQueue = null;
        try {
            msgQueue = msgMgrInterface.GetMessageQueue();
        } catch (Exception e) {
            msgWin.WriteMessage ("Error getting message queue::" + e);
        }

        int queueLength = msgQueue.GetSize();
        for (int i = 0; i < queueLength; i++) {
            Message msg = msgQueue.GetMessage();
            int msgId = msg.GetMessageId();
            if (dataFromSensor.containsKey(msgId)) {
                dataFromSensor.put(msgId, msg);
            }
        }
    }

    public void controlFire () {
        Message msg = dataFromSensor.get(FIRE_MSGID);
        String fireMsgTxt = msg.GetMessage();
        msgWin.WriteMessage(fireMsgTxt);
        state.setHasAlarm(true);
    }

    @Override
    public void run () {
        if (msgMgrInterface != null) {
            msgWin = new MessageWindow("Security Console", 0, 0);
            msgWin.WriteMessage("Registered with the message manager.");

            try {
                msgWin.WriteMessage("   Participant id: " + msgMgrInterface.GetMyId());
                msgWin.WriteMessage("   Registration Time: " + msgMgrInterface.GetRegistrationTime());
            } catch (Exception e) {
                System.out.println("Error:: " + e);
            }

            while (!shutdown) {
                readMsg();
                if (dataFromSensor.get(FIRE_MSGID) != null) {
                    msgWin.WriteMessage("Enters controlFire");
                    controlFire();
                } else if (dataFromSensor.get(HALT_MSGID) != null) {
                    try {
                        msgMgrInterface.UnRegister();
                    } catch (Exception e) {
                        msgWin.WriteMessage("Error unregistering: " + e);
                    }

                    msgWin.WriteMessage("\n\nSimulation stopped.\n");
                    break;
                }

                dataFromSensor.put(FIRE_MSGID, null);

                if(state.getSprinklerOn()) {
                    Message msgToSend = new Message(SPRINKLER_MSGID, "S1");
                    try {
                        msgMgrInterface.SendMessage(msgToSend);
                    } catch (Exception e) {
                        System.out.println("Error sending sprinkler control message:: " + e);
                    }
                } else {
                    Message msgToSend = new Message(SPRINKLER_MSGID, "S0");
                    try {
                        msgMgrInterface.SendMessage(msgToSend);
                    } catch (Exception e) {
                        System.out.println("Error sending sprinkler control message::" + e);
                    }
                }

                try {
                    sleep (delay);
                } catch (InterruptedException e) {
                    System.out.println ("Error::" + e);
                }
            }
        } else {
            System.out.println ("Unable to register with the message manager.");
        }

    }
}
