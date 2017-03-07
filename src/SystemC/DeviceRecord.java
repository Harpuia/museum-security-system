package SystemC;

/*
 * class DeviceRecord
 * this class will store a device's name description and senderID
 * 2017-03-04 Jiawei
 */
public class DeviceRecord{
    final static int INVALID_DOG_FOOD = Integer.MAX_VALUE;
    /** Member Variable **/
    private long senderID;
    private String name;
    private String description;
    // countdown num represents how many seconds elapse when we judge a device is disconnect whether or not
    private int countDownNum = INVALID_DOG_FOOD;

    /** Constructor **/
    public DeviceRecord(long senderID, String name, String description) {
        this.senderID = senderID;
        this.name = name;
        this.description = description;
    }

    public DeviceRecord(long senderID, String name, String description, int food) {
        this.senderID = senderID;
        this.name = name;
        this.description = description;
        countDownNum = food;
    }

    /** Methods **/
    /**
     * If countdown is larger than 0, we regard this device as alive
     * @return true if countDownNun > 0
     */
    public boolean isAlive(){
        if(countDownNum > 0){
            return true;
        }
        return false;
    }

    /**
     * Override toString() method for displaying easily in MaintainanceMonitor
     * @return
     */
    @Override
    public String toString() {
        if(countDownNum <= 0){
            return name + " : " + description + " >>>> Disconnected!";
        }else{
            return name + " : " + description;
        }
    }

    /** Getter and Setter **/
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCountDownNum() {
        return countDownNum;
    }

    public void setCountDownNum(int countDownNum) {
        this.countDownNum = countDownNum;
    }

}
