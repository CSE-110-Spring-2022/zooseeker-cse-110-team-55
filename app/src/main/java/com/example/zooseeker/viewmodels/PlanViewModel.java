package com.example.zooseeker.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.test.core.app.ApplicationProvider;

import com.example.zooseeker.models.Animal;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class PlanViewModel extends AndroidViewModel {
    private int curExhibit = -1;
    private List<List<GraphNode>> _plan;
    public MutableLiveData<List<GraphNode>> directions;
    public ObservableField<String> nextExhibit;

    public PlanViewModel(@NonNull Application application) {
        super(application);
        directions = new MutableLiveData<>();
    }

    public void setPlan(List<List<GraphNode>> plan) {
        this._plan = plan;
    }

    public void getNextDirections() {
        if (curExhibit < _plan.size() - 1) {
            curExhibit++;
            setDirections(_plan.get(curExhibit));
        }
    }

    public List<List<Graph.GraphData.GraphNode>> getRoute(List<String> animals){
        Graph routeGraph = new Graph();
        //List<Animal> selectedAnimals = new ArrayList<>();
        routeGraph.loadGraph(getApplication().getApplicationContext(), "sample_zoo_graph.json");
        List<Graph.GraphData.GraphNode> selected_animals_to_nodes = new ArrayList<>();
        for(String a : animals){
            selected_animals_to_nodes.add(routeGraph.nodes.get(a));
        }
        return routeGraph.createPlan(selected_animals_to_nodes,routeGraph.nodes.get("entrance_exit_gate"));
    }


    private void setDirections(List<GraphNode> directions) {
        this.directions.setValue(directions);
    }

    public LiveData<List<GraphNode>> getDirections() {
        return directions;
    }
}
