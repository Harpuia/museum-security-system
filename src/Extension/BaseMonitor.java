package Extension;

import InstrumentationPackage.Indicator;
import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

import java.util.HashMap;

/**
 * Created by Yuchao on 03/03/2017.
 */
public class BaseMonitor extends MaintainableDevice implements Runnable {
    protected final int HALT_MSGID = 99;

    protected MessageManagerInterface msgMgrInterface;
    protected String msgMgrIP;
    protected boolean registered = true;
    protected MessageWindow msgWin;
    protected HashMap<Integer, Message> dataFromSensor = new HashMap<>();


    public BaseMonitor () {
        try {
            msgMgrInterface = new MessageManagerInterface();
        } catch (Exception e) {
            System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
            registered = false;
        }

        dataFromSensor.put (HALT_MSGID, null);
    }

    public BaseMonitor (String msgIpAddress) {
        msgMgrIP = msgIpAddress;
        try {
            msgMgrInterface = new MessageManagerInterface (msgMgrIP);
        } catch (Exception e) {
            System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
            registered = false;
        }

        dataFromSensor.put (HALT_MSGID, null);
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

    @Override
    public void run() {}
}
