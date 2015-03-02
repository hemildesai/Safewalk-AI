import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

/**
 * Maintain connection to the Rendezvous server.
 * 
 * @author jtk
 */
public class Connector extends Observable implements Runnable {
    private String host;
    private int port;
    private String connectString;

    private boolean closing = false;
    private Socket socket = null;
    private OutputStreamWriter outputStreamWriter;
    private BufferedReader bufferedReader;

    public Connector(String host, int port, String connectString, Observer observer) {
	this.host = host;
	this.port = port;
	this.connectString = connectString;

	addObserver(observer);

	new Thread(this).start();
    }

    private synchronized void open() {
	if (socket != null || closing)
	    return;
	try {
	    socket = new Socket(host, port);
	    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
	    Log.i("Established connection to server at (%s, %s)", host, port);

	    if (connectString != null) {
		outputStreamWriter.write(connectString);
		outputStreamWriter.write("\n");
		outputStreamWriter.flush();
	    }
	} catch (IOException e) {
	    socket = null;
	}
    }

    public void writeLine(String message, Object... parameters) {
	String s = String.format(message, parameters);
	writeLine(s);
    }

    public void writeLine(String s) {
	open();
	try {
	    outputStreamWriter.write(s);
	    outputStreamWriter.write("\n");
	    outputStreamWriter.flush();
	    Log.i("SENT: %s", s);
	} catch (Exception e) {
	    socket = null;
	}
    }

    public void run() {
	while (!closing) {
	    String line = null;
	    open();
	    try {
		line = bufferedReader.readLine();
		setChanged();
		notifyObservers(line);
	    } catch (Exception e) {
		socket = null;
		if (!closing) {
		    Log.w("OPEN OR READ FAILED %s: sleeping for 5 seconds (closing = %b)", e, closing);
		    Log.w("Stack trace... '%s'", e.getStackTrace()[0]);
		    sleep(5000);
		}
	    }
	}
    }

    synchronized public void close(boolean stayClosed) {
	Log.i("Closing down Connector; stayClosed = %b", stayClosed);
	closing = stayClosed;
	try {
	    if (outputStreamWriter != null)
		outputStreamWriter.close();
	    if (bufferedReader != null)
		bufferedReader.close();
	    if (socket != null)
		socket.close();
	} catch (IOException e) {
	    // ignore
	}
	socket = null;
    }

    private void sleep(int n) {
	try {
	    Thread.sleep(n);
	} catch (InterruptedException e) {
	    throw new RuntimeException(e);
	}
    }
}
