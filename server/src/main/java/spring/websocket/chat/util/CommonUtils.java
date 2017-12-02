package spring.websocket.chat.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class that provides all common static java utility
 * methods.
 */
public final class CommonUtils {

    private static final DateFormat DATE_FORMAT_HH_mm = new SimpleDateFormat("HH:mm");

    public static String getCurrentTimeStamp() {
        return DATE_FORMAT_HH_mm.format(new Date());
    }
}
