package com.example.zooseeker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.zooseeker.models.Graph;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;
import com.example.zooseeker.models.Graph.SymmetricPair;
import com.example.zooseeker.models.Route;

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
        graph.loadGraph(context, "sample_zoo_graph.json", "sample_node_info.json", "sample_edge_info.json");

        assertEquals(7, graph.nodes.size());
        assertEquals(7, graph.edges.size());
    }

    @Test
    public void testObviousPath() {
        Graph graph = new Graph();
        graph.loadGraph(context, "sample_zoo_graph.json", "sample_node_info.json", "sample_edge_info.json");

        List<String> selected = new ArrayList<>();
        selected.add("gorillas");
        Route route = new Route(graph, selected, "entrance_exit_gate");
        List<List<GraphNode>> plan = route.getRoute();
        assertEquals(2, plan.size());
        assertEquals(3, plan.get(0).size());
    }

    @Test
    public void testShortestPath() {
        Graph graph = new Graph();
        graph.loadGraph(context, "sample_zoo_graph.json", "sample_node_info.json", "sample_edge_info.json");

        List<String> selected = new ArrayList<>();
        selected.add("lions");
        Route route = new Route(graph, selected, "entrance_exit_gate");
        List<List<GraphNode>> plan = route.getRoute();
        double totalWeight = 0;
        List<GraphNode> path = plan.get(0);
        for (int i = 0; i < path.size() - 1; i++) {
            totalWeight += graph.edges.get(new SymmetricPair(path.get(i).id, path.get(i + 1).id)).weight;
        }
        assertEquals(310, (int) totalWeight);
    }


    @Test
    public void testNearestNeighbor() {
        Graph graph = new Graph();
        graph.loadGraph(context, "sample_zoo_graph.json", "sample_node_info.json", "sample_edge_info.json");

        List<String> selected = new ArrayList<>();
        selected.add("lions");
        selected.add("gators");
        selected.add("arctic_foxes");
        Route route = new Route(graph, selected, "entrance_exit_gate");
        List<List<GraphNode>> plan = route.getRoute();

        assertEquals("entrance_exit_gate", plan.get(0).get(0).id);
        assertEquals("gators", plan.get(1).get(0).id);
        assertEquals("lions", plan.get(2).get(0).id);
        assertEquals("arctic_foxes", plan.get(3).get(0).id);
    }
}