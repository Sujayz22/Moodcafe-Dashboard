package com.example.servletdemo.model;

public class OrderLine {
    private String itemName;
    private int qty;
    private double price;

    public OrderLine() {}

    public OrderLine(String itemName, int qty, double price) {
        this.itemName = itemName;
        this.qty = qty;
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
