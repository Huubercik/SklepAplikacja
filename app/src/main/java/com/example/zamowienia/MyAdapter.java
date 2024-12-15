package com.example.zamowienia;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
    Context context;
    int [] myszki;
    String [] opisy;
    LayoutInflater layoutInflater;
    ImageView imageView;
    TextView textView;

    public MyAdapter(Context context, int[] myszki, String[] opisy){
        super();
        this.context = context;
        this.myszki = myszki;
        this.opisy = opisy;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return opisy.length;
    }

    @Override
    public Object getItem(int position) {
        return opisy[position];
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.my_spinner_items, null);
        imageView = convertView.findViewById(R.id.imageView);
        textView = convertView.findViewById(R.id.textView);

        if (opisy[position].equals("Wybierz...")) {
            imageView.setVisibility(View.GONE);
            textView.setText(opisy[position]);
        } else {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(myszki[position - 1]);
            textView.setText(opisy[position]);
        }

        return convertView;
    }
}
