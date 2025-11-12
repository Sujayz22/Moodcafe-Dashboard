package com.example.servletdemo.generics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GenericBox<T> {
    private final List<T> items = new ArrayList<>();

    public void add(T t) {
        items.add(t);
    }

    public T get(int index) {
        return items.get(index);
    }

    public List<T> asList() {
        return Collections.unmodifiableList(items);
    }

    public int size() {
        return items.size();
    }
}
