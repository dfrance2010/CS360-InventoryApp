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

    // Singleton class to only allow one inventory database instance.
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

    /**
     * Get a list of all items in database related to current user
     * @param user - current user
     * @return - list of items for current user
     */
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

    /**
     * Get item from database using item's id
     * @param itemId - id of item to return
     * @return - Item object associated with item id
     */
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

    /**
     * Add item to database. If item name already exists in database, do not add item.
     * @param item - item to add
     * @param user - user associated with item
     * @return - new id of item added or -1 if item not added
     */
    public int addItem(Item item, AuthenticatedUser user) {
        // If item name exists for user, return without adding item.
        if (checkItem(item, user)) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryTable.COL_QUANTITY, item.getQuantity());
        values.put(InventoryTable.COL_ITEM, item.getName());
        values.put(InventoryTable.COL_DESCRIPTION, item.getDescription());
        values.put(InventoryTable.COL_USERNAME, user.getUserName());
        int newId = (int) db.insert(InventoryTable.TABLE, null, values);

        return newId;
    }

    /**
     * Check if an item name already exists in database for given user
     * @param item - item to check
     * @param user - user associated with item
     * @return - true if item name exists, false if item name does not exist
     */
    public boolean checkItem(Item item, AuthenticatedUser user) {
        boolean itemExists = false;

        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + InventoryTable.TABLE + " WHERE " +
                InventoryTable.COL_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{user.getUserName()});

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(1);
                if (item.getName().equals(name)) {
                    itemExists = true;
                    break;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        return itemExists;
    }

    /**
     * Edit item in database based on given item id
     * @param id - id of item to edit
     * @param item - updated item
     * @return - true if item has been edited, false if not
     */
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


    /**
     * Delete item from database based on given id
     * @param id - id of item to delete
     * @return - true if item deleted, false if not
     */
    public boolean deleteItem(int id) {
        SQLiteDatabase db = getWritableDatabase();

        int result = db.delete(InventoryTable.TABLE, InventoryTable.COL_ID + " = " + id,
                null);

        return result == 1;
    }


}