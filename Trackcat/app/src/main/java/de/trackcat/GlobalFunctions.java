package de.trackcat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.widget.TextView;

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

    /* get string date from millis */
    public static String getDateWithTimeFromSeconds(long seconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(seconds*1000);
        return formatter.format(calendar.getTime());
    }

    /* get millis from string date */
    public static long getMillisFromString(String str_date, String dateFormat) throws ParseException {

        DateFormat formatter = new SimpleDateFormat(dateFormat);
        Date date = (Date) formatter.parse(str_date);
        return date.getTime();
    }

    /* function to parse an byte to an Base64 String */
    public static String getBase64FromBytes(byte[] bytes){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(bytes);
        }else {
            return "";
        }
    }

    /* function to set style of editText */
    public static void setNoInformationStyle(TextView t) {
        t.setTextColor(Color.LTGRAY);
        t.setTypeface(null, Typeface.ITALIC);
    }

    /* function to set style of editText */
    public static void resetNoInformationStyle(TextView t, int oldColor) {

        t.setTextColor(oldColor);
        t.setTypeface(null, Typeface.NORMAL);
    }
}
