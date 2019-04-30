package de.trackcat;

import android.os.Build;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class GlobalFunctions {


    /* get string date from millis */
    public static String getDateFromMillis(long millis, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }

    /* get millis from string date */
    public static long getMillisFromString(String str_date, String dateFormat) throws ParseException {

        DateFormat formatter = new SimpleDateFormat(dateFormat);
        Date date = (Date) formatter.parse(str_date);
        System.out.println("Today is " + date.getTime());
        return date.getTime();
    }

    public static String getBase64FromBytes(byte[] bytes){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(bytes);
        }else {
            return "";
        }
    }
}
