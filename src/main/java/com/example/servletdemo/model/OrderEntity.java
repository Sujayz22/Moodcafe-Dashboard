package com.example.servletdemo.model;

public class OrderEntity {
    private int id;
    private String name;
    private double amount;
    private String status;

    public OrderEntity() {}

    public OrderEntity(int id, String name, double amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.status = "PENDING";
    }

    public OrderEntity(String name, double amount) {
        this.name = name;
        this.amount = amount;
        this.status = "PENDING";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}
