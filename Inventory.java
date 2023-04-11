package com.zybooks.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "inventory.db";
    private static Inventory instance;

    private Inventory(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    public static Inventory getInstance(Context context) {
        if (instance == null) {
            instance = new Inventory(context);
        }
        return instance;
    }

    private static final class InventoryTable {
        private static final String TABLE = "inventory";
        private static final String COL_USERNAME = "username";
        private static final String COL_ID = "_id";
        private static final String COL_ITEM = "item";
        private static final String COL_DESCRIPTION = "description";
        private static final String COL_QUANTITY = "quantity";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + InventoryTable.TABLE + "(" +
                InventoryTable.COL_ID + " integer primary key autoincrement," +
                InventoryTable.COL_ITEM + " text," +
                InventoryTable.COL_DESCRIPTION + " text," +
                InventoryTable.COL_QUANTITY + " text," +
                InventoryTable.COL_USERNAME + " text);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + InventoryTable.TABLE);
        onCreate(db);
    }

    public List<Item> getItems(AuthenticatedUser user) {
        List<Item> items = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + InventoryTable.TABLE + " WHERE " +
                InventoryTable.COL_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{user.getUserName()});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String description = cursor.getString(2);
                String quantity = cursor.getString(3);
                Item item = new Item(id, name, description, quantity);
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return items;
    }

    public Item getItem(int itemId) {
        Item item = null;

        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + InventoryTable.TABLE + " WHERE " + InventoryTable.COL_ID +
                " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{Long.toString(itemId)});

        if (cursor.moveToFirst()) {

            String name = cursor.getString(1);
            String description = cursor.getString(2);
            String quantity = cursor.getString(3);
            item = new Item(itemId, name, description, quantity);
        }

        cursor.close();

        return item;
    }

    public int addItem(Item item, AuthenticatedUser user) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryTable.COL_QUANTITY, item.getQuantity());
        values.put(InventoryTable.COL_ITEM, item.getName());
        values.put(InventoryTable.COL_DESCRIPTION, item.getDescription());
        values.put(InventoryTable.COL_USERNAME, user.getUserName());
        int newId = (int) db.insert(InventoryTable.TABLE, null, values);

        return newId;
    }

    public boolean editItem(int id, Item item) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryTable.COL_ITEM, item.getName());
        values.put(InventoryTable.COL_DESCRIPTION, item.getDescription());
        values.put(InventoryTable.COL_QUANTITY, item.getQuantity());

        int result = db.update(InventoryTable.TABLE, values, InventoryTable.COL_ID +
                " = " + id, null);

        return result == 1;
    }

    public boolean deleteItem(int id) {
        SQLiteDatabase db = getWritableDatabase();

        int result = db.delete(InventoryTable.TABLE, InventoryTable.COL_ID + " = " + id,
                null);

        return result == 1;
    }

    public void clearTable() {
        SQLiteDatabase db = getWritableDatabase();
        String user = AuthenticatedUserManager.getInstance().getUser().getUserName();
        db.execSQL("DELETE FROM " + InventoryTable.TABLE + " Where " + InventoryTable.COL_USERNAME +
                " = '" + user + "';");
    }

}