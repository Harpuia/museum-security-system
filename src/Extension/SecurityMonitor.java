package Extension;

import InstrumentationPackage.MessageWindow;

import static java.lang.Thread.sleep;

/**
 * Created by yazid on 03-Mar-17.
 */
public class SecurityMonitor extends BaseMonitor {
    private final int FIRE_MSGID = 6;
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
        /*
        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
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
                controlIntrusion();
                controlFire();
                break;
            }
            try {
                sleep (delay);
            } catch (InterruptedException e) {
                System.out.println ("Error::" + e);
            }
        } else {
            System.out.println ("Unable to register with the message manager.");
        }

    }
}
