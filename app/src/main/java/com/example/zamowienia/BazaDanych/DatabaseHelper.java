package com.example.zamowienia.BazaDanych;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.zamowienia.Orders.Order;
import com.example.zamowienia.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Context context;

    // Nazwa bazy danych oraz tabeli
    public static final String DATABASE_NAME = "user.db";
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE_USERS = "users";

    // Kolumny tabeli users
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // Kolumny do tabeli zamowien
    private static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_ORDER_ID = "order_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_ORDER_ITEMS = "order_items";
    public static final String COLUMN_TOTAL_PRICE = "total_price";
    public static final String COLUMN_ORDER_DATE = "order_date";
    public static final String COLUMN_CUSTOMER_NAME = "customer_name";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL" +
                    ");";

    private static final String CREATE_TABLE_ORDERS =
            "CREATE TABLE " + TABLE_ORDERS + " (" +
                    COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_ORDER_ITEMS + " TEXT NOT NULL, " +
                    COLUMN_TOTAL_PRICE + " INTEGER, " +
                    COLUMN_ORDER_DATE + " TEXT NOT NULL, " +
                    COLUMN_CUSTOMER_NAME + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_ORDERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN " + COLUMN_ORDER_DATE + " TEXT NOT NULL DEFAULT 'N/A'");
            db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN " + COLUMN_CUSTOMER_NAME + " TEXT NOT NULL DEFAULT 'Anonymous'");
        }
    }

    public void addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USERNAME + " = ? AND " +
                COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public void addOrder(int userId, String orderItems, int totalPrice, String customerName, String orderDate) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_ORDER_ITEMS, orderItems);
            values.put(COLUMN_TOTAL_PRICE, totalPrice);
            values.put(COLUMN_CUSTOMER_NAME, customerName);
            values.put(COLUMN_ORDER_DATE, orderDate);

            db.insert(TABLE_ORDERS, null, values);
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error inserting order", e);
        }
    }

    public List<Order> getOrdersForUser(int userId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_ORDER_ITEMS + ", " + COLUMN_TOTAL_PRICE + ", " +
                COLUMN_CUSTOMER_NAME + ", " + COLUMN_ORDER_DATE +
                " FROM " + TABLE_ORDERS +
                " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            int orderNumber = 1;
            do {
                String orderItems = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ITEMS));
                int totalPrice = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_PRICE));
                String customerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_NAME));
                String orderDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE));

                String orderText = context.getString(R.string.order_number,
                        orderNumber, orderItems, customerName, orderDate);
                orders.add(new Order(orderText, totalPrice));
                orderNumber++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return orders;
    }
}