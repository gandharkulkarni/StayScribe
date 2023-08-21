package hotelapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper {
    private static final Logger log = LogManager.getLogger();
    private LogHelper(){
    }
    public static Logger getLogger() {
        return log;
    }
}
