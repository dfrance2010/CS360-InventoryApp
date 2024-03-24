package com.zybooks.inventoryapp;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationsManager {

    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;
    private static final String INFO = "notification_prefs";
    private static NotificationsManager instance;

    private String username;

    // Singleton to allow only one NotificationsManager
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

    /**
     * Set username of current user
     * @param username - username to be set
     */
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * Save preference for notifications - true = notifications on, false = notifications off
     * @param isEnabled - current state of notifications
     */
    public void saveNotificationPreference(boolean isEnabled) {
        editor.putBoolean(username, isEnabled);
        editor.apply();
    }

    /**
     * Return notification preference of current user
     * @return - sharedPref of user
     */
    public boolean getNotificationPreference() {
        return sharedPref.getBoolean(username, false);
    }

}
