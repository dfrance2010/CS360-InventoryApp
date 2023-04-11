package com.zybooks.inventoryapp;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationsManager {

    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;
    private static final String INFO = "notification_prefs";
    private static NotificationsManager instance;

    private String username;

    private NotificationsManager(Context context, String username) {
        this.username = username;
        sharedPref = context.getSharedPreferences(INFO, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.apply();
    }

    public static void initialize(Context context, String username) {
        if (instance == null) {
            instance = new NotificationsManager(context, username);
        }
    }

    public static NotificationsManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(("Notifications manager not initialized"));
        }
        return instance;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void saveNotificationPreference(boolean isEnabled) {
        editor.putBoolean(username, isEnabled);
        editor.apply();
    }

    public boolean getNotificationPreference() {
        return sharedPref.getBoolean(username, false);
    }

    public void removeNotificationPreference() {
        if (sharedPref.contains(username)) {
            editor.remove(username);
            editor.apply();
        }
    }
}
