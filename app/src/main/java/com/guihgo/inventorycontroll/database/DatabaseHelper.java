package com.guihgo.inventorycontroll.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "main.db";
    private static final int DATABASE_VERSION = 2;


    private static final String TABLE_CREATE_TAG =
            "CREATE TABLE "+ TagContract.TagEntry.TABLE_NAME +" (" +
                TagContract.TagEntry.COLUMN_NAME_ID             +" VARCHAR PRIMARY KEY," +
                TagContract.TagEntry.COLUMN_NAME_NAME           + " VARCHAR," +
                TagContract.TagEntry.COLUMN_NAME_DESCRIPTION    + " TEXT);";

    public static final String TABLE_DROP_TAG = "DROP TABLE IF EXISTS "+TagContract.TagEntry.TABLE_NAME+";";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_TAG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TABLE_DROP_TAG);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
