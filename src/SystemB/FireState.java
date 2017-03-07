package SystemB;

/**
 * Created by Yuchao on 04/03/2017.
 */
public class FireState {
    private boolean hasAlarm;
    private boolean sprinklerOn;
    private boolean isStop;
    public boolean sprinklerChanged = false;

    public FireState() {
        hasAlarm = false;
        sprinklerOn = false;
        isStop = false;
    }

    public synchronized boolean getHasAlarm() {
        return hasAlarm;
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

    public synchronized void setSprinklerOn(boolean sprinklerOn) {
        this.sprinklerOn = sprinklerOn;
        sprinklerChanged = true;
    }

    public synchronized void setIsStop(boolean stop) {
        isStop = stop;
    }
}
