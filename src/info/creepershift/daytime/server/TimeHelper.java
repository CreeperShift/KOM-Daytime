package info.creepershift.daytime.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 */
public final class TimeHelper {

    private TimeHelper(){}


    public static String getCurrentTime(){

        LocalDateTime date = LocalDateTime.now();

        DateTimeFormatter dateFormat = DateTimeFormatter.ISO_DATE_TIME;

        return date.format(dateFormat);
    }


}
