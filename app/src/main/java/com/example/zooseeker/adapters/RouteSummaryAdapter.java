package com.example.zooseeker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zooseeker.R;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.viewmodels.PlanViewModel;

import java.util.ArrayList;
import java.util.List;

public class RouteSummaryAdapter extends RecyclerView.Adapter<RouteSummaryAdapter.RouteSummaryHolder> {
    private Graph graph;
    private List<Graph.GraphData.GraphNode> nodes = new ArrayList<>();
    private PlanViewModel planViewModel;

    public RouteSummaryAdapter(Graph graph, PlanViewModel planViewModel) {
        this.graph = graph;
        this.planViewModel = planViewModel;
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
        String node = graph.nodeInfo.get(nodes.get(position).id).name;

        StringBuilder count = new StringBuilder();
        count.append(position + 1);
        count.append(". ");
        count.append(graph.nodeInfo.get(nodes.get(position).id).name);

        holder.directions.setText(count);

        StringBuilder dist = new StringBuilder();
        dist.append(planViewModel.getRoute().getDistancesToEachExhibit()[position]);
        dist.append(" ft");

        holder.distance.setText(dist);
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