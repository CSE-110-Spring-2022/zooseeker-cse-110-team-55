package com.example.zooseeker.models;

import android.content.Context;

import com.example.zooseeker.models.Graph.GraphData.GraphEdge;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class Graph {
    public Map<String, List<GraphNode>> adjacencyList;
    public Map<String, GraphNode> nodes;
    public Map<SymmetricPair, Double> edgeWeights;

    public Graph() {
        adjacencyList = new HashMap<>();
        edgeWeights = new HashMap<>();
        nodes = new HashMap<>();
    }

    public void loadGraph(Context context, String path) {
        GraphData graphData = loadGraphJSON(context, path);

        nodes = graphData.nodes.stream()
                .collect(Collectors.toMap(v -> v.id, datum -> datum));

        for (GraphEdge e : graphData.edges) {
            adjacencyList.computeIfAbsent(e.source, k -> new ArrayList<>());
            adjacencyList.computeIfAbsent(e.target, k -> new ArrayList<>());

            adjacencyList.get(e.source).add(nodes.get(e.target));
            adjacencyList.get(e.target).add(nodes.get(e.source));

            edgeWeights.put(new SymmetricPair(e.source, e.target), e.weight);
        }
    }

    public List<List<GraphNode>> createPlan(ArrayList<GraphNode> selected, GraphNode start) {
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
            // mark u as planned
            u = partialPath.get(partialPath.size() - 1);
            unplanned_exhibits.remove(u);
            // Add u to navigation list
            plan.add(partialPath);
        }

        return plan;
    }

    private ArrayList<GraphNode> pathToNearestNeighbor(GraphNode u, List<GraphNode> unplanned) {
        Map<String, GraphNode> parent = new HashMap<>();
        Map<String, Double> dist = new HashMap<>();
        PriorityQueue<GraphNode> queue = new PriorityQueue<>(10,
                (o1, o2) -> (int) (dist.get(o1.id) - dist.get(o2.id)));

        queue.add(u);
        dist.put(u.id, (double) 0);

        while (!queue.isEmpty()) {
            GraphNode cur = queue.poll();
            if (unplanned
                    .stream()
                    .anyMatch(n -> n.id.equals(cur.id))
            ) return backtrace(cur, parent);

            List<GraphNode> neighbors = adjacencyList.get(cur.id);
            for (GraphNode other : neighbors) {
                SymmetricPair key = new SymmetricPair(cur.id, other.id);
                double edgeWeight = edgeWeights.get(key);
                if (dist.getOrDefault(other.id, Double.MAX_VALUE) > dist.get(cur.id) + edgeWeight) {
                    dist.put(other.id, dist.get(cur.id) + edgeWeight);
                    queue.add(other);
                    parent.put(other.id, cur);
                }
            }
        }

        return null;
    }

    private static ArrayList<GraphNode> backtrace(GraphNode end, Map<String, GraphNode> parent) {
        ArrayList<GraphNode> result = new ArrayList<GraphNode>();
        GraphNode cur = end;
        while (cur != null) {
            result.add(0, cur);
            cur = parent.get(cur.id);
        }

        return result;
    }


    public static GraphData loadGraphJSON(Context context, String path) {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            return gson.fromJson(reader, GraphData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class SymmetricPair {
        public String k1;
        public String k2;

        public SymmetricPair(String k1, String k2) {
            this.k1 = k1;
            this.k2 = k2;
        }

        @Override
        public boolean equals(Object obj) {
            SymmetricPair other = (SymmetricPair) obj;
            return (this.k1.equals(other.k1) && this.k2.equals(other.k2)
                    || this.k1.equals(other.k2) && this.k2.equals(other.k1));
        }

        @Override
        public int hashCode() {
            return k1.hashCode() ^ k2.hashCode();
        }
    }

    public static class GraphData {
        public List<GraphNode> nodes;
        public List<GraphEdge> edges;

        public static class GraphNode {
            String id;
        }

        public static class GraphEdge {
            public String id;
            public Double weight;
            public String source;
            public String target;
        }
    }
}
