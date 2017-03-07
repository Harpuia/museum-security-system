package Extension;

import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

import java.sql.Timestamp;
import java.util.List;

/**
 * MaintenanceMonitor is used to receive alive-message from working devices, including
 * sensor, monitor(except for Maintenance Monitor) and controller
 */
public class MaintenanceMonitor {
    /**
     * Member Variables
     **/
    boolean isRegistered = true;    // show if the maintenance has registered to message manager
    MessageWindow messageWindow = null; // convey the output of the monitor
    private MessageManagerInterface msgManagerIF = null;     // message manager interface
    private String msgMgrIP = null; // remote message manager IP address    // if message manager is remote, we store its IP address
    private WatchDogTimerUtil watchDogTimerUtil;

    /**
     * Default constructor
     **/
    public MaintenanceMonitor() {
        try {
            msgManagerIF = new MessageManagerInterface();
            messageWindow = new MessageWindow("MaintenanceMonitor", 10, 20);
            watchDogTimerUtil = new WatchDogTimerUtil();
        } catch (Exception e) {
            System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
            isRegistered = false;
        }
    }

    /**
     * Constructor with remote connection
     *
     * @param MsgIpAddress Message Manager IP address
     */
    public MaintenanceMonitor(String MsgIpAddress) {
        // message manager is not on the local system
        msgMgrIP = MsgIpAddress;
        try {
            // Here we create an message manager interface object. This assumes
            // that the message manager is NOT on the local machine
            msgManagerIF = new MessageManagerInterface(msgMgrIP);
        } catch (Exception e) {
            System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
            isRegistered = false;
        }
    }

    /**
     * Main method
     */
    public void startMonitoring() {
        Message tmpMsg = null;
        boolean done = false;
        MessageQueue messageQueue = null;
        //used for store time for fulfill countdown time
        Timestamp feedTime = new Timestamp(System.currentTimeMillis());

        while (!done) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Timestamp curTime = new Timestamp(System.currentTimeMillis());
            long timeSlot = curTime.getTime() - feedTime.getTime();
            // every 1 sec, we invoke timer in WatchDog, if someone new is hungry then
            // we refresh the windows
            if ((timeSlot / 1000) >= 1) {
                feedTime = curTime;
                boolean isChange = watchDogTimerUtil.timerOne();
                if (isChange) {
                    List<DeviceRecord> tmpAliveDeviceList = watchDogTimerUtil.getAliveDeviceList();
                    List<DeviceRecord> tmpDeadDeviceList = watchDogTimerUtil.getNoRespondDeviceList();
                    messageWindow.clearWindow();
                    for (DeviceRecord device : tmpAliveDeviceList) {
                        messageWindow.WriteMessage(device.toString());
                    }
                    for (DeviceRecord device : tmpDeadDeviceList) {
                        messageWindow.WriteMessage(device.toString());
                    }
                }
            }
            // try fetch message queue
            try {
                messageQueue = msgManagerIF.GetMessageQueue();
            } catch (Exception e) {
                messageWindow.WriteMessage("Error getting message queue::" + e);
            }
            // read messages
            int qlen = messageQueue.GetSize();
            for (int i = 0; i < qlen; i++) {
                tmpMsg = messageQueue.GetMessage();
                if (tmpMsg.GetMessageId() == 3) {
                    String[] msgPartition = tmpMsg.GetMessage().split(" : ");
                    String msgName = msgPartition[0];
                    String msgDescription = msgPartition[1];
                    if (watchDogTimerUtil.isNewDevice(msgName)) {
                        watchDogTimerUtil.addNewDevice(tmpMsg.GetSenderId(), msgName, msgDescription);
                        messageWindow.WriteMessage(tmpMsg.GetMessage());
                    } else {
                        boolean hasRevive = watchDogTimerUtil.updateDeviceStatusByName(msgName);
                        if (hasRevive) {
                            List<DeviceRecord> tmpAliveDeviceList = watchDogTimerUtil.getAliveDeviceList();
                            List<DeviceRecord> tmpDeadDeviceList = watchDogTimerUtil.getNoRespondDeviceList();
                            messageWindow.clearWindow();
                            for (DeviceRecord device : tmpAliveDeviceList) {
                                messageWindow.WriteMessage(device.toString());
                            }
                            for (DeviceRecord device : tmpDeadDeviceList) {
                                messageWindow.WriteMessage(device.toString());
                            }
                        }
                    }
                } else if (tmpMsg.GetMessageId() == 99 ){
                    haltMonitor();
                    done = true;
                }
            }
        }
    }

    /**
     * Halt the monitor
     */
    public void haltMonitor() {

        messageWindow.WriteMessage("***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***");
        try {
            msgManagerIF.UnRegister();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method
     *
     * @param args
     */
    public static void main(String[] args) {
        MaintenanceMonitor maintenanceMonitor;
        if (args.length > 0) {
            maintenanceMonitor = new MaintenanceMonitor(args[0]);
        } else {
            maintenanceMonitor = new MaintenanceMonitor();
        }
        maintenanceMonitor.startMonitoring();
    }
}
