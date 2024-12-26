package com.example.zamowienia.Orders;

public class Order {
    private String items;
    private int price;

    public Order(String items, int price) {
        this.items = items;
        this.price = price;
    }

    public String getItems() {
        return items;
    }
}
