package de.trackcat.Settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.Recording.RecordFragment;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        /* Einstellungen aus XML Datei laden */
        addPreferencesFromResource(R.xml.fragment_settings);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /* Benutzerspezifischen Einstellungen laden */
        CheckBoxPreference help_messages = (CheckBoxPreference) findPreference("help_messages");
        help_messages.setChecked(MainActivity.getHints());
        SwitchPreference theme = (SwitchPreference) findPreference("dark_theme");
        theme.setChecked(MainActivity.getDarkTheme());

        /* Deaktiviere Themewechsel bei laufender Aufzeichnung */
        if (RecordFragment.isTracking()) {
            theme.setEnabled(false);
            theme.setSummary("Während Aufzeichnung nicht möglich!");
        }

        /* Export von Daten */
        Preference export = findPreference("global_export_options");
        export.setOnPreferenceClickListener(this);

        /* Aktuelle Version in Einstellungen anzeigen */
        Preference version = findPreference("current_version");
        version.setSummary("unknown");
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version.setSummary(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version.setOnPreferenceClickListener(this);

        /* Feedback senden */
        Preference feedback = findPreference("send_feedback");
        feedback.setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //unregister the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        /* Data Access Object (DAO) */
        UserDAO dao = new UserDAO(getActivity());
        User currentUser = dao.read(MainActivity.getActiveUser());

        /* Wechsel beim Anzeigen der Hilfreichen Tipps */
        if (preference.getKey().equals("help_messages")) {
            if (((CheckBoxPreference) preference).isChecked()) {
                Toast.makeText(getActivity(), "Hilfreiche Tipps aktiviert!", Toast.LENGTH_LONG).show();
                /* Nutzer aktualisieren */
                currentUser.setHintsActive(true);
                MainActivity.setHints(true);

            } else {
                if (MainActivity.getHints()) {
                    Toast.makeText(getActivity(), "Hilfreiche Tipps deaktiviert!", Toast.LENGTH_LONG).show();
                }
                /* Nutzer aktualisieren */
                currentUser.setHintsActive(false);
                MainActivity.setHints(false);
            }
        }
        /* Wechsel des Themes */
        else if (preference.getKey().equals("dark_theme")) {

            MainActivity.isActiv = false;

            if (((SwitchPreference) preference).isChecked()) {
                if (MainActivity.getHints()) {
                    Toast.makeText(getActivity(), "DarkTheme aktiviert!", Toast.LENGTH_LONG).show();
                }

                /* Nutzer aktualisieren */
                currentUser.setDarkThemeActive(true);
                MainActivity.setDarkTheme(true);

            } else {
                if (MainActivity.getHints()) {
                    Toast.makeText(getActivity(), "LightTheme aktiviert!", Toast.LENGTH_LONG).show();
                }

                /* Nutzer aktualisieren */
                currentUser.setDarkThemeActive(false);
                MainActivity.setDarkTheme(false);
            }
            // Restart Activity
            MainActivity.restart();

        } else {
            if (MainActivity.getHints()) {
                Toast.makeText(getActivity(), "Unbekannte Aktion ausgeführt!", Toast.LENGTH_LONG).show();
            }
        }
        dao.update(MainActivity.getActiveUser(), currentUser);
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
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
            alert.setTitle(getResources().getString(R.string.app_name)+" v" + version);
            alert.setMessage("Entwickler:\nTimo Kramer, Marie Fock, Finn Lenz, Yannik Petersen, Kristoff Klan, Jenö Petsch");
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
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@trackcat.de"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback: Trackcat");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, "Wählen Sie Ihren E-Mail Client"));
    }
}
