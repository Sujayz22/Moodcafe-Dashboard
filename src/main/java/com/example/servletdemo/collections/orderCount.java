package com.example.servletdemo.collections;

import com.example.servletdemo.model.OrderEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class orderCount {
    
    public static Map<String, Integer> countByName(List<OrderEntity> orders) {
        Map<String, Integer> map = new HashMap<>();
        for (OrderEntity o : orders) {
            map.put(o.getName(), map.getOrDefault(o.getName(), 0) + 1);
        }
        return map;
    }
}
