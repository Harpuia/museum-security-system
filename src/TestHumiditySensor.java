import Extension.MaintainableDevice;
import InstrumentationPackage.*;
import MessagePackage.*;

import java.util.*;

class TestHumiditySensor extends MaintainableDevice{
    private String MsgMgrIP;                    // Message Manager IP address

    private int MsgId = 0;                        // User specified message ID
    private MessageManagerInterface em = null;    // Interface object to the message manager
    private float RelativeHumidity;                // Current simulated ambient room humidity
    private float DriftValue;                    // The amount of humidity gained or lost

    public void doAnythingHere() {

    }

    public String getMsgMgrIP() {
        return MsgMgrIP;
    }

    public void setMsgMgrIP(String msgMgrIP) {
        MsgMgrIP = msgMgrIP;
    }

    public int getMsgId() {
        return MsgId;
    }

    public void setMsgId(int msgId) {
        MsgId = msgId;
    }

    public MessageManagerInterface getEm() {
        return em;
    }

    public void setEm(MessageManagerInterface em) {
        this.em = em;
    }


    public float getRelativeHumidity() {
        return RelativeHumidity;
    }

    public void setRelativeHumidity(float relativeHumidity) {
        RelativeHumidity = relativeHumidity;
    }

    public float getDriftValue() {
        return DriftValue;
    }

    public void setDriftValue(float driftValue) {
        DriftValue = driftValue;
    }
    public void registerToMessageBus() {
        try {
            em = new MessageManagerInterface();
        } catch (Exception e) {
            System.out.println("Error instantiating message manager interface: " + e);
        }
    }

    public void registerToRemoteMessageBus() {
        try {
            em = new MessageManagerInterface(MsgMgrIP);
        } catch (Exception e) {
            System.out.println("Error instantiating message manager interface: " + e);
        }
    }

    public boolean isRegistered() {
        if (em != null) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String args[]) {

        boolean Done = false;                // Loop termination flag
        MessageQueue eq = null;                // Message Queue
        TestHumiditySensor sensor = new TestHumiditySensor();
        boolean HumidifierState = false;    // Humidifier state: false == off, true == on
        boolean DehumidifierState = false;    // Dehumidifier state: false == off, true == on
        Message Msg = null;                    // Message object
        int Delay = 2500;                    // The loop delay (2.5 seconds)
        /////////////////////////////////////////////////////////////////////////////////
        // Get the IP address of the message manager
        /////////////////////////////////////////////////////////////////////////////////

        if (args.length == 0) {
            // message manager is on the local system
            System.out.println("\n\nAttempting to register on the local machine...");
            sensor.registerToMessageBus();
        } else {
            // message manager is not on the local system
            sensor.setMsgMgrIP(args[0]);
            System.out.println("\n\nAttempting to register on the machine:: " + sensor.getMsgMgrIP());
            try {
                sensor.registerToRemoteMessageBus();
            } catch (Exception e) {
                System.out.println("Error instantiating message manager interface: " + e);
            }

        }

        // Here we check to see if registration worked. If ef is null then the
        // message manager interface was not properly created.

        if (sensor.isRegistered()) {
            sensor.SendAliveSignal("TestSensor","hello~",sensor.getEm());
            // We create a message window. Note that we place this panel about 1/2 across
            // and 2/3s down the screen

            float WinPosX = 0.5f;    //This is the X position of the message window in terms
            //of a percentage of the screen height
            float WinPosY = 0.60f;    //This is the Y position of the message window in terms
            //of a percentage of the screen height

            MessageWindow mw = new MessageWindow("Humidity Sensor", WinPosX, WinPosY);

            mw.WriteMessage("Registered with the message manager.");

            try {
                mw.WriteMessage("   Participant id: " + sensor.getEm().GetMyId());
                mw.WriteMessage("   Registration Time: " + sensor.getEm().GetRegistrationTime());

            } catch (Exception e) {
                mw.WriteMessage("Error:: " + e);

            } // catch

            mw.WriteMessage("\nInitializing Humidity Simulation::");

            sensor.setRelativeHumidity(GetRandomNumber() * (float) 100.00);

            if (CoinToss()) {
                sensor.setDriftValue(GetRandomNumber() * (float) -1.0);

            } else {

                sensor.setDriftValue(GetRandomNumber());

            } // if

            mw.WriteMessage("   Initial Humidity Set:: " + sensor.getRelativeHumidity());
            // mw.WriteMessage("   Drift Value Set:: " + DriftValue ); // Used to debug the random drift values

            /********************************************************************
             ** Here we start the main simulation loop
             *********************************************************************/

            mw.WriteMessage("Beginning Simulation... ");


            while (!Done) {
                // Post the current relative humidity

                PostHumidity(sensor.getEm(), sensor.getRelativeHumidity());

                mw.WriteMessage("Current Relative Humidity:: " + sensor.getRelativeHumidity() + "%");

                // Get the message queue

                try {
                    eq = sensor.getEm().GetMessageQueue();

                } // try

                catch (Exception e) {
                    mw.WriteMessage("Error getting message queue::" + e);

                } // catch

                // If there are messages in the queue, we read through them.
                // We are looking for MessageIDs = -4, this means the the humidify or
                // dehumidifier has been turned on/off. Note that we get all the messages
                // from the queue at once... there is a 2.5 second delay between samples,..
                // so the assumption is that there should only be a message at most.
                // If there are more, it is the last message that will effect the
                // output of the humidity as it would in reality.

                int qlen = eq.GetSize();

                for (int i = 0; i < qlen; i++) {
                    Msg = eq.GetMessage();

                    if (Msg.GetMessageId() == -4) {
                        if (Msg.GetMessage().equalsIgnoreCase("H1")) // humidifier on
                        {
                            HumidifierState = true;

                        } // if

                        if (Msg.GetMessage().equalsIgnoreCase("H0")) // humidifier off
                        {
                            HumidifierState = false;

                        } // if

                        if (Msg.GetMessage().equalsIgnoreCase("D1")) // dehumidifier on
                        {
                            DehumidifierState = true;

                        } // if

                        if (Msg.GetMessage().equalsIgnoreCase("D0")) // dehumidifier off
                        {
                            DehumidifierState = false;

                        } // if

                    } // if

                    // If the message ID == 99 then this is a signal that the simulation
                    // is to end. At this point, the loop termination flag is set to
                    // true and this process unregisters from the message manager.

                    if (Msg.GetMessageId() == 99) {
                        Done = true;

                        try {
                            sensor.getEm().UnRegister();

                        } // try

                        catch (Exception e) {
                            mw.WriteMessage("Error unregistering: " + e);

                        } // catch

                        mw.WriteMessage("\n\nSimulation Stopped. \n");

                    } // if

                } // for

                // Now we trend the relative humidity according to the status of the
                // humidifier/dehumidifier controller.

                if (HumidifierState) {
                    sensor.setRelativeHumidity(sensor.getRelativeHumidity() + GetRandomNumber());

                } // if humidifier is on

                if (!HumidifierState && !DehumidifierState) {
                    sensor.setRelativeHumidity(sensor.getRelativeHumidity()+sensor.getDriftValue());

                } // if both the humidifier and dehumidifier are off

                if (DehumidifierState) {
                    sensor.setRelativeHumidity(sensor.getRelativeHumidity() - GetRandomNumber());

                } // if dehumidifier is on

                // Here we wait for a 2.5 seconds before we start the next sample

                try {
                    Thread.sleep(Delay);

                } // try

                catch (Exception e) {
                    mw.WriteMessage("Sleep error:: " + e);

                } // catch

            } // while

        } else {

            System.out.println("Unable to register with the message manager.\n\n");

        } // if

    } // main

