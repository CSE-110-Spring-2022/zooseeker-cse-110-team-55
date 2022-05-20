package com.example.zooseeker.models;

import android.util.Log;

import com.example.zooseeker.models.Graph.GraphData.GraphNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class Route {
    private Graph graph;
    private int[] _distances;
    private List<List<GraphNode>> route;

    public Route(Graph graph, List<String> exhibitIds, String startExhibit) {
        this.graph = graph;
        createRouteThroughExhibits(exhibitIds, startExhibit);
    }

    public void createRouteThroughExhibits(List<String> exhibitIds, String startExhibit) {
        // Convert exhibit IDs to graph nodes
        List<GraphNode> selectedAnimalNodes = new ArrayList<>(
                exhibitIds
                        .stream()
                        .map(a -> graph.nodes.get(a))
                        .collect(Collectors.toList())
        );
        GraphNode start = graph.nodes.get(startExhibit);

        // Set instance fields
        route = createShortestRoute(selectedAnimalNodes, start);
        _distances = calculateDistances();
    }

    public List<List<GraphNode>> getRoute() {
        return route;
    }

    public int[] getDistancesToEachExhibit() {
        return _distances;
    }

    /**
     * Calculates distances to and from each exhibit in the route
     */
    private int[] calculateDistances() {
        // Calculate distances to each exhibit
        int[] distances = new int[route.size()];
        for (int i = 0; i < route.size(); i++) {
            List<GraphNode> exhibitDirections = route.get(i);
            int totalWeight = 0;
            for (int j = 0; j < exhibitDirections.size() - 1; j++) {
                GraphNode source = exhibitDirections.get(j);
                GraphNode dest = exhibitDirections.get(j + 1);

                Graph.SymmetricPair edge = new Graph.SymmetricPair(source.id, dest.id);
                totalWeight += graph.edges.get(edge).weight;
            }
            distances[i] = totalWeight;
        }

        return distances;
    }

    // Algo
    private List<List<GraphNode>> createShortestRoute(List<GraphNode> selected, GraphNode start) {
        List<List<GraphNode>> plan = new ArrayList<>();
        // exhibits to be planned
        List<GraphNode> unplanned_exhibits = new ArrayList<>(selected);
        // u = entrance
        GraphNode u = start;
        List<GraphNode> partialPath;
        // Run dijkstra's on u
        while (unplanned_exhibits.size() > 0) {
            // Find nearest selected neighbor of u
            // Set u to nearest neighbor
            partialPath = pathToNearestNeighbor(u, unplanned_exhibits);
            Log.d("Partial Path Size ", String.valueOf(partialPath.size()));

            // mark u as planned
            u = partialPath.get(partialPath.size() - 1);
            unplanned_exhibits.remove(u);

            // Add u to navigation list
            plan.add(partialPath);
            Log.d("Plan Size", String.valueOf(plan.size()));
            Log.d("Added element", String.valueOf(u.id));
        }

        plan.add(shortestPathToNode(u, start));

        return plan;
    }

    private ArrayList<GraphNode> shortestPathToNode(GraphNode start, GraphNode end) {
        return pathToNearestNeighbor(start, new ArrayList<>(Collections.singleton(end)));
    }

    /**
     * Finds path to nearest neighbor of unplanned nodes starting from node u
     * @param u
     * @param unplanned
     * @return
     */
    private ArrayList<GraphNode> pathToNearestNeighbor(GraphNode u, List<GraphNode> unplanned) {
        Map<String, GraphNode> parent = new HashMap<>();
        Map<String, Double> dist = new HashMap<>();
        PriorityQueue<GraphNode> queue = new PriorityQueue<>(10,
                (o1, o2) -> (int) (dist.get(o1.id) - dist.get(o2.id)));

        // Init dijkstra's
        queue.add(u);
        dist.put(u.id, (double) 0);

        // Continue while there are more nodes
        while (!queue.isEmpty()) {
            GraphNode cur = queue.poll();
            // If unplanned node is found, return path to that node
            if (unplanned
                    .stream()
                    .anyMatch(n -> n.id.equals(cur.id))
            ) return backtrace(cur, parent);

            // Get neighboring nodes
            List<GraphNode> neighbors = graph.adjacencyList.get(cur.id);
            for (GraphNode other : neighbors) {
                Graph.SymmetricPair key = new Graph.SymmetricPair(cur.id, other.id);
                double edgeWeight = graph.edges.get(key).weight;
                // If distance to neighbor is closer, update distance and re-insert into priority queue
                if (dist.getOrDefault(other.id, Double.MAX_VALUE) > dist.get(cur.id) + edgeWeight) {
                    Log.d("New Weight: ", String.valueOf((dist.get(cur.id))));
                    dist.put(other.id, dist.get(cur.id) + edgeWeight);
                    queue.add(other);
                    parent.put(other.id, cur);
                    Log.d("Update Weight of Node ", String.valueOf(other.id));
                }
            }
        }

        return null;
    }

    private ArrayList<GraphNode> backtrace(GraphNode end, Map<String, GraphNode> parent) {
        ArrayList<GraphNode> result = new ArrayList<GraphNode>();
        GraphNode cur = end;
        // Traverse parent pointers and add to list of nodes
        while (cur != null) {
            result.add(0, cur);
            Log.d("Added to result ", String.valueOf(result.get(0)));
            cur = parent.get(cur.id);
        }

        return result;
    }
    // End Algo
}
