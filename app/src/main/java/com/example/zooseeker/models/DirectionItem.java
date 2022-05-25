package com.example.zooseeker.models;

import java.util.List;

public class DirectionItem {
    public String target;
    public String streetName;
    public double weight;
    public List<String> containedExhibits;

    public DirectionItem(String target, String streetName, double weight, List<String> containedExhibits) {
        this.target = target;
        this.streetName = streetName;
        this.weight = weight;
        this.containedExhibits = containedExhibits;
    }

}
