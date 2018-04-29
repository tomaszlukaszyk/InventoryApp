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
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.BooksEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_CURSOR_LOADER = 0;

    private Uri currentUri;

    private boolean entryHasChanged = false;

    @BindView(R.id.editor_title)
    EditText titleEditText;
    @BindView(R.id.editor_price)
    EditText priceEditText;
    @BindView(R.id.editor_quantity)
    EditText quantityEditText;
    @BindView(R.id.editor_supplier)
    EditText supplierEditText;
    @BindView(R.id.editor_phone)
    EditText phoneEditText;

    @OnTouch({R.id.editor_title, R.id.editor_price, R.id.editor_quantity, R.id.editor_supplier, R.id.editor_phone})
    public boolean changeTouchStatus() {
        entryHasChanged = true;
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        currentUri = intent.getData();

        if (currentUri == null) {
            setTitle(R.string.editor_add);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.editor_update);
            getLoaderManager().initLoader(EXISTING_CURSOR_LOADER, null, this);
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
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);

            // Set the values to the view
            titleEditText.setText(name);
            priceEditText.setText(Integer.toString(price));
            quantityEditText.setText(Integer.toString(quantity));
            supplierEditText.setText(supplier);
            phoneEditText.setText(phone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        titleEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierEditText.setText("");
        phoneEditText.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveEntry();
                return true;
            case android.R.id.home:
                if (!entryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveEntry() {
        String title = titleEditText.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "You must specify the title of the book", Toast.LENGTH_SHORT).show();
            return;
        }
        int price = -1;
        String priceString = priceEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        if (price <= 0) {
            Toast.makeText(this, "You must provide price larger than 0", Toast.LENGTH_SHORT).show();
            return;
        }
        int quantity = 0;
        String quantityString = quantityEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        if (quantity < 0 || quantity > 100) {
            Toast.makeText(this, "You must provide quantity larger than 0 and smaller than 100", Toast.LENGTH_SHORT).show();
            return;
        }
        String supplier = supplierEditText.getText().toString().trim();
        if (TextUtils.isEmpty(supplier)) {
            Toast.makeText(this, "You must specify the name of the supplier", Toast.LENGTH_SHORT).show();
            return;
        }
        String phone = phoneEditText.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "You must specify the phone number of the supplier", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BooksEntry.COLUMN_PRODUCT_NAME, title);
        values.put(BooksEntry.COLUMN_PRICE, price);
        values.put(BooksEntry.COLUMN_QUANTITY, quantity);
        values.put(BooksEntry.COLUMN_SUPPLIER_NAME, supplier);
        values.put(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER, phone);

        if (currentUri == null) {
            Uri newUri = getContentResolver().insert(BooksEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Error saving entry", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Entry saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsUpdated = getContentResolver().update(currentUri, values, null, null);

            if (rowsUpdated == 0) {
                Toast.makeText(this, "Error updating entry", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Entry updated", Toast.LENGTH_SHORT).show();
            }
        }
        NavUtils.navigateUpFromSameTask(EditorActivity.this);
    }

    @Override
    public void onBackPressed() {
        if (!entryHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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