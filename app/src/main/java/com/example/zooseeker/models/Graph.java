package com.example.zooseeker.models;

import android.content.Context;
import android.util.Log;

import com.example.zooseeker.models.Graph.GraphData.GraphEdge;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class Graph {
    public Map<String, List<GraphNode>> adjacencyList;
    public Map<String, GraphNode> nodes;
    public Map<SymmetricPair, GraphEdge> edges;
    public Map<String, NodeInfo> nodeInfo;
    public Map<String, EdgeInfo> edgeInfo;

    public Graph() {
        adjacencyList = new HashMap<>();
        edges = new HashMap<>();
        nodes = new HashMap<>();
    }

    // Graph Loading
    public void loadGraph(Context context, String graphPath, String nodesPath, String edgesPath) {
        GraphData graphData = loadGraphJSON(context, graphPath);

        nodes = graphData.nodes.stream()
                .collect(Collectors.toMap(v -> v.id, datum -> datum));

        for (GraphEdge e : graphData.edges) {
            adjacencyList.computeIfAbsent(e.source, k -> new ArrayList<>());
            adjacencyList.computeIfAbsent(e.target, k -> new ArrayList<>());

            adjacencyList.get(e.source).add(nodes.get(e.target));
            adjacencyList.get(e.target).add(nodes.get(e.source));

            edges.put(new SymmetricPair(e.source, e.target), e);
        }

        this.nodeInfo = loadNodeInfo(context, nodesPath);
        this.edgeInfo = loadEdgeInfo(context, edgesPath);
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

    public static Map<String, EdgeInfo> loadEdgeInfo(Context context, String path) {
        InputStream inputStream;
        try {
            inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();

            Type type = new TypeToken<List<EdgeInfo>>(){}.getType();
            List<EdgeInfo> edgeInfo = gson.fromJson(reader, type);

            return edgeInfo
                    .stream()
                    .collect(Collectors.toMap(edge -> edge.id, info -> info));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, NodeInfo> loadNodeInfo(Context context, String path) {
        InputStream inputStream;
        try {
            inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();

            Type type = new TypeToken<List<NodeInfo>>(){}.getType();
            List<NodeInfo> nodeInfo = gson.fromJson(reader, type);

            return nodeInfo
                    .stream()
                    .collect(Collectors.toMap(node -> node.id, info -> info));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    // End Graph Loading

    public List<GraphEdge> getEdgesFromNodes(List<GraphNode> nodes) {
        List<GraphEdge> edges = new ArrayList<>();
        // Loop through each edge in new directions
        for (int i = 0; i < nodes.size() - 1; i++) {
            String source = nodes.get(i).id;
            String dest = nodes.get(i + 1).id;

            // Get graph edge
            SymmetricPair key = new SymmetricPair(source, dest);
            GraphEdge existingEdge = this.edges.get(key);
            // Create duplicate edge to ensure source and destination nodes are not swapped
            GraphEdge edge = new GraphEdge(existingEdge.id, existingEdge.weight, source, dest);
            // Add graph edge to list of directions
            edges.add(edge);
        }

        return edges;
    }

    /**
     * Helper class for symmetric hashing
     */
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
            public String id;
        }

        public static class GraphEdge {
            public String id;
            public double weight;
            public String source;
            public String target;

            public GraphEdge(String id, double weight, String source, String target) {
                this.id = id;
                this.weight = weight;
                this.source = source;
                this.target = target;
            }
        }
    }

    public static class NodeInfo {
        public static enum Kind {
            // The SerializedName annotation tells GSON how to convert
            // from the strings in our JSON to this Enum.
            @SerializedName("gate") GATE,
            @SerializedName("exhibit") EXHIBIT,
            @SerializedName("exhibit_group") EXHIBIT_GROUP,
            @SerializedName("intersection") INTERSECTION
        }

        public String id;
        public Kind kind;
        public String name;
        public double lat;
        public double lng;
        public List<String> tags;
    }

    public static class EdgeInfo {
        public String id;
        public String street;
    }
}
