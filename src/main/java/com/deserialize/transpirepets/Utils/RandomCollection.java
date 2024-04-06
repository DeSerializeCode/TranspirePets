package com.deserialize.transpirepets.Utils;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map;
    private final Random random;
    private double total;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.map = new TreeMap<>();
        this.total = 0.0D;
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0.0D) {
            return this;
        } else {
            this.total += weight;
            this.map.put(this.total, result);
            return this;
        }
    }

    public E next() {
        double value = this.random.nextDouble() * this.total;
        return this.map.higherEntry(value).getValue();
    }
}