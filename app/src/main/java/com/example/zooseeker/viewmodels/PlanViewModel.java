package com.example.zooseeker.viewmodels;

import static com.example.zooseeker.util.Helper.getLast;
import static com.example.zooseeker.util.Helper.getSecondToLast;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.zooseeker.contracts.ICommand;
import com.example.zooseeker.models.DirectionItem;
import com.example.zooseeker.models.Graph.EdgeInfo;
import com.example.zooseeker.models.Route;
import com.example.zooseeker.repositories.AnimalItemDao;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;
import com.example.zooseeker.repositories.AnimalDatabase;
import com.example.zooseeker.util.Helper;

import java.util.ArrayList;
import java.util.List;

public class PlanViewModel extends AndroidViewModel {
    private Context context;
    private Graph graph;
    private Route route;
    private AnimalItemDao repository;

    private int curExhibit = -1;
    private List<List<GraphNode>> _plan;


    // Observables
    public MutableLiveData<Boolean> detailedDirectionToggle = new MutableLiveData<>(false);
    public MutableLiveData<List<DirectionItem>> directions;
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

    /**
     * Initializes _plan field.
     * @param plan list of list of nodes.
     */
    public void setPlan(List<List<GraphNode>> plan) {
        this._plan = plan;
    }

    /**
     *
     * @param plan each list of selected graph nodes and paths to them
     * @param exhibitNum the specific exhibit that you compute the path for
     * @return a list of detailed DirectionItem objects
     */
    private List<DirectionItem> computeDetailedPlan(List<List<GraphNode>> plan, int exhibitNum) {


        // Add new set of directions to plan
        ArrayList<DirectionItem> newDir = new ArrayList<>();

        // Add all nodes from current direction set to nodes
        List<GraphNode> nodes = new ArrayList<>();
        for (GraphNode node : plan.get(exhibitNum)) nodes.add(node);

        // Get Edges
        List<Graph.GraphData.GraphEdge> edges = graph.getEdgesFromNodes(nodes);

        double currWeight = 0;

        //Iterate through all edges
        for (int currEdgeNum = 0; currEdgeNum < edges.size(); currEdgeNum++) {

            EdgeInfo currEdge = graph.edgeInfo.get(edges.get(currEdgeNum).id);

            //Update weight based on first edge (entrance_exit to entrance_plaza)
            if (currEdgeNum == 0) {
                currWeight += edges.get(currEdgeNum).weight;
                continue;
            }

            //
            EdgeInfo previousEdge = graph.edgeInfo.get(edges.get(currEdgeNum - 1).id);

            //Add a new direction item with target node's name, edge's street name, and curr weight.
            newDir.add(new DirectionItem(graph.nodeInfo.get(nodes.get(currEdgeNum).id).name, previousEdge.street, currWeight));
            currWeight = edges.get(currEdgeNum).weight;
        }

        //Add the final edge
        EdgeInfo finalEdge = graph.edgeInfo.get(getLast(edges).id);
        newDir.add(new DirectionItem(graph.nodeInfo.get(getLast(nodes).id).name, finalEdge.street, currWeight));

        return newDir;

    }

    /**
     *
     * @param plan each list of selected graph nodes and paths to them
     * @param exhibitNum the specific exhibit that you compute the path for
     * @return a simple list of DirectionItem objects
     */
    private List<DirectionItem> computeSimplePlan(List<List<GraphNode>> plan, int exhibitNum) {

            // Add new set of directions to plan
            ArrayList<DirectionItem> newDir = new ArrayList<>();

            // Add all nodes from current direction set to nodes
            List<GraphNode> nodes = new ArrayList<>();
            for (GraphNode node : plan.get(exhibitNum)) nodes.add(node);

            // Get Edges
            List<Graph.GraphData.GraphEdge> edges = graph.getEdgesFromNodes(nodes);

            double currWeight = 0;


            for (int currEdgeNum = 0; currEdgeNum < edges.size(); currEdgeNum++) {

                EdgeInfo currEdge = graph.edgeInfo.get(edges.get(currEdgeNum).id);

                //Update weight based on first edge (entrance_exit to entrance_plaza)
                if (currEdgeNum == 0) {
                    currWeight += edges.get(currEdgeNum).weight;
                    continue;
                }

                EdgeInfo previousEdge = graph.edgeInfo.get(edges.get(currEdgeNum - 1).id);


                //If two edges have the same street name, do not add new DirectionItem, but update weight.
                if ((currEdge.street).equals(previousEdge.street)) {
                    currWeight += edges.get(currEdgeNum).weight;
                }

                //Two edges do not have the same street name, so create a new DirectionItem.
                else {

                    newDir.add(new DirectionItem(graph.nodeInfo.get(nodes.get(currEdgeNum).id).name, previousEdge.street, currWeight));
                    currWeight = edges.get(currEdgeNum).weight;

                }

            }

            EdgeInfo finalEdge = graph.edgeInfo.get(getLast(edges).id);
            newDir.add(new DirectionItem(graph.nodeInfo.get(getLast(nodes).id).name, finalEdge.street, currWeight));
            return newDir;

    }

    /**
     * Changes state of MutableLivaData boolean based on toggle.
     */
    public void updateCurrentDirections() {
        if (!detailedDirectionToggle.getValue()) {
            setDirections(computeSimplePlan(_plan, curExhibit));
        }
        else {
            setDirections(computeDetailedPlan(_plan, curExhibit));
        }

        updateObservables();

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
        }
        else {
            setDirections(computeDetailedPlan(_plan, ++curExhibit));
        }

        updateObservables();

    }

    public LiveData<List<DirectionItem>> getDirections() {
        return directions;
    }

    private void setDirections(List<DirectionItem> directions) {


        this.directions.setValue(directions);
    }

    public Route getRoute() { return route; }

    /**
     * Updates the subjects being observed to match current pathing data
     */
    private void updateObservables() {
        List<DirectionItem> directions = getDirections().getValue();
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

    public void initRoute(List<String> selectedAnimals) {
        this.route = new Route(graph, selectedAnimals, "entrance_exit_gate");
        setPlan(route.getRoute());
        getDirectionsToNextExhibit();
    }

    public Graph getGraph() {
        return graph;
    }

    public List<List<GraphNode>> getPlan() {
        return _plan;
    }

    public void clearPlan() {
        _plan.clear();
    }

    public boolean isLastExhibit(){
        return curExhibit >= _plan.size();
    }
}
