package Extension;

/**
 * Created by yazid on 03-Mar-17.
 */
public class SecurityConsole {
    private SecurityMonitor securityMonitor;
    private boolean done;
    public void Arm(){
        //TODO: code
    }
    public void Disarm(){
        //TODO: code
    }

    protected void main(String[] args) {
        securityMonitor = new SecurityMonitor(this);
        if (securityMonitor.isRegistered()) {
            Thread monitorThread = new Thread(securityMonitor);
            monitorThread.start();
            while (!done) {
                //TODO: implement logic here
                System.out.println("Choose something from 1 to 5:");
                //Scan input
                //input to int
                if(true/*input can be parsed as int*/){
                int i=0;
                switch(i){
                    case 1:
                        //Arm
                        securityMonitor.arm();
                        break;
                    case 2:
                        //do something
                        break;
                    default:
                        //Display "Error, choose a value in 1-5"
                        break;
                }
                }
                else{
                    System.out.println("Error, choose a value in 1-5");
                }
            }
        }
    }
}