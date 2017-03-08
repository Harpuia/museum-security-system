package SystemB;

/**
 * A thread-safe data structure shared among threads.
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

    /**
     * When the state of sprinkler gets changed,
     * the system sets sprinklerChanged to be true.
     */
    public synchronized void setSprinklerOn(boolean sprinklerOn) {
        this.sprinklerOn = sprinklerOn;
        sprinklerChanged = true;
    }

    public synchronized void setIsStop(boolean stop) {
        isStop = stop;
    }
}
