package com.example.zooseeker.models;

import android.util.Pair;

import com.example.zooseeker.models.Graph.NodeInfo;

import java.util.List;

public class DirectionItem {
    public NodeInfo target;
    public String streetName;
    public double weight;
    public List<String> containedExhibits;

    public DirectionItem(NodeInfo target, String streetName, double weight, List<String> containedExhibits) {
        this.target = target;
        this.streetName = streetName;
        this.weight = weight;
        this.containedExhibits = containedExhibits;
    }

}
