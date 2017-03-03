package Extension;

/**
 * Created by yazid on 03-Mar-17.
 */
public class SecurityConsole {
    private SecurityMonitor securityMonitor;
    //Monitor can call all console's public methods
    //Console can call all monitor's public methods
    public SecurityConsole(){
        securityMonitor = new SecurityMonitor(this);
    }

    public void Arm(){
        //TODO: code
    }
    public void Disarm(){
        //TODO: code
    }
}