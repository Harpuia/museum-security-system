package Extension;

import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by rachel on 3/3/17.
 */
public class MaintenanceMonitor extends Thread {

    boolean Registered = true;
    MessageWindow mw = null;
    private MessageManagerInterface msgManagerIF = null;
    private Map<Long, Integer> idToCount;
    private List<List<String>> msgTable;
    private String MsgMgrIP = null;
    private WatchDogUtil wdog;

    public MaintenanceMonitor() {
        try {
            msgManagerIF = new MessageManagerInterface();
            msgTable = new ArrayList<>();
            mw = new MessageWindow("MaintenanceMonitor", 10, 20);
            wdog=new WatchDogUtil();


        } catch (Exception e) {
            System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
            Registered = false;

        }

    }

    public MaintenanceMonitor(String MsgIpAddress) {
        // message manager is not on the local system

        MsgMgrIP = MsgIpAddress;

        try {
            // Here we create an message manager interface object. This assumes
            // that the message manager is NOT on the local machine

            msgManagerIF = new MessageManagerInterface(MsgMgrIP);
        } catch (Exception e) {
            System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
            Registered = false;

        } // catch

    }



    public void run() {

        Message Msg = null;
        boolean Done = false;
        MessageQueue eq = null;
        //String status = "Alive";
        int number = 10;
        //used for store time for feeding dogs
        Timestamp feedTime = new Timestamp(System.currentTimeMillis());

        while (!Done) {
            Timestamp curTime = new Timestamp(System.currentTimeMillis());
            long timeSlot = curTime.getTime() - feedTime.getTime();
            // every 1s, we invoke timer in WatchDog, if someone new is hungry then
            // we refresh the windows
            if( (timeSlot/1000) >= 1 ){
                feedTime = curTime;
                boolean isChange = wdog.timerOne();
                if(isChange){
                    List<DeviceRecord> tmpAliveDeviceList = wdog.getAliveDeviceList();
                    List<DeviceRecord> tmpDeadDeviceList = wdog.getNoRespondDeviceList();
                    mw.clearWindow();
                    for (DeviceRecord device : tmpAliveDeviceList) {
                        mw.WriteMessage(device.toString());
                    }
                    for (DeviceRecord device : tmpDeadDeviceList) {
                        mw.WriteMessage(device.toString());
                    }
                }
            }

            try {
                eq = msgManagerIF.GetMessageQueue();

            } catch (Exception e) {
                mw.WriteMessage("Error getting message queue::" + e);

            }
            int qlen = eq.GetSize();
            for (int i = 0; i < qlen; i++) {
                Msg = eq.GetMessage();
                if (Msg.GetMessageId() == 3) {
                    //idToCount.put(Msg.GetSenderId(), number);
                    String[] msgPartition = Msg.GetMessage().split(" : ");
                    String msgName = msgPartition[0];
                    String msgDescription = msgPartition[1];
                    if (wdog.isNewDevice(msgName)) {
                        wdog.addNewDevice(Msg.GetSenderId(), msgName, msgDescription);
                        mw.WriteMessage(Msg.GetMessage());
                    } else {
                        boolean hasRevive = wdog.updateDeviceStatusByName(msgName);
                        if (hasRevive) {
                            List<DeviceRecord> tmpAliveDeviceList = wdog.getAliveDeviceList();
                            List<DeviceRecord> tmpDeadDeviceList = wdog.getNoRespondDeviceList();
                            mw.clearWindow();
                            for (DeviceRecord device : tmpAliveDeviceList) {
                                mw.WriteMessage(device.toString());
                            }
                            for (DeviceRecord device : tmpDeadDeviceList) {
                                mw.WriteMessage(device.toString());
                            }
                        }
                    }

                   /* if (!msgTable.contains(Msg.GetSenderId())) {
                        List<String> listTemp = new ArrayList<>();
                        listTemp.add(Msg.GetMessage());
                        listTemp.add(String.valueOf(number));
                        msgTable.add(listTemp);
                        mw.WriteMessage(Msg.GetMessage());

                    } else {
                        List<String> listTemp = new ArrayList<>();
                        listTemp.add(Msg.GetMessage());
                        listTemp.add(String.valueOf(number));
                        msgTable.set(msgTable.indexOf(Msg.GetSenderId()), listTemp);
                    }*/

                }
            }
            /*List<String> numbersOfAllDevices = new ArrayList<>();
            for (int y = 0; y < msgTable.size(); y++) {
                numbersOfAllDevices.add(msgTable.get(y).get(1));
            }

            while (number > 0) {
                try {
                    Thread.sleep(1000);
                    for (int i = 0; i < msgTable.size(); i++) {
                        number--;
                        numbersOfAllDevices.set(i, String.valueOf(number));
                        msgTable.set(i, numbersOfAllDevices);
                    }
                } catch (InterruptedException ie) {

                }


            }*/
        }


}

public static void main(String [] args)
{
    MaintenanceMonitor maintenMonitor=new MaintenanceMonitor();
    maintenMonitor.start();
}

}
