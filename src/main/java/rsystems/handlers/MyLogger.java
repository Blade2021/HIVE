package rsystems.handlers;

import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class MyLogger {
    static private FileHandler fileTxt;
    private static SimpleFormatter format;
    private static String loggerName;

    static public void setup() throws IOException {

        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        Logger.getLogger("KarmaLogger");
        logger.setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();

        format = new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()
                );
            }
        };

        handler.setFormatter(format);
        logger.addHandler(handler);

        logger.setLevel(Level.INFO);
        fileTxt = new FileHandler("logfile.log", true);

        // create a TXT formatter
        fileTxt.setFormatter(format);
        logger.addHandler(fileTxt);
    }

    static public void setup(String logName) throws IOException {
        loggerName = logName;
        Logger logger = Logger.getLogger(logName);
        logger.setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();

        format = new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()
                );
            }
        };

        handler.setFormatter(format);
        logger.addHandler(handler);

        logger.setLevel(Level.INFO);
        fileTxt = new FileHandler(logName + ".log", true);


        // create a TXT formatter
        fileTxt.setFormatter(format);
        logger.addHandler(fileTxt);
    }
}