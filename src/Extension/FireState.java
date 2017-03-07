package Extension;

/**
 * Created by Yuchao on 04/03/2017.
 */
public class FireState {
    private boolean hasAlarm;
    private boolean isArmed;
    private boolean sprinklerOn;
    private boolean isStop;

    public FireState() {
        hasAlarm = false;
        isArmed = false;
        sprinklerOn = false;
        isStop = false;
    }

    public FireState(boolean hasAlarm, boolean isArmed, boolean sprinklerOn, boolean isStop) {
        this.hasAlarm = hasAlarm;
        this.isArmed = isArmed;
        this.sprinklerOn = sprinklerOn;
        this.isStop = isStop;
    }

    public synchronized boolean getHasAlarm() {
        return hasAlarm;
    }

    public synchronized boolean getIsArmed() {
        return isArmed;
    }

    public synchronized boolean getSprinklerOn() {
        return sprinklerOn;
    }

    public synchronized boolean getIsStop() {
        return isStop;
    }

    public synchronized void setHasAlarm(boolean hasAlarm) {
        this.hasAlarm = hasAlarm;
    }

    public synchronized void setIsArmed(boolean armed) {
        isArmed = armed;
    }

    public synchronized void setSprinklerOn(boolean sprinklerOn) {
        this.sprinklerOn = sprinklerOn;
    }

    public synchronized void setIsStop(boolean stop) {
        isStop = stop;
    }
}
