package de.mobcom.group3.gotrack.Settings;

import android.content.Context;
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
import android.widget.Toast;
import de.mobcom.group3.gotrack.Database.DAO.UserDAO;
import de.mobcom.group3.gotrack.Database.Models.User;
import de.mobcom.group3.gotrack.InExport.Export;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
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
        //theme.setChecked(MainActivity.getDarkTheme());

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
        User oldUser = dao.read(MainActivity.getActiveUser());

        /* Wechsel beim Anzeigen der Hilfreichen Tipps */
        if (preference.getKey().equals("help_messages")) {
            if (((CheckBoxPreference) preference).isChecked()) {
                Toast.makeText(getActivity(), "Hilfreiche Tipps aktiviert!", Toast.LENGTH_LONG).show();
                /* Nutzer aktualisieren */
                oldUser.setHintsActive(true);
                MainActivity.setHints(true);

            } else {
                if (MainActivity.getHints()) {
                    Toast.makeText(getActivity(), "Hilfreiche Tipps deaktiviert!", Toast.LENGTH_LONG).show();
                }
                /* Nutzer aktualisieren */
                oldUser.setHintsActive(false);
                MainActivity.setHints(false);
            }
        }
        /* Wechsel des Themes */
        else if (preference.getKey().equals("dark_theme")) {
            Log.d("PREFERENCES", "Wechsel des Themes!");
            /* getActivity().finish();
            final Intent intent = getActivity().getIntent();
            intent.putExtra("action", getResources().getString(R.string.fSettings));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().startActivity(intent); */

            /* Theme wechseln */
            getActivity().setTheme(android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false) ? R.style.AppTheme_Dark : R.style.AppTheme);

            /* Aktuelles Fragment neustarten */
            FragmentTransaction fragTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, new SettingsFragment(), getResources().getString(R.string.fSettings));
            fragTransaction.commit();

            // TODO: Voll funktionierendes Beenden und Neuerstellen der vorherigen Instanz
            Bundle savedInstanceState = new Bundle();
            onSaveInstanceState(savedInstanceState);
            onDestroy();
            onCreate(savedInstanceState);

            //getActivity().recreate();
            if (((SwitchPreference) preference).isChecked()) {
                if (MainActivity.getHints()) {
                    Toast.makeText(getActivity(), "DarkTheme aktiviert!", Toast.LENGTH_LONG).show();
                }
                Log.d("PREFERENCES", "DarkTheme aktiviert!");
                /* Nutzer aktualisieren */
                oldUser.setDarkThemeActive(true);
                MainActivity.setDarkTheme(true);

            } else {
                if (MainActivity.getHints()) {
                    Toast.makeText(getActivity(), "LightTheme aktiviert!", Toast.LENGTH_LONG).show();
                }
                Log.d("PREFERENCES", "LightTheme aktiviert!");
                /* Nutzer aktualisieren */
                oldUser.setDarkThemeActive(false);
                MainActivity.setDarkTheme(false);
            }
        } else if (preference.getKey().equals("global_export_options")){
            String value = ((ListPreference) preference).getValue();
            /* Alle Aufnahmen des aktuellen Nutzers exportieren */
            if (value.equals(getActivity().getResources().getStringArray(R.array.export_options)[0])){
                Toast.makeText(getContext(), "Exportiere alle Aufzeichnungen von \"" + oldUser.getFirstName() + " " + oldUser.getLastName() + "\"!", Toast.LENGTH_LONG).show();
                Export.getExport().exportAllUserData(getActivity(), oldUser.getId(), true);

                // TODO: Exportieren aller Aufzeichnungen des derzeit aktiven Nutzers
                /**
                 * Die ID des aktuellen Nutzers kann mithilfe der in der MainActivity
                 * liegenden Methode getActiveUser() ermittelt werden!
                 */
            }
            /* Nutzer-Einstellung exportieren */
            else if (value.equals(getActivity().getResources().getStringArray(R.array.export_options)[1])){
                Toast.makeText(getContext(), "Exportiere Nutzer-Einstellungen!", Toast.LENGTH_LONG).show();
                Export.getExport().exportUserData(getActivity(), oldUser.getId(), true);
                // TODO: Exportieren aller Nutzer
                /**
                 * Hier würde es am meisten Sinn machen, die Nutzer mit ihren
                 * Aufzeichnungen zu exportieren, quasi einen DB-Snapshot!
                 */
            }
            /* Alle Nutzer exportieren */
            else if (value.equals(getActivity().getResources().getStringArray(R.array.export_options)[2])){
                Toast.makeText(getContext(), "Exportiere alle Nutzer!", Toast.LENGTH_LONG).show();
                // TODO: Exportieren aller Nutzer
                /**
                 * Hier würde es am meisten Sinn machen, die Nutzer mit ihren
                 * Aufzeichnungen zu exportieren, quasi einen DB-Snapshot!
                 */
            }
        } else {
            Log.d("PREFERENCES", "Unbekannte Aktion ausgeführt!");
        }
        dao.update(MainActivity.getActiveUser(), oldUser);
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
            body = "\n\n-----------------------------\nDevice OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@androidhive.info"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from android app");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, "Wählen Sie Ihren E-Mail Client"));
    }
}
