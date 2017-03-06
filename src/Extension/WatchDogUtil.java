package Extension;

import MessagePackage.Message;

import java.util.*;

/**
 * Created by Jiawei on 2017/3/4.
 */
public class WatchDogUtil {

    final static int DOG_FOOD = 5;

    private List<DeviceRecord> deviceList;
    // assume each device has unique name
    private HashSet<String> deviceNameSet;


    WatchDogUtil(){
        deviceList = new ArrayList<>();
        deviceNameSet = new HashSet<>();
    }

    public boolean addNewDevice(long participantID, String name, String description){
        if(!deviceNameSet.contains(name)){
            DeviceRecord record = new DeviceRecord(participantID, name, description, DOG_FOOD);
            deviceList.add(record);
            deviceNameSet.add(name);
            return true;
        }else{  // if existed, return false
            return false;       // device with same name has added
        }
    }

    public boolean isNewDevice(String name){
        if(!deviceNameSet.contains(name)){
            return true;
        }
        return false;
    }

    /**
     * Reduce watch dogs' food by one, if there's any dog food less than zero
     * we return true if new dog is
     * @return boolean
     */
    public boolean timerOne(){
        boolean dogIsHungry = false;
        for (DeviceRecord device : deviceList) {
            device.setDogFood(device.getDogFood() - 1);
            if(device.getDogFood() == 0){dogIsHungry = true;}
        }
        return dogIsHungry;
    }

    public List<DeviceRecord> getAllDevice(){
        List<DeviceRecord> copyList= new ArrayList<>();
        Collections.copy(copyList, deviceList);
        return deviceList;
    }

    public List<DeviceRecord> getAliveDeviceList(){
        List<DeviceRecord> aliveDevices = new ArrayList<DeviceRecord>();
        for (DeviceRecord device : deviceList) {
            if(device.isAlive()){
                aliveDevices.add(device);
            }
        }
        return aliveDevices;
    }

    public List<DeviceRecord> getNoRespondDeviceList(){
        List<DeviceRecord> deadDevices = new ArrayList<DeviceRecord>();
        for (DeviceRecord device : deviceList) {
            if(!device.isAlive()){
                deadDevices.add(device);
            }
        }
        return deadDevices;
    }

    public int getDeviceNumber(){
        return deviceList.size();
    }

    /**
     * This method deals with updating the device status, if the device previously dead then alive, we would see the status change
     * We assume the description won't change, and the total number of devices are limited and pre-set
     * @param name
     * @return if status change, return true
     */
    public boolean updateDeviceStatusByName(String name){
        boolean revive = false;
        if(deviceNameSet.contains(name)){
            for (DeviceRecord device:deviceList) {
                if(device.getName().equals(name)){
                    // feed the dog
                    if(device.getDogFood() <= 0){
                        revive = true;
                    }
                    device.setDogFood(DOG_FOOD);
                }
            }
        }
        return revive;
    }

}
