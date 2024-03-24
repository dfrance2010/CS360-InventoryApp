package com.zybooks.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabase extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "users.db";
    private static UserDatabase instance;


    private UserDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public static UserDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new UserDatabase(context);
        }
        return instance;
    }

    private static final class UserTable {
        private static final String TABLE = "users";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + UserTable.TABLE + "(" +
                UserTable.COL_ID + " integer primary key autoincrement," +
                UserTable.COL_USERNAME + " text," +
                UserTable.COL_PASSWORD + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + UserTable.TABLE);
        onCreate(db);
    }

    /**
     * Add new user to database
     * @param user - username and password of user to add
     * @return - id of added user or -1 if user not added
     */
    public long addUser(UserCredentials user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserTable.COL_USERNAME, user.getUsername());
        values.put(UserTable.COL_PASSWORD, user.getPassword());
        long newId = db.insert(UserTable.TABLE, null, values);
        return newId;
    }

    /**
     * Check if username already exists in database
     * @param user - username to check
     * @return - true if username exists, false if not
     */
    public boolean checkUserName(String user) {
        boolean isUser = false;

        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + UserTable.TABLE;

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(1);
                if (user.equals(username)) {
                    isUser = true;
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return isUser;
    }

    /**
     * Check if password for given user is correct
     * @param user - username and password to check
     * @return - true if password is correct for username, false if not
     */
    public boolean checkPassword(UserCredentials user) {
        boolean correctPassword = false;

        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + UserTable.TABLE + " WHERE "
                + UserTable.COL_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{user.getUsername()});
        cursor.moveToFirst();
        String password = cursor.getString(2);
        if (user.getPassword().equals(password)) {
            correctPassword = true;
        }

        cursor.close();

        return correctPassword;
    }

}
