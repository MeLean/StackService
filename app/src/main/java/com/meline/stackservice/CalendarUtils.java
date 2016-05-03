package com.meline.stackservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//static class
public final class  CalendarUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy MM dd HH:mm:ss Z");

    private CalendarUtils() {
    }

    static Date addTimeToCurrentTime(int minutes){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    static String stringifyDateInFormat(Date date){
        return DATE_FORMAT.format(date);
    }

    static Date parseDateInFormat(String dateStr){
        Date result = null;
        try {
            result = DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    static Date getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }
}
