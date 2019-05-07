package de.trackcat;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.security.AccessController.getContext;

public class GlobalFunctions {


    /* get string date from millis */
    public static String getDateFromMillis(long millis, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }

    /* get string date from millis */
    public static String getDateFromSeconds(long seconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(seconds * 1000);
        return formatter.format(calendar.getTime());
    }

    /* get string date from millis */
    public static String getDateWithTimeFromSeconds(long seconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(seconds * 1000);
        return formatter.format(calendar.getTime());
    }

    /* get millis from string date */
    public static long getSecondsFromString(String str_date, String dateFormat) throws ParseException {

        DateFormat formatter = new SimpleDateFormat(dateFormat);
        Date date = (Date) formatter.parse(str_date);
        return date.getTime() / 1000;
    }

    /* get millis from string date */
    public static long getMillisFromString(String str_date, String dateFormat) throws ParseException {

        DateFormat formatter = new SimpleDateFormat(dateFormat);
        Date date = (Date) formatter.parse(str_date);
        return date.getTime();
    }


    /* function to parse an byte to an Base64 String */
    public static String getBase64FromBytes(byte[] bytes) {
        String data = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
        return data;
    }

    /* function to parse an byte to an Base64 String */
    public static byte[] getBytesFromBase64(String base64) {
        byte[] data = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
        return data;
    }

    /* function to set style of editText */
    public static void setNoInformationStyle(TextView t) {
        t.setTextColor(Color.LTGRAY);
        t.setText(MainActivity.getInstance().getResources().getString(R.string.noInformation));
        t.setTypeface(null, Typeface.ITALIC);
    }

    /* function to set style of editText */
    public static void resetNoInformationStyle(TextView t, int oldColor) {

        t.setTextColor(oldColor);
        t.setTypeface(null, Typeface.NORMAL);
    }

    public static long getTimeStamp() {

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        Log.d("LALALALAL", "Time : " + ts);
        return tsLong;
    }


    public static boolean validatePassword(TextView passwordTextView){

        boolean valid= true;
        String password = passwordTextView.getText().toString();

        /* validate password */
        Pattern pattern2 = Pattern.compile(MainActivity.getInstance().getResources().getString(R.string.rPassword));
        Matcher matcher2 = pattern2.matcher(password);

        if (!matcher2.matches()) {
            passwordTextView.setError(MainActivity.getInstance().getResources().getString(R.string.errorMsgPassword));
            Toast.makeText(MainActivity.getInstance().getApplicationContext(), MainActivity.getInstance().getString(R.string.tErrorPassword), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            passwordTextView.setError(null);
        }
        return valid;
    }

    public static boolean validateName(TextView nameTextView, Activity activity){

        boolean valid= true;
        String input_name = nameTextView.getText().toString();

        /* validate firstName */
        Pattern pattern3 = Pattern.compile(activity.getResources().getString(R.string.rName));
        Matcher matcher3 = pattern3.matcher(input_name);
        if (!matcher3.matches()) {
            nameTextView.setError(activity.getResources().getString(R.string.errorMsgName));
            Toast.makeText(StartActivity.getInstance().getApplicationContext(), activity.getResources().getString(R.string.tErrorName), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            nameTextView.setError(null);
        }
        return valid;
    }


}
