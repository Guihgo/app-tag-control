package com.guihgo.tagcontrol.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "main.db";
    private static final int DATABASE_VERSION = 1;

    /* TAG */
    private static final String TABLE_CREATE_TAG =
            "CREATE TABLE " + TagContract.TagEntry.TABLE_NAME + " (" +
                    TagContract.TagEntry.COLUMN_NAME_ID + " VARCHAR PRIMARY KEY," +
                    TagContract.TagEntry.COLUMN_NAME_NAME + " VARCHAR," +
                    TagContract.TagEntry.COLUMN_NAME_DESCRIPTION + " TEXT);";
    public static final String TABLE_DROP_TAG = "DROP TABLE IF EXISTS " + TagContract.TagEntry.TABLE_NAME + ";";

    /* INVENTORY */
    private static final String TABLE_CREATE_INVENTORY =
            "CREATE TABLE " + InventoryContract.InventoryEntry.TABLE_NAME + " (" +
                    InventoryContract.InventoryEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    InventoryContract.InventoryEntry.COLUMN_NAME_TAG_ID + " VARCHAR NOT NULL," +
                    InventoryContract.InventoryEntry.COLUMN_NAME_EXPIRATION + " INTEGER," +
                    InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY + " TEXT," +
                    "FOREIGN KEY (" + InventoryContract.InventoryEntry.COLUMN_NAME_TAG_ID + ") REFERENCES " + TagContract.TagEntry.TABLE_NAME + "(" + TagContract.TagEntry.COLUMN_NAME_ID + ") );";
    public static final String TABLE_DROP_INVENTORY = "DROP TABLE IF EXISTS " + InventoryContract.InventoryEntry.TABLE_NAME + ";";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_TAG);
        db.execSQL(TABLE_CREATE_INVENTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TABLE_DROP_TAG);
        db.execSQL(TABLE_DROP_INVENTORY);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
