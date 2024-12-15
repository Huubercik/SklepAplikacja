package com.example.zamowienia;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.zamowienia.BazaDanych.DatabaseHelper;
import com.example.zamowienia.Orders.OrderProcessor;
import com.example.zamowienia.Toolbar.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PCView extends BaseActivity implements AdapterView.OnItemSelectedListener {
    HashMap<String, Integer> myszkiCeny = new HashMap<>();
    HashMap<String, Integer> klawiaturyCeny = new HashMap<>();
    HashMap<String, Integer> sluchawkiCeny = new HashMap<>();
    String [] opisy_myszki = {
            "Wybierz...",
            "Razer DeathAdder V2, cena: 130 zl",
            "Logitech MX Master 3, cena: 420 zl",
            "SteelSeries Rival 600, cena: 340 zl "
    };
    String [] opisy_klawiatury = {
            "Wybierz...",
            "Corsair K95 RGB Platinum, cena: 840 zl",
            "Logitech G Pro X, cena: 630 zl",
            "Razer Huntsman Elite, cena: 1050 zl"
    };
    String [] opisy_sluchawki = {
            "Wybierz...",
            "Sony WH-1000XM5, cena: 1470 zl",
            "Bose QuietComfort 45, cena: 1400",
            "SteelSeries Arctis 7, cena: 630"
    };
    int [] myszki = {
            R.drawable.mysz_razer,
            R.drawable.mysz_logitech,
            R.drawable.mysz_steelseries
    };
    int [] klawiatury = {
            R.drawable.klawiatura_corsair,
            R.drawable.klawiatura_logitech,
            R.drawable.klawiatura_razer
    };
    int [] sluchawki = {
            R.drawable.sluchawki_sony,
            R.drawable.sluchawki_bose,
            R.drawable.sluchawki_steelseries
    };
    Spinner spinner_mysz;
    Spinner spinner_klawiatura;
    Spinner spinner_sluchawki;
    CheckBox checkBox_mysz;
    CheckBox checkBox_klawiatura;
    CheckBox checkBox_sluchawki;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcview_layout);
        setupToolbar(R.id.toolbar);
        
        Button btnOrder = findViewById(R.id.btn_order);
        btnOrder.setOnClickListener(v -> submitOrder());


        TextView pcName = findViewById(R.id.pc_name);
        ImageView picture = findViewById(R.id.picture);
        TextView description = findViewById(R.id.pc_description);
        TextView PCprice = findViewById(R.id.pc_price);

        EditText customerName = findViewById(R.id.customerName);

        checkBox_mysz = findViewById(R.id.chbox_myszka);
        checkBox_klawiatura = findViewById(R.id.chbox_klawiatura);
        checkBox_sluchawki = findViewById(R.id.chbox_sluchawki);

        Bundle bundle = getIntent().getExtras();

        assert bundle != null;
        picture.setImageResource(Integer.parseInt(bundle.getString("PICTURE_IMAGE")));
        pcName.setText(bundle.getString("PICTURE_NAME"));
        description.setText(bundle.getString("PICTURE_DESC"));
        PCprice.setText(bundle.getString("PICTURE_PRICE"));

