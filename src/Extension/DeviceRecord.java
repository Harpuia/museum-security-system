package Extension;

/**
 * Created by hitno on 2017/3/4.
 */
public class DeviceRecord{
    final static int INVALID_DOG_FOOD = Integer.MAX_VALUE;
    /** Member Variable **/
    private long senderID;
    private String name;
    private String description;

    private int dogFood = INVALID_DOG_FOOD;

    public DeviceRecord(long senderID, String name, String description) {
        this.senderID = senderID;
        this.name = name;
        this.description = description;
    }

    public DeviceRecord(long senderID, String name, String description, int food) {
        this.senderID = senderID;
        this.name = name;
        this.description = description;
        dogFood = food;
    }

    public long getSenderID() {
        return senderID;
    }

    public void setSenderID(long senderID) {
        this.senderID = senderID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDogFood() {
        return dogFood;
    }

    public void setDogFood(int dogFood) {
        this.dogFood = dogFood;
    }

    public boolean isAlive(){
        if(dogFood > 0){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        if(dogFood <= 0){
            return name + " : " + description + "     no responding";
        }else{
            return name + " : " + description;
        }
    }
}
