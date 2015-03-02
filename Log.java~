/**
 * A simple log interface.
 * 
 * @author jtk
 */

import java.io.IOException;
import java.text.DateFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Log {
    private static Logger logger;
    
    public static void setupLogging(String name, Boolean console) {
	LogManager.getLogManager().reset();
	logger = Logger.getLogger(name);
	Logger loggerParent = logger.getParent(); // get root or global logger (why is not clear to me)
	loggerParent.setLevel(Level.INFO);

	Handler handler = null;
	if (console)
	    handler = new ConsoleHandler();
	else {
	    try {
		handler = new FileHandler("trace.log", 10000000, 20);
	    } catch (IOException e) {
		throw new RuntimeException(e);
	    }
	}

	Formatter formatter = new Formatter() {
	    @Override
	    public String format(LogRecord logRecord) {
		final StringBuffer sb = new StringBuffer();
		sb.setLength(0);
		sb.append(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(
			logRecord.getMillis()));
		sb.append(": ");
		sb.append(logRecord.getLevel().toString());
		sb.append(" - ");
		sb.append(logRecord.getMessage());
		sb.append(" (");
		sb.append(logRecord.getLoggerName()); 
		sb.append(")\n");
		return sb.toString();
	    }
	};
	handler.setFormatter(formatter);
	loggerParent.addHandler(handler);
	logger.info("Logging configuration complete");
    }
    
    public static void i(String message, Object... parameters) {
	logger.info(String.format(message, parameters));
    }

    public static void w(String message, Object... parameters) {
	logger.info(String.format(message, parameters));
    }

    public static void e(String message, Object... parameters) {
	logger.info(String.format(message, parameters));
    }
}