//        -------- SPINNERY ---------
//        Te takie opcje/listy do wybory

        myszkiCeny.put("Razer DeathAdder V2", 126);
        myszkiCeny.put("Logitech MX Master 3", 420);
        myszkiCeny.put("SteelSeries Rival 600", 336);

        klawiaturyCeny.put("Corsair K95 RGB Platinum", 840);
        klawiaturyCeny.put("Logitech G Pro X", 630);
        klawiaturyCeny.put("Razer Huntsman Elite", 1050);

        sluchawkiCeny.put("Sony WH-1000XM5", 1470);
        sluchawkiCeny.put("Bose QuietComfort 45", 1386);
        sluchawkiCeny.put("SteelSeries Arctis 7", 630);

        spinner_mysz = findViewById(R.id.spinner_myszka);
        ArrayAdapter<String> arrayAdapterMyszka = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opisy_myszki);
        arrayAdapterMyszka.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_mysz.setAdapter(arrayAdapterMyszka);
        spinner_mysz.setOnItemSelectedListener(this);

        spinner_klawiatura = findViewById(R.id.spinner_klawiatura);
        ArrayAdapter<String> arrayAdapterKlawiatura = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opisy_klawiatury);
        arrayAdapterKlawiatura.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_klawiatura.setAdapter(arrayAdapterKlawiatura);
        spinner_klawiatura.setOnItemSelectedListener(this);

        spinner_sluchawki = findViewById(R.id.spinner_sluchawki);
        ArrayAdapter<String> arrayAdapterSluchawki = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opisy_sluchawki);
        arrayAdapterSluchawki.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sluchawki.setAdapter(arrayAdapterSluchawki);
        spinner_sluchawki.setOnItemSelectedListener(this);

        setupSpinner(spinner_mysz, opisy_myszki, myszki);
        setupSpinner(spinner_klawiatura, opisy_klawiatury, klawiatury);
        setupSpinner(spinner_sluchawki, opisy_sluchawki, sluchawki);

        checkBox_mysz.setOnCheckedChangeListener(((buttonView, isChecked) -> updateTotalPrice()));
        checkBox_klawiatura.setOnCheckedChangeListener(((buttonView, isChecked) -> updateTotalPrice()));
        checkBox_sluchawki.setOnCheckedChangeListener(((buttonView, isChecked) -> updateTotalPrice()));
    }

    private void submitOrder() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Nie jesteś zalogowany. Zaloguj się, aby złożyć zamówienie.", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText customerNameInput = findViewById(R.id.customerName);
        String customerName = customerNameInput.getText().toString().trim();

        if (customerName.isEmpty()) {
            Toast.makeText(this, "Proszę podać imię.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder orderDetails = new StringBuilder();
        int totalPrice = 0;

        TextView PCprice = findViewById(R.id.pc_price);
        String pcPriceText = PCprice.getText().toString().replaceAll("[^0-9]", "");
        if (!pcPriceText.isEmpty()) {
            totalPrice += Integer.parseInt(pcPriceText);
            orderDetails.append("PC Price: ").append(pcPriceText).append(" zł\n");
        }

        if (checkBox_mysz.isChecked()) {
            String selectedMouse = (String) spinner_mysz.getSelectedItem();
            selectedMouse = extractNameFromSpinnerText(selectedMouse);
            Integer mousePrice = myszkiCeny.get(selectedMouse);
            if (mousePrice != null) {
                totalPrice += mousePrice;
                orderDetails.append("Mouse: ").append(selectedMouse).append(" cena: ").append(mousePrice).append(" zł\n");
            }
        }

        if (checkBox_klawiatura.isChecked()) {
            String selectedKeyboard = (String) spinner_klawiatura.getSelectedItem();
            selectedKeyboard = extractNameFromSpinnerText(selectedKeyboard);
            Integer keyboardPrice = klawiaturyCeny.get(selectedKeyboard);
            if (keyboardPrice != null) {
                totalPrice += keyboardPrice;
                orderDetails.append("Keyboard: ").append(selectedKeyboard).append(" cena: ").append(keyboardPrice).append(" zł\n");
            }
        }

        if (checkBox_sluchawki.isChecked()) {
            String selectedHeadphones = (String) spinner_sluchawki.getSelectedItem();
            selectedHeadphones = extractNameFromSpinnerText(selectedHeadphones);
            Integer headphonesPrice = sluchawkiCeny.get(selectedHeadphones);
            if (headphonesPrice != null) {
                totalPrice += headphonesPrice;
                orderDetails.append("Headphones: ").append(selectedHeadphones).append(" cena: ").append(headphonesPrice).append(" zł\n");
            }
        }

        orderDetails.append("Total Price: ").append(totalPrice).append(" zł\n");

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        saveOrder(orderDetails.toString(), totalPrice, customerName, currentDate);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastOrderDetails", orderDetails.toString());
        editor.putInt("lastOrderTotalPrice", totalPrice);
        editor.putString("lastOrderDate", currentDate);
        editor.apply();

        resetOrderForm();

        Toast.makeText(this, "Zamówienie zostało złożone!", Toast.LENGTH_SHORT).show();
    }

    private void resetOrderForm() {
        TextView cenaTekst = findViewById(R.id.cena_tekst);
        cenaTekst.setText("Cena całkowita: 0 zł");

        checkBox_mysz.setChecked(false);
        checkBox_klawiatura.setChecked(false);
        checkBox_sluchawki.setChecked(false);

        spinner_mysz.setSelection(0);
        spinner_klawiatura.setSelection(0);
        spinner_sluchawki.setSelection(0);
    }

    private void saveOrder(String orderDetails, int totalPrice, String customerName, String orderDate) {
        OrderProcessor orderProcessor = new OrderProcessor(this);
        orderProcessor.processOrder(orderDetails, totalPrice, customerName, orderDate);
    }
    private void updateTotalPrice() {
        TextView PCprice = findViewById(R.id.pc_price);
        TextView cenaTekst = findViewById(R.id.cena_tekst);

        int totalPrice = 0;

        String pcPriceText = PCprice.getText().toString().replaceAll("[^0-9]", "");
        if (!pcPriceText.isEmpty()) {
            totalPrice += Integer.parseInt(pcPriceText);
        }
        if (checkBox_mysz.isChecked()) {
            String selectedMouse = (String) spinner_mysz.getSelectedItem();
            selectedMouse = extractNameFromSpinnerText(selectedMouse);
            Integer mousePrice = myszkiCeny.get(selectedMouse);
            if (mousePrice != null) {
                totalPrice += mousePrice;
            } else {
                Toast.makeText(this, "Mouse not found: " + selectedMouse, Toast.LENGTH_SHORT).show();
            }
        }
        if (checkBox_klawiatura.isChecked()) {
            String selectedKeyboard = (String) spinner_klawiatura.getSelectedItem();
            selectedKeyboard = extractNameFromSpinnerText(selectedKeyboard);
            Integer keyboardPrice = klawiaturyCeny.get(selectedKeyboard);
            if (keyboardPrice != null) {
                totalPrice += keyboardPrice;
            } else {
                Toast.makeText(this, "Keyboard not found: " + selectedKeyboard, Toast.LENGTH_SHORT).show();
            }
        }
        if (checkBox_sluchawki.isChecked()) {
            String selectedHeadphones = (String) spinner_sluchawki.getSelectedItem();
            selectedHeadphones = extractNameFromSpinnerText(selectedHeadphones);
            Integer headphonesPrice = sluchawkiCeny.get(selectedHeadphones);
            if (headphonesPrice != null) {
                totalPrice += headphonesPrice;
            } else {
                Toast.makeText(this, "Headphones not found: " + selectedHeadphones, Toast.LENGTH_SHORT).show();
            }
        }
        cenaTekst.setText("Cena całkowita: " + totalPrice + " zł");
    }

    private String extractNameFromSpinnerText(String spinnerText) {
        if (spinnerText.contains(",")) {
            return spinnerText.split(",")[0].trim();
        }
        return spinnerText;
    }
    private void setupSpinner(Spinner spinner, String[] opisy, int[] obrazki) {
        Log.d("SpinnerSetup", "Initializing spinner with options: ");
        for (String option : opisy) {
            Log.d("SpinnerSetup", "Option: " + option);
        }

        MyAdapter myAdapter = new MyAdapter(getApplicationContext(), obrazki, opisy);
        spinner.setAdapter(myAdapter);
        spinner.setOnItemSelectedListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = (String) parent.getSelectedItem();
        Log.d("SpinnerSelection", "Spinner: " + parent.getId() + " | Selected: " + selectedItem + " | Position: " + position);
        if (position == 0){
            Log.d("SpinnerSelection", "skipping 'wybierz'");
            return;
        }
        updateTotalPrice();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
//
    }
}