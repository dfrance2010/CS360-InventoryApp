package com.zybooks.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

    public void clearTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + UserTable.TABLE);
    }

    public long addUser(UserCredentials user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserTable.COL_USERNAME, user.getUsername());
        values.put(UserTable.COL_PASSWORD, user.getPassword());
        long newId = db.insert(UserTable.TABLE, null, values);
        return newId;
    }

    public boolean checkUser(UserCredentials user) {
        boolean isUser = false;

        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + UserTable.TABLE;

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(1);
                String password = cursor.getString(2);
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    isUser = true;
                    break;
                };
            } while (cursor.moveToNext());
        }
        cursor.close();

        return isUser;
    }

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
                };
            } while (cursor.moveToNext());
        }
        cursor.close();

        return isUser;
    }

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

    public List<UserCredentials> getUsers() {
        List<UserCredentials> userList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + UserTable.TABLE;

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(1);
                String password = cursor.getString(2);
                UserCredentials user = new UserCredentials(username, password);
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return userList;
    }

}
