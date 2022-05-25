package com.example.zooseeker.viewmodels;

import static com.example.zooseeker.util.Helper.getLast;
import static com.example.zooseeker.util.Helper.getSecondToLast;

import android.app.Application;
import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.zooseeker.contracts.ICommand;
import com.example.zooseeker.models.DirectionItem;
import com.example.zooseeker.models.Graph.EdgeInfo;
import com.example.zooseeker.models.Graph.GraphData.GraphEdge;
import com.example.zooseeker.models.Route;
import com.example.zooseeker.repositories.AnimalItemDao;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;
import com.example.zooseeker.repositories.AnimalDatabase;
import com.example.zooseeker.util.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlanViewModel extends AndroidViewModel {
    private static double delta = 0.001d;
    private Context context;
    private Graph graph;
    private Route route;
    private AnimalItemDao repository;

    private int curExhibit = -1;
    private List<List<GraphNode>> _plan;
    private HashMap<String, List<String>> exhibitGroups;

    // Observables
    public MutableLiveData<Boolean> detailedDirectionToggle = new MutableLiveData<>(false);
    public MutableLiveData<List<DirectionItem>> directions;
    public ObservableField<Integer> remainingExhibits = new ObservableField<>(0);
    public ObservableField<String> curExhibitName = new ObservableField<>("");
    public ObservableField<Integer> curExhibitDist = new ObservableField<>(0);
    public ObservableField<String> nextExhibitName = new ObservableField<>("");
    public ObservableField<Integer> nextExhibitDist = new ObservableField<>(0);
    public ObservableField<String> buttonText = new ObservableField<>("");

    public MutableLiveData<Pair<Double, Double>> lastKnownLocation = new MutableLiveData<>();
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

    /**
     * Changes state of MutableLivaData boolean based on toggle.
     */
    public void updateCurrentDirections(boolean useDetailed) {
        if (!useDetailed) {
            setDirections(computeSimplePlan(_plan, curExhibit));
        } else {
            setDirections(computeDetailedPlan(_plan, curExhibit));
        }
    }

    /**
     * Proceeds to next exhibit and changes state based on toggle.
     */
    public void getDirectionsToNextExhibit() {
        // If there are no more exhibits to visit, do nothing
        if (curExhibit >= _plan.size() - 1) return;

        // Update directions display
        if (!detailedDirectionToggle.getValue()) {
            setDirections(computeSimplePlan(_plan, ++curExhibit));
        } else {
            setDirections(computeDetailedPlan(_plan, ++curExhibit));
        }

        updateObservables();
    }

    public void initRoute(List<String> selectedAnimals) {
        this.route = new Route(graph, selectedAnimals, "entrance_exit_gate");
        setPlan(route.getRoute());
        getDirectionsToNextExhibit();
    }

    public void clearPlan() {
        _plan.clear();
    }

    public boolean isOnPathToCurExhibit() {
        var loc = lastKnownLocation.getValue();
        return directions.getValue()
                .stream()
                .anyMatch(e -> {
                    var dlat = e.target.lat - loc.first;
                    var dlng = e.target.lng - loc.second;
                    return Math.sqrt(Math.pow(dlat, 2) + Math.pow(dlng, 2)) < delta;
                });
    }

    /**
     * Computes detailed directions
     * @param plan       each list of selected graph nodes and paths to them
     * @param exhibitNum the specific exhibit that you compute the path for
     * @return a list of detailed DirectionItem objects
     */
    private List<DirectionItem> computeDetailedPlan(List<List<GraphNode>> plan, int exhibitNum) {
        // Add new set of directions to plan
        ArrayList<DirectionItem> newDir = new ArrayList<>();
        // Add all nodes from current direction set to nodes
        List<GraphNode> nodes = new ArrayList<>(plan.get(exhibitNum));

        // Get Edges
        List<GraphEdge> edges = graph.getEdgesFromNodes(nodes);
        double currWeight = 0;
        //Iterate through all edges
        for (int currEdgeNum = 0; currEdgeNum < edges.size(); currEdgeNum++) {
            var currNode = graph.nodeInfo.get(nodes.get(currEdgeNum).id);
            EdgeInfo currEdge = graph.edgeInfo.get(edges.get(currEdgeNum).id);
            //Update weight based on first edge (entrance_exit to entrance_plaza)
            if (currEdgeNum == 0) {
                currWeight += edges.get(currEdgeNum).weight;
                continue;
            }
            EdgeInfo previousEdge = graph.edgeInfo.get(edges.get(currEdgeNum - 1).id);
            //Add a new direction item with target node's name, edge's street name, and curr weight.
            newDir.add(new DirectionItem(currNode, previousEdge.street, currWeight, null));
            currWeight = edges.get(currEdgeNum).weight;
        }

        //Add the final edge
        EdgeInfo finalEdge = graph.edgeInfo.get(getLast(edges).id);
        var containedExhibits = exhibitGroups.getOrDefault(getLast(nodes).id, null);

        var node = graph.nodeInfo.get(getLast(nodes).id);
        newDir.add(new DirectionItem(node, finalEdge.street, currWeight, containedExhibits));

        return newDir;
    }

    /**
     * @param plan       each list of selected graph nodes and paths to them
     * @param exhibitNum the specific exhibit that you compute the path for
     * @return a simple list of DirectionItem objects
     */
    private List<DirectionItem> computeSimplePlan(List<List<GraphNode>> plan, int exhibitNum) {
        // Add new set of directions to plan
        ArrayList<DirectionItem> newDir = new ArrayList<>();
        // Add all nodes from current direction set to nodes
        List<GraphNode> nodes = new ArrayList<>(plan.get(exhibitNum));

        // Get Edges
        var edges = graph.getEdgesFromNodes(nodes);
        double currWeight = 0;
        for (int currEdgeNum = 0; currEdgeNum < edges.size(); currEdgeNum++) {
            EdgeInfo currEdge = graph.edgeInfo.get(edges.get(currEdgeNum).id);
            // Update weight based on first edge (entrance_exit to entrance_plaza)
            if (currEdgeNum == 0) {
                currWeight += edges.get(currEdgeNum).weight;
                continue;
            }

            EdgeInfo previousEdge = graph.edgeInfo.get(edges.get(currEdgeNum - 1).id);
            // If two edges have the same street name, do not add new DirectionItem, but update weight.
            if ((currEdge.street).equals(previousEdge.street)) {
                currWeight += edges.get(currEdgeNum).weight;
            } else {
                // Two edges do not have the same street name, so create a new DirectionItem.
                newDir.add(new DirectionItem(graph.nodeInfo.get(nodes.get(currEdgeNum).id), previousEdge.street, currWeight, null));
                currWeight = edges.get(currEdgeNum).weight;
            }
        }

        // Add last edge to directions
        EdgeInfo finalEdge = graph.edgeInfo.get(getLast(edges).id);
        var containedExhibits = exhibitGroups.getOrDefault(getLast(nodes).id, null);
        newDir.add(new DirectionItem(graph.nodeInfo.get(getLast(nodes).id), finalEdge.street, currWeight, containedExhibits));
        return newDir;
    }

    /**
     * Updates the subjects being observed to match current pathing data
     */
    private void updateObservables() {
        String curExhibitId = getLast(_plan.get(curExhibit)).id;

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

    /// Getters
    public List<List<GraphNode>> getPlan() {
        return _plan;
    }

    public Graph getGraph() {
        return graph;
    }

    public Route getRoute() {
        return route;
    }

    public LiveData<List<DirectionItem>> getDirections() {
        return directions;
    }

    public boolean isLastExhibit() {
        return curExhibit >= _plan.size();
    }

    /// Setters
    public void setPlan(List<List<GraphNode>> plan) {
        this._plan = plan;
    }

    private void setDirections(List<DirectionItem> directions) {
        this.directions.setValue(directions);
    }

    public void setExhibitGroups(HashMap<String, List<String>> groups) {
        this.exhibitGroups = groups;
    }
}