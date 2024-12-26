package com.example.zamowienia.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import java.util.Locale;

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
                return true;
            case R.id.share:
                shareInfo();
                return true;
            case R.id.change_language:
                showLanguageDialog();
                return true;
            case R.id.about:
                showAboutDialog();
                return true;
            case R.id.logout:
                LogOutUser();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showLanguageDialog() {
        final String[] languages = {"Polski", "English"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wybierz język / Choose language")
                .setItems(languages, (dialog, which) -> {
                    String selectedLocale = (which == 0) ? "pl" : "en";
                    changeLanguage(selectedLocale);
                });
        builder.create().show();
    }

    private void changeLanguage(String languageCode) {

        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Language", languageCode);
        editor.apply();

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void sendBySms() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String lastOrderDetails = sharedPreferences.getString("lastOrderDetails", getString(R.string.baseLastOrder));
        int lastOrderTotalPrice = sharedPreferences.getInt("lastOrderTotalPrice", 0);
        String lastOrderDate = sharedPreferences.getString("lastOrderDate", getString(R.string.noDate));
        String lastOrderCustomerName = sharedPreferences.getString("lastOrderCustomerName", null);

        String smsContent = getString(R.string.lastOrder) + "\n" +
                lastOrderDetails + "\n" +
                getString(R.string.totalPrice) + ": " + lastOrderTotalPrice + " zł\n" +
                getString(R.string.orderDate) + ": " + lastOrderDate + "\n" +
                lastOrderCustomerName;

        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.putExtra("sms_body", smsContent);

        try {
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.smsError), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void shareInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String lastOrderDetails = sharedPreferences.getString("lastOrderDetails", getString(R.string.baseLastOrder));
        int lastOrderTotalPrice = sharedPreferences.getInt("lastOrderTotalPrice", 0);
        String lastOrderDate = sharedPreferences.getString("lastOrderDate", getString(R.string.noDate));
        String lastOrderCustomerName = sharedPreferences.getString("lastOrderCustomerName", null);

        String shareContent = getString(R.string.lastOrder) + "\n" +
                lastOrderDetails + "\n" +
                getString(R.string.totalPrice) + ": " + lastOrderTotalPrice + " zł\n" +
                getString(R.string.orderDate) + ": " + lastOrderDate + "\n" +
                lastOrderCustomerName;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }



    public void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.aboutApp))
                .setMessage(getString(R.string.aboutAppMessage) + "\n--Hubert Tkaczyk")
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
        Toast.makeText(this, R.string.logoutMessage, Toast.LENGTH_SHORT).show();
    }
}
