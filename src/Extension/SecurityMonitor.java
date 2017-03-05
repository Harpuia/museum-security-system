package Extension;

import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;

import static java.lang.Thread.sleep;

/**
 * Created by yazid on 03-Mar-17.
 */
public class SecurityMonitor extends BaseMonitor {
    private final int FIRE_MSGID = 6;
    private final int SPRINKLER_MSGID = 7;
    private final int delay = 1000;
    private SecurityState state;
    //Monitor can call all console's public methods
    //Console can call all monitor's public methods


    public SecurityMonitor (SecurityState state) {
        super();
        this.state = state;
        dataFromSensor.put (FIRE_MSGID, null);
    }

    public SecurityMonitor (String msgIpAddress, SecurityState state) {
        super(msgIpAddress);
        this.state = state;
        dataFromSensor.put (FIRE_MSGID, null);
    }

    public void controlIntrusion () {

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

            while (true) {
                readMsg();
                //TODO: ADD ACTION HERE
                //controlIntrusion();
                //controlFire();

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

                //state.setHasAlarm(true);
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
