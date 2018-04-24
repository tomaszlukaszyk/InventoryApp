package com.example.android.inventoryapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.Data.InventoryContract.booksEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "books.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + booksEntry.TABLE_NAME + " ("
                + booksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + booksEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + booksEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + booksEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + booksEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + booksEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
