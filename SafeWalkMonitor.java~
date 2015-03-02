/**
 * Start up for SafeWalk Monitor program.
 * 
 * @author dhelia
 * @author jtk
 */

import javax.swing.SwingUtilities;

public class SafeWalkMonitor implements Runnable {
    String host;
    int port;

    /**
     * The main method, starts the SafeWalkMonitor
     * 
     * @param args
     */
    public static void main(String[] args) {
	Log.setupLogging("Monitor", true);
	
	String host;
	int port;
	
	if (args.length == 0) {
	    host = "pc.cs.purdue.edu";
	    port = 1337;
	} else {
	    host = args[0];
	    port = Integer.parseInt(args[1]);
	}

	// Pass args to new SafeWalkMonitor instance running on Event Dispatch Thread...
	SwingUtilities.invokeLater(new SafeWalkMonitor(host, port));
    }

    public SafeWalkMonitor(String host, int port) {
	this.host = host;
	this.port = port;
    }

    /**
     * Run on the EDT, creating model, view, and controller.
     */
    public void run() {
	Model model = new Model();
	View view = new View(model);

	new Controller(model, view, host, port);
    }
}