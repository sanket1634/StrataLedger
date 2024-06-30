package com.example.strataledger;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StrataLedger.db";
    private static final int DATABASE_VERSION = 2; // Incremented version number

    // User table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Invoice table
    private static final String TABLE_INVOICES = "invoices";
    private static final String COLUMN_INVOICE_ID = "invoice_id";
    private static final String COLUMN_INVOICE_NUMBER = "invoice_number";
    private static final String COLUMN_INVOICE_DATE = "invoice_date";
    private static final String COLUMN_CLIENT_NAME = "client_name";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_INVOICES_TABLE = "CREATE TABLE " + TABLE_INVOICES + "("
                + COLUMN_INVOICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_INVOICE_NUMBER + " TEXT,"
                + COLUMN_INVOICE_DATE + " TEXT,"
                + COLUMN_CLIENT_NAME + " TEXT,"
                + COLUMN_AMOUNT + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_INVOICES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVOICES);
        onCreate(db);
    }

    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }

    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_USERNAME + "=?", new String[]{username},
                null, null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();

        return exists;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return cursorCount > 0;
    }

    public boolean addInvoice(String invoiceNumber, String invoiceDate, String clientName, String amount, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INVOICE_NUMBER, invoiceNumber);
        values.put(COLUMN_INVOICE_DATE, invoiceDate);
        values.put(COLUMN_CLIENT_NAME, clientName);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DESCRIPTION, description);

        long result = db.insert(TABLE_INVOICES, null, values);
        db.close();

        return result != -1;
    }
}
