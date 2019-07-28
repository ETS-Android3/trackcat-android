package de.trackcat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.trackcat.Database.DAO.RecordTempDAO;
import de.trackcat.Database.DAO.RouteDAO;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.Route;
import de.trackcat.Database.Models.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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

        Date date = new java.util.Date(seconds * 1000);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat(dateFormat);
        String formattedDate = sdf.format(date);

        return formattedDate;
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
        return tsLong;
    }


    public static boolean validatePassword(TextView passwordTextView, Activity activity) {

        boolean valid = true;
        String password = passwordTextView.getText().toString();

        /* validate password */
        Pattern pattern2 = Pattern.compile(activity.getResources().getString(R.string.rPassword));
        Matcher matcher2 = pattern2.matcher(password);

        if (!matcher2.matches()) {
            passwordTextView.setError(activity.getResources().getString(R.string.errorMsgPassword));
            Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.tErrorPassword), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            passwordTextView.setError(null);
        }
        return valid;
    }

    public static boolean validateName(TextView nameTextView, Activity activity) {

        boolean valid = true;
        String input_name = nameTextView.getText().toString();

        /* validate name */
        Pattern pattern3 = Pattern.compile(activity.getResources().getString(R.string.rName));
        Matcher matcher3 = pattern3.matcher(input_name);
        if (!matcher3.matches()) {
            nameTextView.setError(activity.getResources().getString(R.string.errorMsgName));
            Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.tErrorName), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            nameTextView.setError(null);
        }
        return valid;
    }

    public static boolean validateEMail(TextView emailTextView, Activity activity) {
        boolean valid = true;

        /* read inputs */
        String input_email = emailTextView.getText().toString();

        /* validate email */
        Pattern pattern = Pattern.compile(activity.getResources().getString(R.string.rEmail));
        Matcher matcher = pattern.matcher(input_email);

        if (!matcher.matches()) {
            emailTextView.setError(activity.getResources().getString(R.string.errorMsgEMail));
            Toast.makeText(StartActivity.getInstance().getApplicationContext(), activity.getResources().getString(R.string.tErrorEmail), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            emailTextView.setError(null);
        }

        return valid;
    }

    public static boolean validateTwoPassword(TextView password1TextView, TextView password2TextView, Activity activity) {
        boolean valid = true;

        /* read inputs */
        String input_password1 = password1TextView.getText().toString();

        /* validate password */
        Pattern pattern2 = Pattern.compile(activity.getResources().getString(R.string.rPassword));
        Matcher matcher2 = pattern2.matcher(input_password1);

        if (!matcher2.matches()) {
            password1TextView.setError(activity.getResources().getString(R.string.errorMsgPassword));
            password2TextView.setError(activity.getResources().getString(R.string.errorMsgPassword));
            Toast.makeText(StartActivity.getInstance().getApplicationContext(), activity.getResources().getString(R.string.tErrorPassword), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            password1TextView.setError(null);
            password2TextView.setError(null);
        }
        return valid;
    }

    public static Bitmap findLevel(double distance) {
        int result = 0;
        Bitmap bitmap = null;
        if (distance < 5) {
            bitmap = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.raw.lvl1);
        } else if (distance >= 5 && distance < 20) {
            bitmap = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.raw.lvl2);
        } else if (distance >= 20 && distance < 40) {
            bitmap = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.raw.lvl3);
        } else if (distance >= 40 && distance < 80) {
            bitmap = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.raw.lvl4);
        } else if (distance >= 80) {
            bitmap = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.raw.lvl5);
        }
        return bitmap;
    }

    public static User createUser(JSONObject userObject, boolean updateDrawer) throws JSONException {
        User user = new User();
        user.setId(userObject.getInt("id"));
        user.setMail(userObject.getString("email"));
        user.setFirstName(userObject.getString("firstName"));
        user.setLastName(userObject.getString("lastName"));
        if (userObject.getString("image") != "null") {
            user.setImage(GlobalFunctions.getBytesFromBase64(userObject.getString("image")));
        }
        user.setGender(userObject.getInt("gender"));

        if (userObject.getInt("darkTheme") == 0) {
            user.setDarkThemeActive(false);
        } else {
            user.setDarkThemeActive(true);
        }

        if (userObject.getInt("hints") == 0) {
            user.setHintsActive(false);
        } else {
            user.setHintsActive(true);
        }

        try {
            user.setDateOfRegistration(userObject.getLong("dateOfRegistration"));
        } catch (Exception e) {
        }

        try {
            user.setLastLogin(userObject.getLong("lastLogin"));
        } catch (Exception e) {
        }

        try {
            user.setWeight((float) userObject.getDouble("weight"));
        } catch (Exception e) {
        }

        try {
            user.setSize((float) userObject.getDouble("size"));
        } catch (Exception e) {
        }
        try {
            user.setDateOfBirth(userObject.getLong("dateOfBirth"));
        } catch (Exception e) {
        }

        try {
            user.setTotalDistance(userObject.getLong("totalDistance"));
        } catch (Exception e) {
        }

        try {
            user.setTotalTime(userObject.getLong("totalTime"));
        } catch (Exception e) {
        }

        try {
            user.setAmountRecord(userObject.getLong("amountRecords"));
        } catch (Exception e) {
        }

        user.setPassword(userObject.getString("password"));
        user.setTimeStamp(userObject.getLong("timeStamp"));
        user.isSynchronised(true);

        /* update drawer */
        if(updateDrawer) {
            MainActivity.getInstance().setDrawerInfromation(user.getImage(), user.getFirstName(), user.getLastName(), user.getMail());
        }

        return user;
    }

    public static void createRecords(JSONArray recordsArray, Activity activity) throws JSONException {
        RouteDAO recordDao = new RouteDAO(activity);
        for (int i = 0; i < recordsArray.length(); i++) {
            Route record = new Route();
            record.setId(((JSONObject) recordsArray.get(i)).getInt("id"));
            record.setName(((JSONObject) recordsArray.get(i)).getString("name"));
            record.setTime(((JSONObject) recordsArray.get(i)).getLong("time"));
            record.setDate(((JSONObject) recordsArray.get(i)).getLong("date"));
            record.setType(((JSONObject) recordsArray.get(i)).getInt("type"));
            record.setRideTime(((JSONObject) recordsArray.get(i)).getInt("ridetime"));
            record.setDistance(((JSONObject) recordsArray.get(i)).getDouble("distance"));
            record.setTimeStamp(((JSONObject) recordsArray.get(i)).getLong("timestamp"));
            record.setTemp(false);
            record.setLocations(((JSONObject) recordsArray.get(i)).getString("locations"));
            recordDao.create(record);
        }
    }

    public static void deleteAllTempRecord(ClosingService activity, int currentUserId) {



        /* delete temp from device */
        RecordTempDAO recordTempDAO = new RecordTempDAO(activity);
        recordTempDAO.deleteAllNotFinished();

        /* delete possible live tracking from server */
        UserDAO userDAO = new UserDAO(activity);
        User currentUser = userDAO.read(currentUserId);
        Log.d("GESCHLOSSEN","Alles Gelöscht");

        Log.d("GLOBALFUNCTIONS", ""+ currentUser.getFirstName());

        /* Start a call */
        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);
        String base = currentUser.getMail() + ":" + currentUser.getPassword();

        Log.d("GLOBALFUNCTIONS", base);

        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        Call<ResponseBody> call = apiInterface.abortLiveRecord(authString);
        Log.d("GESCHLOSSEN","base: "+base);
        call.enqueue(new Callback<ResponseBody>() {

        Log.d("GLOBALFUNCTIONS", "BEFORESEND");



        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    try {
                        Response<ResponseBody> execute = call.execute();
                        Log.d("GLOBALFUNCTIONS", execute.toString());

                    }catch (Exception e){
                        Log.d("GLOBALFUNCTIONS", e.toString());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();




//        call.enqueue(new Callback<ResponseBody>() {
//
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.d("GESCHLOSSEN","Antwort");
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.d("GESCHLOSSEN","AntwortFail");
//                call.cancel();
//            }
//        });
    }
}
