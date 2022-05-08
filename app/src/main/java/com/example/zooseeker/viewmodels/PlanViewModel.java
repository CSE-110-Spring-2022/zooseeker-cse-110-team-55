package com.example.zooseeker.viewmodels;

import static com.example.zooseeker.util.Helper.getLast;

import android.app.Application;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.zooseeker.R;
import com.example.zooseeker.models.AnimalItemDao;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;
import com.example.zooseeker.models.Graph.SymmetricPair;
import com.example.zooseeker.repositories.AnimalDatabase;

import java.util.ArrayList;
import java.util.List;

public class PlanViewModel extends AndroidViewModel {
    private Context context;
    private Graph routeGraph;
    private AnimalItemDao repository;

    private int curExhibit = -1;
    private List<List<GraphNode>> _plan;
    private int[] _distances;

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

        // Init graph
        routeGraph = new Graph();
        routeGraph.loadGraph(context, "sample_zoo_graph.json", "sample_node_info.json", "sample_edge_info.json");
    }

    public void setPlan(List<List<GraphNode>> plan) {
        this._plan = plan;
    }

    public void getNextDirections() {
        if (curExhibit >= _plan.size() - 1) return;

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
            List<GraphNode> nextDirections = _plan.get(curExhibit + 1);
            String nextExhibitId = getLast(nextDirections).id;

            nextExhibitName.set(routeGraph.nodeInfo.get(nextExhibitId).name);
            nextExhibitDist.set(_distances[curExhibit + 1]);
        }

        remainingExhibits.set(_plan.size() - curExhibit);
    }

    public void initRoute(List<String> selectedAnimals) {
        List<List<GraphNode>> route = getRoute(selectedAnimals);
        setPlan(route);
        setDistances(route);
        getNextDirections();
    }

    public void setDistances(List<List<GraphNode>> route) {
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
        List<GraphNode> selectedAnimalNodes = new ArrayList<>();
        for(String a : selectedAnimals){
            selectedAnimalNodes.add(routeGraph.nodes.get(a));
        }

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
}
