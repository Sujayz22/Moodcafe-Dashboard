package com.example.servletdemo.exception;

import com.example.servletdemo.model.OrderEntity;

import java.util.List;

public class ExceptionEmpty {
    public static void validateNotEmpty(List<OrderEntity> orders) throws CustomMyException {
        if (orders == null || orders.isEmpty()) {
            throw new CustomMyException("Orders list is empty");
        }
    }
}
