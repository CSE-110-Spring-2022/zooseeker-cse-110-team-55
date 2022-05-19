package com.example.zooseeker.viewmodels;

import static com.example.zooseeker.util.Helper.getLast;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.zooseeker.repositories.AnimalItemDao;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;
import com.example.zooseeker.models.Graph.SymmetricPair;
import com.example.zooseeker.repositories.AnimalDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlanViewModel extends AndroidViewModel {
    private Context context;
    private Graph routeGraph;
    private AnimalItemDao repository;

    private int curExhibit = -1;
    private List<List<GraphNode>> _plan;
    private int[] _distances;

    // Observables
    public MutableLiveData<List<GraphNode>> directions;
    public ObservableField<Integer> remainingExhibits = new ObservableField<>(0);
    public ObservableField<String> curExhibitName = new ObservableField<>("");
    public ObservableField<Integer> curExhibitDist = new ObservableField<>(0);
    public ObservableField<String> nextExhibitName = new ObservableField<>("");
    public ObservableField<Integer> nextExhibitDist = new ObservableField<>(0);

    public PlanViewModel(@NonNull Application application) {
        super(application);
        this.context = getApplication().getApplicationContext();
        repository = AnimalDatabase.getSingleton(context).animalItemDao();
        directions = new MutableLiveData<>();

        // Create graph
        routeGraph = new Graph();
        routeGraph.loadGraph(context, "sample_zoo_graph.json", "sample_node_info.json", "sample_edge_info.json");
    }

    public void setPlan(List<List<GraphNode>> plan) {
        this._plan = plan;
    }

    public void getNextDirections() {
        // If there are no more exhibits to visit, do nothing
        if (curExhibit >= _plan.size() - 1) return;

        // Update directions display
        curExhibit++;
        setDirections(_plan.get(curExhibit));
        updateObservables();
    }

    private void updateObservables() {
        List<GraphNode> directions = getDirections().getValue();
        String curExhibitId = getLast(directions).id;

        curExhibitName.set(routeGraph.nodeInfo.get(curExhibitId).name);
        curExhibitDist.set(_distances[curExhibit]);

        if (curExhibit == _plan.size() - 1) {
            // TODO: Display "End"

        } else {
            // If there are more exhibits to visit, update the current and next exhibit display names
            List<GraphNode> nextDirections = _plan.get(curExhibit + 1);
            String nextExhibitId = getLast(nextDirections).id;

            nextExhibitName.set(routeGraph.nodeInfo.get(nextExhibitId).name);
            nextExhibitDist.set(_distances[curExhibit + 1]);
        }

        // Decrease amount of remaining exhibits
        remainingExhibits.set(_plan.size() - curExhibit);
    }

    public void initRoute(List<String> selectedAnimals) {
        List<List<GraphNode>> route = getRoute(selectedAnimals);
        setPlan(route);
        setDistances(route);
        getNextDirections();
    }

    public void setDistances(List<List<GraphNode>> route) {
        // Calculate distances to each exhibit
        this._distances = new int[route.size()];
        for (int i = 0; i < route.size(); i++) {
            List<GraphNode> exhibitDirections = route.get(i);
            int totalWeight = 0;
            for (int j = 0; j < exhibitDirections.size() - 1; j++) {
                GraphNode source = exhibitDirections.get(j);
                GraphNode dest = exhibitDirections.get(j + 1);

                SymmetricPair edge = new SymmetricPair(source.id, dest.id);
                totalWeight += routeGraph.edges.get(edge).weight;
            }
            _distances[i] = totalWeight;
        }
    }

    public List<List<GraphNode>> getRoute(List<String> selectedAnimals){
        // Convert exhibit IDs to graph nodes
        List<GraphNode> selectedAnimalNodes = new ArrayList<>(
                selectedAnimals
                        .stream()
                        .map(a -> routeGraph.nodes.get(a))
                        .collect(Collectors.toList())
        );
        GraphNode start = routeGraph.nodes.get("entrance_exit_gate");
        return routeGraph.createPlan(selectedAnimalNodes, start);
    }


    private void setDirections(List<GraphNode> directions) {
        this.directions.setValue(directions);
    }

    public LiveData<List<GraphNode>> getDirections() {
        return directions;
    }

    public Graph getGraph() {
        return routeGraph;
    }

    public List<List<GraphNode>> getPlan() {
        return _plan;
    }
}
