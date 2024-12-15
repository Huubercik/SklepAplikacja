package com.example.zamowienia.Orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zamowienia.R;

import java.util.List;

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private List<Order> orders;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_view_orders, parent, false);
        }

        TextView orderTitle = convertView.findViewById(R.id.order_title);
        TextView orderContent = convertView.findViewById(R.id.order_content);

        Order order = orders.get(position);

        orderTitle.setText("Zamówienie nr " + (position + 1));
        orderContent.setText("Zawartość: " + order.getItems());

        return convertView;
    }
}