    /***************************************************************************
     * CONCRETE METHOD:: GetRandomNumber
     * Purpose: This method provides the simulation with random floating point
     *		   humidity values between 0.1 and 0.9.
     *
     * Arguments: None.
     *
     * Returns: float
     *
     * Exceptions: None
     *
     ***************************************************************************/

    static private float GetRandomNumber() {
        Random r = new Random();
        Float Val;

        Val = Float.valueOf((float) -1.0);

        while (Val < 0.1) {
            Val = r.nextFloat();
        }

        return (Val.floatValue());

    } // GetRandomNumber

    /***************************************************************************
     * CONCRETE METHOD:: CoinToss
     * Purpose: This method provides a random true or false value used for
     * determining the positiveness or negativeness of the drift value.
     *
     * Arguments: None.
     *
     * Returns: boolean
     *
     * Exceptions: None
     *
     ***************************************************************************/

    static private boolean CoinToss() {
        Random r = new Random();

        return (r.nextBoolean());

    } // CoinToss

    /***************************************************************************
     * CONCRETE METHOD:: PostHumidity
     * Purpose: This method posts the specified relative humidity value to the
     * specified message manager. This method assumes an message ID of 2.
     *
     * Arguments: MessageManagerInterface ei - this is the messagemanger interface
     *			 where the message will be posted.
     *
     *			 float humidity - this is the humidity value.
     *
     * Returns: none
     *
     * Exceptions: None
     *
     ***************************************************************************/

    static private void PostHumidity(MessageManagerInterface ei, float humidity) {
        // Here we create the message.

        Message msg = new Message((int) 2, String.valueOf(humidity));

        // Here we send the message to the message manager.

        try {
            ei.SendMessage(msg);
            //mw.WriteMessage( "Sent Humidity Message" );

        } // try

        catch (Exception e) {
            System.out.println("Error Posting Relative Humidity:: " + e);

        } // catch

    } // PostHumidity




} // Humidity Sensor