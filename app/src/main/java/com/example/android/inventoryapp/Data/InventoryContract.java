package com.example.android.inventoryapp.Data;

import android.provider.BaseColumns;

public final class InventoryContract {

    // Private constructor
    private InventoryContract() {
    }

    public static class booksEntry implements BaseColumns {

        public static final String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "phone";
    }
}
