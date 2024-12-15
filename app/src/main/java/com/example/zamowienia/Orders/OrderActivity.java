package com.example.zamowienia.Orders;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zamowienia.BazaDanych.DatabaseHelper;
import com.example.zamowienia.R;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private ListView listView;
    private OrderAdapter orderAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);

        listView = findViewById(R.id.list_view_orders);
        databaseHelper = new DatabaseHelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Nie jesteś zalogowany!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<Order> orders = databaseHelper.getOrdersForUser(userId);
        orderAdapter = new OrderAdapter(this, orders);
        listView.setAdapter(orderAdapter);
    }


}