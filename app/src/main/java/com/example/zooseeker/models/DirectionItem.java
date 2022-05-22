package com.example.zooseeker.models;

public class DirectionItem {

    public String target;
    public String streetName;
    public double weight;

    public DirectionItem(String target, String streetName, double weight) {
        this.target = target;
        this.streetName = streetName;
        this.weight = weight;
    }

}
