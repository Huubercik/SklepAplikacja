package com.example.zamowienia.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.zamowienia.Logowanie.LoginActivity;
import com.example.zamowienia.MainActivity;
import com.example.zamowienia.Orders.OrderActivity;
import com.example.zamowienia.R;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupToolbar(int toolbarId) {
        Toolbar toolbar = findViewById(toolbarId);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.order_list:
                Intent intent = new Intent(this, OrderActivity.class);
                startActivity(intent);
                return true;
            case R.id.send_by_sms:
                sendBySms();
                Toast.makeText(this, "Wysłano SMS", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.share:
                shareInfo();
                return true;
            case R.id.change_language:
                Toast.makeText(this, "Zmieniono jezyk", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.about:
                showAboutDialog();
                Toast.makeText(this, "O programie", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout:
                LogOutUser();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendBySms() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String lastOrderDetails = sharedPreferences.getString("lastOrderDetails", "Brak ostatniego zamówienia.");
        int lastOrderTotalPrice = sharedPreferences.getInt("lastOrderTotalPrice", 0);
        String lastOrderDate = sharedPreferences.getString("lastOrderDate", "Nieznana data");

        String smsContent = "Ostatnie zamówienie:\n" + lastOrderDetails + "\nCena całkowita: " + lastOrderTotalPrice + " zł\nData zamówienia: " + lastOrderDate;


        Intent smsIntent = new Intent(Intent.ACTION_SENDTO );
        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.putExtra("sms_body", smsContent);

        try {
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Nie mozna otworzyc apliacji do wysylania sms" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void shareInfo(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String lastOrderDetails = sharedPreferences.getString("lastOrderDetails", "Brak ostatniego zamówienia.");
        int lastOrderTotalPrice = sharedPreferences.getInt("lastOrderTotalPrice", 0);
        String lastOrderDate = sharedPreferences.getString("lastOrderDate", "Nieznana data");

        String shareContent = "Ostatnie zamówienie:\n" + lastOrderDetails + "\nCena całkowita: " + lastOrderTotalPrice + " zł\nData zamówienia: " + lastOrderDate;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }



    public void showAboutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("O programie")
                .setMessage("Aplikacja zamowienia jako projekt na Programowanie Apliacji Mobilnych\nAutor:Hubert Tkaczyk")
                .setPositiveButton("OK", null)
                .setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void LogOutUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Wylogowano pomyślnie", Toast.LENGTH_SHORT).show();
    }
}
