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

    public MyAdapter(Context context, int[] myszki, String[] opisy) {
        this.context = context;
        this.myszki = new int[myszki.length];
        System.arraycopy(myszki, 0, this.myszki, 0, myszki.length);

        this.opisy = new String[opisy.length + 1];
        this.opisy[0] = context.getString(R.string.chooseSpinner);
        System.arraycopy(opisy, 0, this.opisy, 1, opisy.length);

        this.layoutInflater = LayoutInflater.from(context);
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
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.my_spinner_items, null);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textView = convertView.findViewById(R.id.textView);

        if (position == 0) {
            imageView.setVisibility(View.GONE);
            textView.setText(context.getString(R.string.chooseSpinner));
        } else {
            imageView.setVisibility(View.VISIBLE);
            if (position - 1 < myszki.length) {
                imageView.setImageResource(myszki[position - 1]);
            }
            textView.setText(opisy[position]);
        }

        return convertView;
    }
}
