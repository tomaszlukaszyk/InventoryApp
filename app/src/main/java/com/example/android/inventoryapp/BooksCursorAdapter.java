package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.BooksEntry;

public class BooksCursorAdapter extends CursorAdapter {

    private int quantity;

    BooksCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find the views
        TextView nameTextView = view.findViewById(R.id.list_title);
        TextView quantityTextView = view.findViewById(R.id.list_stock);
        TextView priceTextView = view.findViewById(R.id.list_price);

        // Find index number of columns of interest
        int nameColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_NAME);
        final int quantityColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRICE);

        // Extract values from cursor
        String name = cursor.getString(nameColumnIndex);
        quantity = cursor.getInt(quantityColumnIndex);
        int price = cursor.getInt(priceColumnIndex);

        // Set the values to the view
        nameTextView.setText(name);
        quantityTextView.setText(Integer.toString(quantity));
        priceTextView.setText(context.getString(R.string.list_price_text, price));

        // Set the color of the background of in stock view according to quantity value
        LinearLayout inStock = view.findViewById(R.id.in_stock);
        int backgroundColor = getBackgroundColor(context, quantity);
        inStock.setBackgroundColor(backgroundColor);

        // Set onClick action on the button
        final Button sale = view.findViewById(R.id.sale_button);
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Find the position of the clicked button
                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);

                // Move cursors to position of button
                cursor.moveToPosition(position);

                // Get the quantity value from cursor at current position
                quantity = cursor.getInt(quantityColumnIndex);

                if (quantity == 0) {
                    Toast.makeText(context, "Minimum quantity value reached", Toast.LENGTH_LONG).show();
                } else {
                    // Decrease quantity value
                    quantity -= 1;

                    // Update the new vale in the database
                    long id = cursor.getLong(cursor.getColumnIndex(BooksEntry._ID));
                    Uri currentUri = ContentUris.withAppendedId(BooksEntry.CONTENT_URI, id);
                    ContentValues values = new ContentValues();
                    values.put(BooksEntry.COLUMN_QUANTITY, quantity);
                    context.getContentResolver().update(currentUri, values, null, null);
                }
            }
        });
    }

    private int getBackgroundColor(Context context, int quantity) {
        int colorResourceId = R.color.quantity_out;
        if (quantity > 80) {
            colorResourceId = R.color.quantity_very_high;
        } else if (quantity > 60) {
            colorResourceId = R.color.quantity_high;
        } else if (quantity > 30) {
            colorResourceId = R.color.quantity_normal;
        } else if (quantity > 10) {
            colorResourceId = R.color.quantity_low;
        } else if (quantity > 0) {
            colorResourceId = R.color.quantity_very_low;
        }
        return ContextCompat.getColor(context, colorResourceId);
    }
}