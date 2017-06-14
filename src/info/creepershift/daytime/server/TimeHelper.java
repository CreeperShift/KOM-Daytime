package info.creepershift.daytime.server;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 */
public final class TimeHelper {

    private TimeHelper() {
    }

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("E, dd/MM/yyyy");
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    /*
    Returns the current time, date and timezone.
     */
    public static String getDate() {

        ZonedDateTime date = ZonedDateTime.now();

        return "Hello, it's " + date.format(dateFormat) + ". The time is " + date.format(timeFormat) +
                ". Your timezone is " + date.format(DateTimeFormatter.ofPattern("VV, O."));
    }

    /*
    Returns time for console
     */
    public static String getTime() {

        ZonedDateTime date = ZonedDateTime.now();

        return "[" + date.format(timeFormat) + "] ";
    }

}
