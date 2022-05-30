package com.example.zooseeker.viewmodels;

import static android.content.Context.MODE_PRIVATE;
import static com.example.zooseeker.util.Constant.CURR_INDEX;
import static com.example.zooseeker.util.Constant.SHARED_PREF;
import static com.example.zooseeker.util.Helper.getLast;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.zooseeker.contracts.ICommand;
import com.example.zooseeker.models.DirectionItem;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.models.Graph.EdgeInfo;
import com.example.zooseeker.models.Graph.GraphData.GraphEdge;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;
import com.example.zooseeker.models.Graph.NodeInfo;
import com.example.zooseeker.models.Route;
import com.example.zooseeker.repositories.AnimalDatabase;
import com.example.zooseeker.repositories.AnimalItemDao;
import com.example.zooseeker.util.Alert;
import com.example.zooseeker.util.Alert.AlertHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlanViewModel extends AndroidViewModel implements AlertHandler {
    private Application app;
    private static double delta = 0.001d;
    private Graph graph;
    private Route route;
    private AnimalItemDao repository;
    private GraphNode startNode;
    private GraphNode endNode;

    private int curExhibit = -1;
    private List<List<GraphNode>> _plan;
    private HashMap<String, List<String>> exhibitGroups;

    // Observables
    public MutableLiveData<Boolean> detailedDirectionToggle = new MutableLiveData<>(false);
    public MutableLiveData<List<DirectionItem>> directions;
    public MutableLiveData<String> closestExhibit = new MutableLiveData<>();

    public ObservableField<Integer> remainingExhibits = new ObservableField<>(0);
    public ObservableField<String> curExhibitName = new ObservableField<>("");
    public ObservableField<Integer> curExhibitDist = new ObservableField<>(0);
    public ObservableField<String> nextExhibitName = new ObservableField<>("");
    public ObservableField<Integer> nextExhibitDist = new ObservableField<>(0);
    public ObservableField<String> buttonText = new ObservableField<>("");

    public MutableLiveData<Pair<Double, Double>> lastKnownLocation = new MutableLiveData<>();
    public ICommand nextExhibitCommand = params -> {
        // Increment direction index from shared preferences or reset index when reaches final destination
        SharedPreferences sharedPreferences = app.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (isLastExhibit()) {
            editor.putInt(CURR_INDEX, -1);
            editor.apply();
            ((AppCompatActivity) params).finish();
        } else {
            editor.putInt(CURR_INDEX, curExhibit + 1);
            editor.apply();
            getDirectionsToNextExhibit();
        }
    };

    public PlanViewModel(@NonNull Application application) {
        super(application);
        this.app = application;
        repository = AnimalDatabase.getSingleton(application).animalItemDao();
        directions = new MutableLiveData<>();

        // Create graph
        graph = new Graph();
        graph.loadGraph(app, "sample_zoo_graph.json", "sample_node_info.json", "sample_edge_info.json");

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
        curExhibit++;

        // Get user location
        GraphNode userNode;
        if (lastKnownLocation.getValue() == null) {
            userNode = route.getRoute().get(0).get(0);
        } else {
            var exhibit = exhibitAtLocation(lastKnownLocation.getValue());
            userNode = graph.nodes.get(exhibit.id);
        }
        // Get path to next exhibit from user's location
        var x = route.shortestPathToNode(userNode, getLast(_plan.get(curExhibit)));
        _plan.set(curExhibit, x);
        route.updateDistances();

        // Update directions display
        if (!detailedDirectionToggle.getValue()) {
            setDirections(computeSimplePlan(_plan, curExhibit));
        } else {
            setDirections(computeDetailedPlan(_plan, curExhibit));
        }

        updateObservables();
    }

    public void initRoute(List<String> selectedAnimals, String start) {
        this.route = new Route(graph, selectedAnimals, start == null ? "entrance_exit_gate" : start, "entrance_exit_gate");
        setPlan(route.getRoute());
        getDirectionsToNextExhibit();
    }

    public void clearPlan() {
        _plan.clear();
    }

    public void adjustToNewLocation(Pair<Double, Double> location) {
        SharedPreferences sharedPreferences = app.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // If user is off-track
        if (!isOnPathToCurExhibit()) {
            // Get exhibit user is currently at
            var exhibit = exhibitAtLocation(location);
            if (exhibit == null) return;

            // Save last known location
            editor.putString("last_exhibit", exhibit.id);
            editor.apply();

            // Find fastest route to current exhibit
            var start = graph.nodes.get(exhibit.id);
            var dest = getLast(_plan.get(curExhibit));

            var closestExhibit = findClosestExhibit();
            // If there is a closer exhibit
            if (!closestExhibit.id.equals(dest.id)) {
                this.closestExhibit.setValue(closestExhibit.name);
            }

            if (directions.getValue().size() < 1) return;
            // Update plan
            _plan.set(curExhibit, route.shortestPathToNode(start, dest));
            route.updateDistances();
            updateCurrentDirections(detailedDirectionToggle.getValue());
            updateObservables();
        } else {
            // Update distances to current exhibit
            DirectionItem node;
            do {
                node = directions.getValue().remove(0);
                curExhibitDist.set(curExhibitDist.get() - (int) node.weight);
            } while (!exhibitAtLocation(location).id.equals(node.target.id));
            directions.setValue(new ArrayList<>(directions.getValue()));
        }
    }

    /**
     * Converts lat/long location to exhibit
     * @param location
     * @return Exhibit at given location, or null if one doesn't exist
     */
    private NodeInfo exhibitAtLocation(Pair<Double, Double> location) {
        return graph.nodeInfo.values()
                .stream()
                .filter(e -> {
                    var dlat = e.lat - location.first;
                    var dlng = e.lng - location.second;
                    return Math.pow(dlat, 2) + Math.pow(dlng, 2) < Math.pow(delta, 2);
                })
                .findFirst()
                .orElse(null);
    }

    private NodeInfo findClosestExhibit() {
        // Get paths to all remaining exhibits
        double minWeight = Double.MAX_VALUE;
        GraphNode closestExhibit = getLast(_plan.get(curExhibit));
        // Get the exhibit the user is closest to right now
        var location = exhibitAtLocation(lastKnownLocation.getValue());
        for (int i = curExhibit; i < _plan.size() - 1; i++) {
            // Find a path to all exhibits in the list
            var pathToExhibit = route.shortestPathToNode(graph.nodes.get(location.id), getLast(_plan.get(i)));
            // Calculate distance
            double pathWeight = 0;
            for (int j = 0; j < pathToExhibit.size() - 1; j++) {
                var edge = graph.edges.get(new Graph.SymmetricPair(pathToExhibit.get(j).id, pathToExhibit.get(j + 1).id));
                pathWeight += edge.weight;
            }
            if (pathWeight < minWeight) {
                minWeight = pathWeight;
                closestExhibit = getLast(pathToExhibit);
            }
        }
        // Find closest one
        return graph.nodeInfo.get(closestExhibit.id);
    }

    private boolean isOnPathToCurExhibit() {
        var loc = lastKnownLocation.getValue();
        var isOnPath = directions.getValue()
                .stream()
                .anyMatch(e -> {
                    var dlat = e.target.lat - loc.first;
                    var dlng = e.target.lng - loc.second;
                    return Math.pow(dlat, 2) + Math.pow(dlng, 2) < Math.pow(delta, 2);
                });
        return isOnPath;
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

        if (nodes.size() == 1) return new ArrayList<>();

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

    public int getCurExhibit() { return curExhibit; }

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
        return curExhibit >= _plan.size() - 1;
    }

    /// Setters
    public void setLocation(Pair<Double, Double> location) {
        this.lastKnownLocation.setValue(location);
    }

    public void setPlan(List<List<GraphNode>> plan) {
        this._plan = plan;
    }

    private void setDirections(List<DirectionItem> directions) {
        this.directions.setValue(directions);
    }

    public void setExhibitGroups(HashMap<String, List<String>> groups) {
        this.exhibitGroups = groups;
    }

    @Override
    public void acceptHandler() {
        // Get exhibit user is currently at
        var exhibit = exhibitAtLocation(lastKnownLocation.getValue());

        // Add all exhibits that are remaining
        var start = graph.nodes.get(exhibit.id);
        var remaining = new ArrayList<String>();
        for (int i = curExhibit; i < _plan.size() - 1; i++) {
            remaining.add(getLast(_plan.get(i)).id);
        }

        // Create plan through remaining exhibits
        curExhibit = 0;
        route.createRouteThroughExhibits(remaining, start.id, "entrance_exit_gate");
        if (route.getRoute().get(0).size() <= 1) {
            route.getRoute().remove(0);
            route.updateDistances();
        }

        // Update plan
        setPlan(route.getRoute());
        updateCurrentDirections(detailedDirectionToggle.getValue());
        updateObservables();
    }

    @Override
    public void rejectHandler() { }

    public boolean reverseExhibit(){
        // Get start node
        if(lastKnownLocation.getValue() == null){
            startNode = route.getRoute().get(0).get(0);
        }else {
            startNode = graph.nodes.get(exhibitAtLocation(lastKnownLocation.getValue()).id);
        }

        // Get end node
        if (curExhibit == 0){
            endNode = _plan.get(curExhibit).get(0);
        } else {
            endNode = getLast(_plan.get(curExhibit - 1));
        }

        // Create a new route to previous
        List<GraphNode> newRoute = route.shortestPathToNode(startNode, endNode);
        List<List<GraphNode>> routeList = new ArrayList<>();
        routeList.add(newRoute);
        curExhibit--;
        _plan.remove(curExhibit);
        _plan.add(curExhibit, newRoute);

        // Update
        updateCurrentDirections(detailedDirectionToggle.getValue());
        route.updateDistances();
        updateObservables();
        return true;
    }

    public void skipNextExhibit() {
        // TODO Implement the logic to skip next exhibit
    }
}
