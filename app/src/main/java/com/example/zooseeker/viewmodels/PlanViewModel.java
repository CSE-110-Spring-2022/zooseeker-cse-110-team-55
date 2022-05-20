package com.example.zooseeker.viewmodels;

import static com.example.zooseeker.util.Helper.getLast;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.zooseeker.contracts.ICommand;
import com.example.zooseeker.models.Route;
import com.example.zooseeker.repositories.AnimalItemDao;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;
import com.example.zooseeker.repositories.AnimalDatabase;

import java.util.List;

public class PlanViewModel extends AndroidViewModel {
    private Context context;
    private Graph graph;
    private Route route;
    private AnimalItemDao repository;

    private int curExhibit = -1;
    private List<List<GraphNode>> _plan;

    // Observables
    public MutableLiveData<List<GraphNode>> directions;
    public ObservableField<Integer> remainingExhibits = new ObservableField<>(0);
    public ObservableField<String> curExhibitName = new ObservableField<>("");
    public ObservableField<Integer> curExhibitDist = new ObservableField<>(0);
    public ObservableField<String> nextExhibitName = new ObservableField<>("");
    public ObservableField<Integer> nextExhibitDist = new ObservableField<>(0);
    public ObservableField<String> buttonText = new ObservableField<>("");

    public ICommand nextExhibitCommand = params -> {
        if (remainingExhibits.get() == 1) {
            ((AppCompatActivity) params).finish();
            return;
        }

        getDirectionsToNextExhibit();
    };

    public PlanViewModel(@NonNull Application application) {
        super(application);
        this.context = getApplication().getApplicationContext();
        repository = AnimalDatabase.getSingleton(context).animalItemDao();
        directions = new MutableLiveData<>();

        // Create graph
        graph = new Graph();
        graph.loadGraph(context, "sample_zoo_graph.json", "sample_node_info.json", "sample_edge_info.json");
    }

    public void setPlan(List<List<GraphNode>> plan) { this._plan = plan; }

    public void getDirectionsToNextExhibit() {
        // If there are no more exhibits to visit, do nothing
        if (curExhibit >= _plan.size() - 1) return;

        // Update directions display
        setDirections(_plan.get(++curExhibit));
        updateObservables();
    }

    public LiveData<List<GraphNode>> getDirections() {
        return directions;
    }

    private void setDirections(List<GraphNode> directions) { this.directions.setValue(directions); }

    public Route getRoute() { return route; }

    /**
     * Updates the subjects being observed to match current pathing data
     */
    private void updateObservables() {
        List<GraphNode> directions = getDirections().getValue();
        String curExhibitId = getLast(directions).id;

        curExhibitName.set(graph.nodeInfo.get(curExhibitId).name);
        curExhibitDist.set(route.getDistancesToEachExhibit()[curExhibit]);

        // If there are more exhibits to visit, update the current and next exhibit display names
        if (curExhibit != _plan.size() - 1) {
            // Next set of directions
            List<GraphNode> nextDirections = _plan.get(curExhibit + 1);
            String nextExhibitId = getLast(nextDirections).id;

            String nextName = graph.nodeInfo.get(nextExhibitId).name;
            int nextDist = route.getDistancesToEachExhibit()[curExhibit + 1];

            nextExhibitName.set(nextName);
            nextExhibitDist.set(nextDist);

            buttonText.set(String.format("Next (%s, %dft)", nextName, nextDist));
        } else {
            buttonText.set("END");
        }

        // Decrease amount of remaining exhibits
        remainingExhibits.set(_plan.size() - curExhibit);
    }

    public void initRoute(List<String> selectedAnimals) {
        this.route = new Route(graph, selectedAnimals, "entrance_exit_gate");
        setPlan(route.getRoute());
        getDirectionsToNextExhibit();
    }
}
