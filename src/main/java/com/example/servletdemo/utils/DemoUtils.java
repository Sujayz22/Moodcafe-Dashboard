package com.example.servletdemo.utils;

import com.example.servletdemo.model.OrderEntity;

import java.util.List;

public class DemoUtils {
   
    public static String format(OrderEntity o) {
        return String.format("Order[id=%d,name=%s,amount=%.2f]", o.getId(), o.getName(), o.getAmount());
    }

    public static String format(List<OrderEntity> orders) {
        StringBuilder sb = new StringBuilder();
        for (OrderEntity o : orders) sb.append(format(o)).append("\n");
        return sb.toString();
    }

   
    public static String format(String name, double amount) {
        return String.format("Order[name=%s,amount=%.2f]", name, amount);
    }
}
