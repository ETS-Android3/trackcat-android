package de.trackcat.Settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.*;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.Recording.RecordFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        /* Load settings from xml */
        addPreferencesFromResource(R.xml.fragment_settings);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /* Load user settings */
        CheckBoxPreference help_messages = (CheckBoxPreference) findPreference("help_messages");
        help_messages.setChecked(MainActivity.getHints());
        SwitchPreference theme = (SwitchPreference) findPreference("dark_theme");
        theme.setChecked(MainActivity.getDarkTheme());

        /* deactivate themechange by run tracking */
        if (RecordFragment.isTracking()) {
            theme.setEnabled(false);
            theme.setSummary("Während Aufzeichnung nicht möglich!");
        }

        /* Show current version */
        Preference version = findPreference("current_version");
        version.setSummary("unknown");
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version.setSummary(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version.setOnPreferenceClickListener(this);

        /* Send feedback */
        Preference feedback = findPreference("send_feedback");
        feedback.setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        /* Data Access Object (DAO) */
        UserDAO dao = new UserDAO(getActivity());
        User currentUser = dao.read(MainActivity.getActiveUser());

        /* Change data in global db */
        HashMap<String, String> map = new HashMap<>();

        /* Help */
        if (preference.getKey().equals("help_messages")) {
            if (((CheckBoxPreference) preference).isChecked()) {
                Toast.makeText(getActivity(), "Hilfreiche Tipps aktiviert!", Toast.LENGTH_LONG).show();

                /* Update user */
                currentUser.setHintsActive(true);
                MainActivity.setHints(true);
                map.put("hints", "1");

            } else {
                if (MainActivity.getHints()) {
                    Toast.makeText(getActivity(), "Hilfreiche Tipps deaktiviert!", Toast.LENGTH_LONG).show();
                }
                /* Update user */
                currentUser.setHintsActive(false);
                MainActivity.setHints(false);
                map.put("hints", "0");

            }
        }
        /* Change themes */
        else if (preference.getKey().equals("dark_theme")) {

            MainActivity.isActiv = false;

            if (((SwitchPreference) preference).isChecked()) {
                if (MainActivity.getHints()) {
                    Toast.makeText(getActivity(), "DarkTheme aktiviert!", Toast.LENGTH_LONG).show();
                }

                /* Update user */
                currentUser.setDarkThemeActive(true);
                MainActivity.setDarkTheme(true);
                map.put("darkTheme", "1");

            } else {
                if (MainActivity.getHints()) {
                    Toast.makeText(getActivity(), "LightTheme aktiviert!", Toast.LENGTH_LONG).show();
                }

                /* Update user */
                currentUser.setDarkThemeActive(false);
                MainActivity.setDarkTheme(false);
                map.put("darkTheme", "0");
            }
            /* Restart Activity */
            MainActivity.restart();

        } else {
            if (MainActivity.getHints()) {
                Toast.makeText(getActivity(), "Unbekannte Aktion ausgeführt!", Toast.LENGTH_LONG).show();
            }
        }

        /* Update user in local db */
        currentUser.setTimeStamp(GlobalFunctions.getTimeStamp());
        dao.update(MainActivity.getActiveUser(), currentUser);

        /* Change values in global DB*/
        map.put("timeStamp", "" + GlobalFunctions.getTimeStamp());

        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);

        /* Start a call */
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        Call<ResponseBody> call = apiInterface.updateUser(authString, map);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    /* Get jsonString from API */
                    String jsonString = response.body().string();

                    /* Parse json */
                    JSONObject successJSON = new JSONObject(jsonString);

                    if (successJSON.getString("success").equals("0")) {

                        /* Save is Synchronized value as true */
                        dao.update(currentUser.getId(), currentUser);
                    }

                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("current_version")) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

            String version = "\"unknown\"";
            try {
                PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            alert.setTitle(getResources().getString(R.string.app_name) + " v" + version);


            alert.setMessage("vom 01.09.2019\n\nEntwickler:\nTimo Kramer, Marie Fock, Finn Lenz, Yannik Petersen, Kristoff Klan, Jenö Petsch");
            alert.setNegativeButton("Schließen", null);
            alert.show();
        } else if (preference.getKey().equals("send_feedback")) {
            sendFeedback(getActivity());
        }
        return false;
    }

    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n------------- SYSTEM INFORMATION -------------\nDevice OS: Android \nDevice OS version: " +
                    Build.VERSION.RELEASE + "\nApp Version: " + body + "\nDevice Brand: " + Build.BRAND +
                    "\nDevice Model: " + Build.MODEL + "\nDevice Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@timoskramkiste.de"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback: Trackcat");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, "Wählen Sie Ihren E-Mail Client"));
    }
}
