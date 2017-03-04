package Extension;

import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

import java.util.HashMap;

/**
 * Created by yazid on 03-Mar-17.
 */
public class SecurityMonitor extends MaintainableDevice implements Runnable {

    protected final int HALT_MSGID = 99;

    protected MessageManagerInterface msgMgrInterface;
    protected String msgMgrIP;
    protected boolean registered = true;
    protected MessageWindow msgWin;
    protected HashMap<Integer, Message> dataFromQueue = new HashMap<>();
    SecurityConsole parentConsole;

    public SecurityMonitor (SecurityConsole console) {
        this.parentConsole = console;
        try {
            msgMgrInterface = new MessageManagerInterface();
        } catch (Exception e) {
            System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
            registered = false;
        }

        dataFromQueue.put (HALT_MSGID, null);
    }

    public SecurityMonitor (String msgIpAddress, SecurityConsole console) {
        this(console);
        msgMgrIP = msgIpAddress;
        try {
            msgMgrInterface = new MessageManagerInterface (msgMgrIP);
        } catch (Exception e) {
            System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
            registered = false;
        }

        dataFromQueue.put (HALT_MSGID, null);
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
            if (dataFromQueue.containsKey(msgId)) {
                dataFromQueue.put(msgId, msg);
            }
        }
    }

    @Override
    public void run() {

    }

    public void arm(){
        parentConsole.Arm();
    }
}
