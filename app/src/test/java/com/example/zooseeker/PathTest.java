package com.example.zooseeker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import android.app.Activity;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.zooseeker.activities.HomeActivity;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;
import com.example.zooseeker.models.Graph.SymmetricPair;

import java.util.ArrayList;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PathTest {
    Context context;

    @Before
    public void initContext() {
        this.context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testGraphLoad() {
        Graph graph = new Graph();
        graph.loadGraph(context, "sample_zoo_graph.json");

        assertEquals(7, graph.nodes.size());
        assertEquals(7, graph.edgeWeights.size());
    }

    @Test
    public void testObviousPath() {
        Graph graph = new Graph();
        graph.loadGraph(context, "sample_zoo_graph.json");

        List<GraphNode> selected = new ArrayList<>();
        selected.add(graph.nodes.get("gorillas"));
        List<List<GraphNode>> plan = graph.createPlan(selected, graph.nodes.get("entrance_exit_gate"));
        assertEquals(2, plan.size());
        assertEquals(3, plan.get(0).size());
    }

    @Test
    public void testShortestPath() {
        Graph graph = new Graph();
        graph.loadGraph(context, "sample_zoo_graph.json");

        List<GraphNode> selected = new ArrayList<>();
        selected.add(graph.nodes.get("lions"));
        List<List<GraphNode>> plan = graph.createPlan(selected, graph.nodes.get("entrance_exit_gate"));
        double totalWeight = 0;
        List<GraphNode> path = plan.get(0);
        for (int i = 0; i < path.size() - 1; i++) {
            totalWeight += graph.edgeWeights.get(new SymmetricPair(path.get(i).id, path.get(i + 1).id));
        }
        assertEquals(310, (int) totalWeight);
    }


    @Test
    public void testNearestNeighbor() {
        Graph graph = new Graph();
        graph.loadGraph(context, "sample_zoo_graph.json");

        List<GraphNode> selected = new ArrayList<>();
        selected.add(graph.nodes.get("lions"));
        selected.add(graph.nodes.get("gators"));
        selected.add(graph.nodes.get("arctic_foxes"));
        List<List<GraphNode>> plan = graph.createPlan(selected, graph.nodes.get("entrance_exit_gate"));

        assertEquals("entrance_exit_gate", plan.get(0).get(0).id);
        assertEquals("gators", plan.get(1).get(0).id);
        assertEquals("lions", plan.get(2).get(0).id);
        assertEquals("arctic_foxes", plan.get(3).get(0).id);
    }
}