package Extension;

import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;

/**
 * Created by yazid on 03-Mar-17.
 */
public abstract class MaintainableDevice {

    //private boolean isStarted = false;
    private int msgID;
    private String text;

    //After registering to the message manager, call this method
    //The message format is "name:description"
    //Message ID = 3
    public static void SendAliveSignal(String name, String description, MessageManagerInterface em) {
        AliveThread aliveThread = new AliveThread(name, description, em);
        aliveThread.start();
    }

    public static class AliveThread extends Thread {
        private String name;
        private String description;
        private MessageManagerInterface em;

        public AliveThread(String name, String description, MessageManagerInterface em){
            this.name = name;
            this.description = description;
            this.em = em;
        }

        public void run() {
            boolean isStarted = false;
            Message msg = new Message(3, name + " : " + description);
            try {
                if (!isStarted) {
                    while (true) {
                        em.SendMessage(msg);
                        isStarted = true;
                        Thread.sleep(5000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}