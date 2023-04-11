package com.zybooks.inventoryapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
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

            EditTextPreference title = findPreference("title");
            title.setText(username);

            SwitchPreferenceCompat notifyPref = findPreference("notifications");
            if (notifyPref != null) {
                notifyPref.setOnPreferenceChangeListener((preference, newValue) -> {

                    if ((Boolean) newValue) {
                        notifyManager.saveNotificationPreference(true);
                        Toast.makeText(getContext(), "Notifications on", Toast.LENGTH_SHORT).show();
                    } else {
                        notifyManager.saveNotificationPreference(false);
                        Toast.makeText(getContext(), "Notifications off", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                });
            }

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