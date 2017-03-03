package Extension;

/**
 * Created by yazid on 03-Mar-17.
 */
public class SecurityMonitor extends Thread {
    private SecurityConsole parentConsole;
    //Monitor can call all console's public methods
    //Console can call all monitor's public methods
    public SecurityMonitor(SecurityConsole parentConsole){
        this.parentConsole = parentConsole;
    }
}
