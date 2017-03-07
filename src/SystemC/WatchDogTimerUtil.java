package SystemC;

import java.util.*;

/**
 * The class is used as WatchDogTimer utility
 * this class will maintain a list of device record and a set of device name
 * The device record will keep device name, device descrption and device status
 * The set of device name will help to determine if there exists the device info in device list
 * <p>
 * Each device will have full number of DOG_FOOD in their countDown field
 */
public class WatchDogTimerUtil {
    /**
     * Member Variable
     **/
    // agreement: the number of DOG_FOOD represents how many second elapse for determining one device is in no-responding-status
    //              when you use this class
    final static int DOG_FOOD = 10;

    private List<DeviceRecord> deviceList;
    // assume each device has unique name
    private HashSet<String> deviceNameSet;

    /**
     * Constructor
     **/
    WatchDogTimerUtil() {
        deviceList = new ArrayList<>();
        deviceNameSet = new HashSet<>();
    }

    /** Methods **/
    /**
     * add a new device info into device list
     *
     * @param participantID
     * @param name
     * @param description
     * @return if the device existed in the list then return false
     */
    public boolean addNewDevice(long participantID, String name, String description) {
        if (!deviceNameSet.contains(name)) {
            DeviceRecord record = new DeviceRecord(participantID, name, description, DOG_FOOD);
            deviceList.add(record);
            deviceNameSet.add(name);
            return true;
        } else {  // if existed, return false
            return false;       // device with same name has added
        }
    }

    /**
     * To see if the device is existed by passing device name as parameter
     *
     * @param name
     * @return true if the device exists in the list
     */
    public boolean isNewDevice(String name) {
        if (!deviceNameSet.contains(name)) {
            return true;
        }
        return false;
    }

    /**
     * Call this method for countdown 1 countdown number for every devices
     * Reduce watch dog timer' food by one,which mean reduce the countdown time of each device info by one.
     * If there's any new no-responding devices <- dog food(countdown time) less than zero
     * we return true
     *
     * @return boolean
     */
    public boolean timerOne() {
        boolean dogIsHungry = false;
        for (DeviceRecord device : deviceList) {
            device.setCountDownNum(device.getCountDownNum() - 1);
            if (device.getCountDownNum() == 0) {
                dogIsHungry = true;
            }
        }
        return dogIsHungry;
    }

    /**
     * Return all device info stored in the deviceList
     *
     * @return
     */
    public List<DeviceRecord> getAllDevice() {
        List<DeviceRecord> copyList = new ArrayList<>();
        Collections.copy(copyList, deviceList);
        return deviceList;
    }

    /**
     * Return all alive devices
     *
     * @return
     */
    public List<DeviceRecord> getAliveDeviceList() {
        List<DeviceRecord> aliveDevices = new ArrayList<DeviceRecord>();
        for (DeviceRecord device : deviceList) {
            if (device.isAlive()) {
                aliveDevices.add(device);
            }
        }
        return aliveDevices;
    }

    /**
     * Return all no-responding devices
     *
     * @return
     */
    public List<DeviceRecord> getNoRespondDeviceList() {
        List<DeviceRecord> deadDevices = new ArrayList<DeviceRecord>();
        for (DeviceRecord device : deviceList) {
            if (!device.isAlive()) {
                deadDevices.add(device);
            }
        }
        return deadDevices;
    }

    /**
     * Return how many devices stored in the device list
     *
     * @return
     */
    public int getDeviceNumber() {
        return deviceList.size();
    }

    /**
     * This method deals with updating the device status, if the device previously dead then alive, we would see the status change
     * We assume the description won't change, and the total number of devices are limited and pre-set
     *
     * @param name
     * @return if status change, return true
     */
    public boolean updateDeviceStatusByName(String name) {
        boolean revive = false;
        if (deviceNameSet.contains(name)) {
            for (DeviceRecord device : deviceList) {
                if (device.getName().equals(name)) {
                    // feed the dog
                    if (device.getCountDownNum() <= 0) {
                        revive = true;
                    }
                    device.setCountDownNum(DOG_FOOD);
                }
            }
        }
        return revive;
    }

}
