/**
 * Project 8
 * @author desai38
 * @author pahlawat
 */

/**
 * Start up for SafeWalk Client program.
 * 
 * @author dhelia
 * @author jtk
 */

import javax.swing.SwingUtilities;

public class SafeWalkClient implements Runnable {
    String host;
    int port;
    String key;
    String nickname;
    
    /**
     * The main method, starts the SafeWalkClient
     * 
     * @param args
     */
    public static void main(String[] args) {
        Log.setupLogging("hemil10", true);
        
        String host;
        int port;
        String key;
        String nickname;
        
        if (args.length == 0) {
            host = "pc.cs.purdue.edu";
            port = 1337;
            key = "k065386";
            nickname = "hemil10";
        } else {
            host = args[0];
            port = Integer.parseInt(args[1]);
            key = args[2];
            nickname = args[3];
        } 
        
        //SafeWalkClient swc = new SafeWalkClient(host, port, key, nickname);
        
        //swc.run();
        
        
        
        // Pass args to new SafeWalkMonitor instance running on Event Dispatch Thread...
        SwingUtilities.invokeLater(new SafeWalkClient(host, port, key, nickname));
    }
    
    public SafeWalkClient(String host, int port, String key, String nickname) {
        this.host = host;
        this.port = port;
        this.key = key;
        this.nickname = nickname;
    }
    
    /**
     * Run on the EDT, creating model, view, and controller.
     */
    public void run() {
        Model model = new Model();
        //View view = new View(model);
        
        new Controller(model, host, port);
    }
}