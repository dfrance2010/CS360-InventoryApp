package com.zybooks.inventoryapp;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            String username = AuthenticatedUserManager.getInstance().getUser().getUserName();

            NotificationsManager notifyManager = NotificationsManager.getInstance();
            notifyManager.setUsername(username);

            // Save notifications preference in NotificationsManager so it persists when app closes.
            SwitchPreferenceCompat notifyPref = findPreference("notifications");
            notifyPref.setChecked(notifyManager.getNotificationPreference());

            notifyPref.setOnPreferenceChangeListener((preference, newValue) -> {

                if ((Boolean) newValue) {
                    notifyManager.saveNotificationPreference(true);
                } else {
                    notifyManager.saveNotificationPreference(false);
                }

                    return true;
                });

            // Preference for dark mode.
            SwitchPreferenceCompat darkPref = findPreference("dark_theme");
            if (darkPref != null) {
                darkPref.setOnPreferenceChangeListener((preference, newValue) -> {

                    if ((Boolean) newValue) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }

                    return true;
                });
            }
        }
    }
}