package Extension;

import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;

/**
 * Created by yazid on 03-Mar-17.
 */
public abstract class MaintainableDevice implements Runnable{

    private boolean isStarted=false;
    private int msgID;
    private String text;
    //Inherit this in every device you use
    public MaintainableDevice(){
        //TODO Yazid: fill this
    }

    //After registering to the message manager, call this method
    //The message format is "name:description"
    //Message ID = 3
    public void SendAliveSignal(String name, String description, MessageManagerInterface em)
    {
        Message msg=new Message(3,name+" : "+description);
        try {
            if (!isStarted) {
                while (true) {
                    em.SendMessage(msg);
                    isStarted=true;
                    Thread.sleep(5000);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void run()
    {

    }
}
