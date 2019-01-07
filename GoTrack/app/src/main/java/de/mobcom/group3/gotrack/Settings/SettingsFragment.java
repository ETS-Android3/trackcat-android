package de.mobcom.group3.gotrack.Settings;

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
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import de.mobcom.group3.gotrack.Database.DAO.UserDAO;
import de.mobcom.group3.gotrack.Database.Models.User;
import de.mobcom.group3.gotrack.InExport.Export;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.Recording.RecordFragment;

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

        /* Aktuelle Version in Einstellungen anzeigen */
        Preference version = findPreference("current_version");
        version.setSummary("unknown");
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version.setSummary(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Preference feedback = findPreference("send_feedback");
        feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                sendFeedback(getActivity());
                return true;
            }
        });

        /* ClickListener für den Export von Daten */
        Preference export = findPreference("global_export_options");
        export.setOnPreferenceClickListener(this);
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
            Log.d("PREFERENCES", "Wechsel des Themes!");

            /* Aktuelles Fragment neustarten */
            FragmentTransaction fragTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, new SettingsFragment(), getResources().getString(R.string.fSettings));
            fragTransaction.commit();

            Bundle savedInstanceState = new Bundle();
            onSaveInstanceState(savedInstanceState);
            onDestroy();
            MainActivity.isActiv = false;
            onCreate(savedInstanceState);

            if (((SwitchPreference) preference).isChecked()) {
                if (MainActivity.getHints()) {
                    Toast.makeText(getActivity(), "DarkTheme aktiviert!", Toast.LENGTH_LONG).show();
                }
                Log.d("PREFERENCES", "DarkTheme aktiviert!");
                /* Nutzer aktualisieren */
                currentUser.setDarkThemeActive(true);
                MainActivity.setDarkTheme(true);

            } else {
                if (MainActivity.getHints()) {
                    Toast.makeText(getActivity(), "LightTheme aktiviert!", Toast.LENGTH_LONG).show();
                }
                Log.d("PREFERENCES", "LightTheme aktiviert!");
                /* Nutzer aktualisieren */
                currentUser.setDarkThemeActive(false);
                MainActivity.setDarkTheme(false);
            }
            // Restart Activity
            MainActivity.restart();

        } else {
            Log.d("PREFERENCES", "Unbekannte Aktion ausgeführt!");
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
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"timokramer1@me.com", "yannik-petersen92@t-online.de", "fock.marie@gmail.com", "finnlenz@outlook.de", "kristoff_klan@hotmail.de", "j.petsch95@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback: GoTrack");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, "Wählen Sie Ihren E-Mail Client"));
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Daten exportieren?");

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertView = inflater.inflate(R.layout.fragment_settings_export, null, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alert.setView(alertView);
        } else {
            alert.setMessage("Hier würden normalerweise die Daten Ihrer Aufzeichnung stehen.\nLeider ist Ihre Android-Version dafür zu alt!");
        }

        alert.setPositiveButton("Exportieren", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /* Data Access Object (DAO) */
                UserDAO dao = new UserDAO(getActivity());
                User currentUser = dao.read(MainActivity.getActiveUser());

                RadioButton rBtn_all_records = alertView.findViewById(R.id.rBtn_all_records);
                RadioButton rBtn_all_options = alertView.findViewById(R.id.rBtn_all_options);
                RadioButton rBtn_all_route = alertView.findViewById(R.id.rBtn_all_route);
                RadioButton rBtn_all_users = alertView.findViewById(R.id.rBtn_all_users);
                String type = "";

                /* Überprüfung, welche exportoption gewählt wurde */

                /* Alle Aufnahmen des aktuellen Nutzers exportieren */
                String fullName = currentUser.getFirstName() + " " + currentUser.getLastName();
                if (rBtn_all_records.isChecked()) {
                    type = "Gesamtes Profil \"" + fullName + "\" exportiert!";
                    Export.getExport().exportAllUserData(getActivity(), currentUser.getId(), true);

                /* Nutzer-Einstellung exportieren */
                } else if (rBtn_all_options.isChecked()) {
                    type = "Profileinstellungen von \"" + fullName + "\" exportiert!";
                    Export.getExport().exportUserData(getActivity(), currentUser.getId(), true);

                /* Alle Routen exportieren */
                } else if (rBtn_all_route.isChecked()) {
                    type = "Alle Routen von \"" + fullName + "\" exportiert!";
                    Export.getExport().exportAllRoute(getActivity(), currentUser.getId(), true);

                /* Alle Nutzer exportieren */
                } else if (rBtn_all_users.isChecked()) {
                    type = "App Backup erstellt!";
                    Export.getExport().exportAllRouteUsers(getActivity(), true);
                }

                if (MainActivity.getHints()) {
                    Toast.makeText(MainActivity.getInstance().getApplicationContext(), type, Toast.LENGTH_LONG).show();
                }
            }
        });

        alert.setNegativeButton("Abbruch", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
        return false;
    }
}
