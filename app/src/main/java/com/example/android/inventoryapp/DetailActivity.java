package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.BooksEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_CURSOR_LOADER = 0;

    private Uri currentUri;

    private int quantity;
    private String phone;

    @BindView(R.id.detail_title)
    TextView nameTextView;
    @BindView(R.id.details_price)
    TextView priceTextView;
    @BindView(R.id.details_stock)
    TextView quantityTextView;
    @BindView(R.id.details_supplier)
    TextView supplierTextView;
    @BindView(R.id.details_phone)
    TextView phoneTextView;

    @OnClick({R.id.plus_button, R.id.minus_button})
    public void changeValue(View view) {
        switch (view.getId()) {
            case R.id.plus_button:
                increment();
                break;
            case R.id.minus_button:
                decrement();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        currentUri = intent.getData();

        getLoaderManager().initLoader(EXISTING_CURSOR_LOADER, null, this);
    }

    private void increment() {
        if (quantity == 100) {
            Toast.makeText(this, "Maximum quantity value reached", Toast.LENGTH_LONG).show();
        } else {
            quantity += 1;
            quantityTextView.setText(Integer.toString(quantity));
        }
    }

    private void decrement() {
        if (quantity == 0) {
            Toast.makeText(this, "Minimum quantity value reached", Toast.LENGTH_LONG).show();
        } else {
            quantity -= 1;
            quantityTextView.setText(Integer.toString(quantity));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                currentUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            // Find index number of columns of interest
            int nameColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract values from cursor
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            phone = cursor.getString(phoneColumnIndex);

            // Set the values to the view
            nameTextView.setText(name);
            priceTextView.setText(getString(R.string.detail_price_text, price));
            quantityTextView.setText(Integer.toString(quantity));
            supplierTextView.setText(supplier);
            phoneTextView.setText(phone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameTextView.setText("");
        priceTextView.setText("");
        quantityTextView.setText("");
        supplierTextView.setText("");
        priceTextView.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_call_supplier:
                Intent call = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(call);
                return true;
            case R.id.action_edit_entry:
                Intent intent = new Intent(DetailActivity.this, EditorActivity.class);
                intent.setData(currentUri);
                startActivity(intent);
                return true;
            case R.id.action_delete_entry:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                updateQuantity();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        updateQuantity();
        finish();
    }

    private void updateQuantity() {
        ContentValues values = new ContentValues();
        values.put(BooksEntry.COLUMN_QUANTITY, quantity);
        getContentResolver().update(currentUri, values, null, null);
    }

    private void deleteEntry() {
        int rowsDeleted = getContentResolver().delete(currentUri, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, "Error deleting entry", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the entry
                deleteEntry();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}