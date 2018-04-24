package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.InventoryContract.booksEntry;
import com.example.android.inventoryapp.Data.InventoryDbHelper;

public class MainActivity extends AppCompatActivity {

    private InventoryDbHelper inventoryDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inventoryDbHelper = new InventoryDbHelper(this);

        Button button = findViewById(R.id.test_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
                displayData();
            }
        });
    }

    private void insertData() {
        // Get the data repository in write mode
        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(booksEntry.COLUMN_PRODUCT_NAME, "The Shining");
        values.put(booksEntry.COLUMN_PRICE, 7);
        values.put(booksEntry.COLUMN_SUPPLIER_NAME, "Anchor");
        values.put(booksEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "(212) 940-7390");

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(booksEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving data", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Data saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    private Cursor queryData() {
        SQLiteDatabase db = inventoryDbHelper.getReadableDatabase();

        return db.query(booksEntry.TABLE_NAME, null, null, null, null, null, null);
    }

    private void displayData() {
        Cursor cursor = queryData();

        TextView displayView = findViewById(R.id.test_text);

        try {
            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(booksEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(booksEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(booksEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(booksEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(booksEntry.COLUMN_SUPPLIER_NAME);
            int phoneNumberColumnIndex = cursor.getColumnIndex(booksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            displayView.setText(R.string.test_title);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentPhoneNumber = cursor.getString(phoneNumberColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplier + " - " +
                        currentPhoneNumber));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
}
