package com.by.communication.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Produced a lot of bug on 2017/4/6.
 */

public class TimeUtil {
    public final static String HOUR         = "HH";
    public final static String REGULAR_TIME = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE         = "yyyy-MM-dd";
    public final static String HOUR_MINUTE  = "HH:mm";

    public static String getCurrentTimeString()
    {
        SimpleDateFormat format = new SimpleDateFormat(REGULAR_TIME);
        return format.format(new Date(System.currentTimeMillis()));
    }

    public static String covertToLocalTime(String timestamp)
    {
        SimpleDateFormat format = new SimpleDateFormat(REGULAR_TIME);
        try {
            Date date = format.parse(timestamp);
            date.setTime(date.getTime() + 60 * 60 * 1000 * 8);
            timestamp = format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }
}
