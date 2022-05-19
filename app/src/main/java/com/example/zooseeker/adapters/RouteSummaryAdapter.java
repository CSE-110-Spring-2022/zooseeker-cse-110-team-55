package com.example.zooseeker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zooseeker.R;
import com.example.zooseeker.models.Graph;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class RouteSummaryAdapter extends RecyclerView.Adapter<RouteSummaryAdapter.RouteSummaryHolder> {
    private Graph graph;
    private List<Graph.GraphData.GraphNode> nodes = new ArrayList<>();

    public RouteSummaryAdapter(Graph graph) {
        this.graph = graph;

    }

    @NonNull
    @Override
    public RouteSummaryAdapter.RouteSummaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.direction_item, parent, false);
        return new RouteSummaryAdapter.RouteSummaryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteSummaryAdapter.RouteSummaryHolder holder, int position) {
        RouteSummaryHolder routeSummaryHolder = (RouteSummaryHolder) holder;
        //super.onCreateViewHolder(routeSummaryHolder);
        routeSummaryHolder.itemView.
        setContentView(R.layout.activity_plan);
        TextView name = findViewById(R.id.path_tv);
        //sb.append(graph.nodeInfo.get(nodes.get(position)).name;
    }

    @Override
    public int getItemCount() {
        return nodes.size();
    }

    public void setDirections(List<Graph.GraphData.GraphNode> nodes) {
        this.nodes.clear();
        this.nodes = nodes;
        notifyDataSetChanged();
    }

    class RouteSummaryHolder extends RecyclerView.ViewHolder {
        public TextView directions;
        public TextView distance;

        public RouteSummaryHolder(@NonNull View itemView) {
            super(itemView);

            directions = itemView.findViewById(R.id.path_tv);
            distance = itemView.findViewById(R.id.dist_tv);
        }
    }

}
