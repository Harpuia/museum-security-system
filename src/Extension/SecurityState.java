package Extension;

/**
 * Created by Yuchao on 04/03/2017.
 */
public class SecurityState {
    private boolean hasAlarm;
    private boolean isArmed;
    private boolean sprinklerOn;

    public SecurityState() {
        hasAlarm = false;
        isArmed = false;
        sprinklerOn = false;
    }

    public SecurityState(boolean hasAlarm, boolean isArmed, boolean sprinklerOn) {
        this.hasAlarm = hasAlarm;
        this.isArmed = isArmed;
        this.sprinklerOn = sprinklerOn;
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

    public synchronized void setHasAlarm(boolean hasAlarm) {
        this.hasAlarm = hasAlarm;
    }

    public synchronized void setIsArmed(boolean armed) {
        isArmed = armed;
    }

    public synchronized void setSprinklerOn(boolean sprinklerOn) {
        this.sprinklerOn = sprinklerOn;
    }
}
