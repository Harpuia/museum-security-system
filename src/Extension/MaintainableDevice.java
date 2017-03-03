package Extension;

/**
 * Created by yazid on 03-Mar-17.
 */
public abstract class MaintainableDevice {
    //Inherit this in every device you use
    public MaintainableDevice(){
        //TODO Yazid: fill this
    }
    //After registering to the message manager, call this method
    //The message format is "name:description"
    //Message ID = 3
    public void SendAliveSignal(long participantId, String name, String description){
        //TODO Yazid: fill this
    }
}
