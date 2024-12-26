package com.example.zamowienia.Orders;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.zamowienia.BazaDanych.DatabaseHelper;
import com.example.zamowienia.R;

public class OrderProcessor {
    private final Context context;

    public OrderProcessor(Context context) {
        this.context = context;
    }

    public void processOrder(String orderDetails, int totalPrice, String customerName, String orderDate) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);

        if (userId != -1) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            databaseHelper.addOrder(userId, orderDetails, totalPrice, customerName, orderDate);
//            Toast.makeText(context, "Zamówienie zapisane w bazie danych", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.loginInfo, Toast.LENGTH_SHORT).show();
        }
    }
}

