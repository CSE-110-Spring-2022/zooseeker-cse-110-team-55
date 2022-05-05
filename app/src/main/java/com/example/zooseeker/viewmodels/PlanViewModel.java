package com.example.zooseeker.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    public MutableLiveData<List<GraphNode>> directions;
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
        routeGraph.loadGraph(context, "sample_zoo_graph.json");
        routeGraph.loadNodeInfo(context, "sample_node_info.json");
        routeGraph.loadEdgeInfo(context, "sample_edge_info.json");
    }

    public void setPlan(List<List<GraphNode>> plan) {
        this._plan = plan;
    }

    // TODO: Encapsulate
    public void getNextDirections() {
        if (curExhibit >= _plan.size() - 1) return;

        curExhibit++;
        setDirections(_plan.get(curExhibit));

        List<GraphNode> directions = getDirections().getValue();
        String curExhibitId = directions.get(directions.size() - 1).id;
        curExhibitName.set(routeGraph.nodeInfo.get(curExhibitId).name);

        // TODO: Refactor to not duplicate exhibit distance calculation
        int totalWeight = 0;
        for (int i = 0; i < directions.size() - 1; i++) {
            GraphNode source = directions.get(i);
            GraphNode dest = directions.get(i + 1);

            SymmetricPair edge = new SymmetricPair(source.id, dest.id);
            totalWeight += routeGraph.edges.get(edge).weight;
        }
        curExhibitDist.set(totalWeight);

        if (curExhibit == _plan.size() - 1) {
            // Do not display next exhibit
        } else {
            List<GraphNode> nextDirections = _plan.get(curExhibit + 1);
            String nextExhibitId = nextDirections.get(nextDirections.size() - 1).id;
            nextExhibitName.set(routeGraph.nodeInfo.get(nextExhibitId).name);

            int weightToNext = 0;
            for (int i = 0; i < nextDirections.size() - 1; i++) {
                GraphNode source = nextDirections.get(i);
                GraphNode dest = nextDirections.get(i + 1);

                SymmetricPair edge = new SymmetricPair(source.id, dest.id);
                weightToNext += routeGraph.edges.get(edge).weight;
            }
            nextExhibitDist.set(weightToNext);
        }
    }

    public void initRoute(List<String> selectedAnimals) {
        List<List<GraphNode>> route = getRoute(selectedAnimals);
        setPlan(route);
        getNextDirections();
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
}
