package com.example.zamowienia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.zamowienia.Toolbar.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends BaseActivity {
    private String [] pcNames;
    private String [] pcDescription;
    private Resources resources;
    private ListView listView;
    private HashMap<String, Object> hashMap;
    private ArrayList<HashMap<String, Object>> pcList;
    private int[] pcPrices = {
            5000,  // Cena dla komputer 1
            4500,  // Cena dla komputer 2
            6000,  // Cena dla komputer 3
            5500,  // Cena dla komputer 4
            7000,  // Cena dla komputer 5
            8000   // Cena dla komputer 6
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setupToolbar(R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


//        --------------LIST VIEW------------------
//        zdjecia, opis itp komputerow dostepnych na sprzedaz

        int [] pcPictures = {
                R.drawable.komputer_zdjecie1,
                R.drawable.komputer_zdjecie1,
                R.drawable.komputer_zdjecie1,
                R.drawable.komputer_zdjecie2,
                R.drawable.komputer_zdjecie3,
                R.drawable.komputer_zdjecie4,
        };

        resources = getResources();
        pcNames = resources.getStringArray(R.array.pcNames);
        pcDescription = resources.getStringArray(R.array.pc_description);

        pcList = new ArrayList<>();
        listView = findViewById(R.id.pc_listView);

        for (int i = 0; i < pcPictures.length; i++) {
            hashMap = new HashMap<>();
            hashMap.put("name", pcNames[i]);
            hashMap.put("image", pcPictures[i]);
            hashMap.put("description", pcDescription[i]);
            hashMap.put("price","Cena: " + pcPrices[i] + " zl");
            pcList.add(hashMap);
        }
        SimpleAdapter simpleAdapter = getSimpleAdapter();
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) ->{
            Intent intent = new Intent(this, PCView.class);
            intent.putExtra("PICTURE_NAME", String.valueOf(pcList.get(i).get("name")));
            intent.putExtra("PICTURE_DESC", String.valueOf(pcList.get(i).get("description")));
            intent.putExtra("PICTURE_IMAGE", String.valueOf(pcList.get(i).get("image")));
            intent.putExtra("PICTURE_PRICE", String.valueOf(pcList.get(i).get("price")));
            startActivity(intent);
        });
    }

    @NonNull
    private SimpleAdapter getSimpleAdapter() {
        String [] from = new String[]{
                "name",
                "image",
                "description",
                "price"
        };
        int [] to = new int[]{
                R.id.namePC,
                R.id.imageView,
                R.id.pcDescription,
                R.id.PCprice
        };
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                getApplicationContext(),
                pcList,
                R.layout.list_view_items,
                from,
                to
        );
        return simpleAdapter;
    }

}