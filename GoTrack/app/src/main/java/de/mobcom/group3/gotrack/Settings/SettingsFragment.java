package de.mobcom.group3.gotrack.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.*;
import android.util.Log;
import android.widget.Toast;

import de.mobcom.group3.gotrack.Database.DAO.UserDAO;
import de.mobcom.group3.gotrack.Database.Models.User;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.fragment_settings);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        /*Anzeigen der Benutzerspezifischen Einstellungen*/
        CheckBoxPreference help_messages = (CheckBoxPreference)findPreference("help_messages");
        help_messages.setChecked(MainActivity.getHints());
        SwitchPreference theme = (SwitchPreference)findPreference("dark_theme");
//        theme.setChecked(MainActivity.getDarkTheme());

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
        if (preference instanceof CheckBoxPreference) {

            if (((CheckBoxPreference) preference).isChecked()) {
                Toast.makeText(getActivity(), "Hilfreiche Tipps aktiviert!", Toast.LENGTH_LONG).show();
                /* Nutzer aktualisieren */
                oldUser.setHintsActive(true);
                MainActivity.setHints(true);

            } else {
                Toast.makeText(getActivity(), "Hilfreiche Tipps deaktiviert!", Toast.LENGTH_LONG).show();
                /* Nutzer aktualisieren */
                oldUser.setHintsActive(false);
                MainActivity.setHints(false);
            }


        }
        /* Wechsel des Themes */
        else if (preference instanceof SwitchPreference) {
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
                Toast.makeText(getActivity(), "DarkTheme aktiviert!", Toast.LENGTH_LONG).show();
                Log.d("PREFERENCES", "DarkTheme aktiviert!");
                /* Nutzer aktualisieren */
                oldUser.setDarkThemeActive(true);
                MainActivity.setDarkTheme(true);

            } else {
                Toast.makeText(getActivity(), "LightTheme aktiviert!", Toast.LENGTH_LONG).show();
                Log.d("PREFERENCES", "LightTheme aktiviert!");
                /* Nutzer aktualisieren */
                oldUser.setDarkThemeActive(false);
                MainActivity.setDarkTheme(false);
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
}
