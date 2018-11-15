package com.example.finnl.gotrack.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.*;
import android.util.Log;
import android.widget.Toast;

import com.example.finnl.gotrack.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.fragment_settings);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
        // Wechsel des Themes
        if (preference instanceof SwitchPreference) {
            Log.d("PREFERENCES", "Wechsel des Themes!");
            /* getActivity().finish();
            final Intent intent = getActivity().getIntent();
            intent.putExtra("action", "SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().startActivity(intent); */

            getActivity().recreate();
            if (preference.isEnabled()) {
                Toast.makeText(getActivity(), "DarkTheme aktiviert!", Toast.LENGTH_LONG).show();
                Log.d("PREFERENCES", "DarkTheme aktiviert!");
            } else {
                Toast.makeText(getActivity(), "LightTheme aktiviert!", Toast.LENGTH_LONG).show();
                Log.d("PREFERENCES", "LightTheme aktiviert!");
            }
        } else {
            Log.d("PREFERENCES", "Unbekannte Aktion ausgeführt!");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
