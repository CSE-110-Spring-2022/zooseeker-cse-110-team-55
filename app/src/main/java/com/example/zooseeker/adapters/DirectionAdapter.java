package com.example.zooseeker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zooseeker.R;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.models.Graph.GraphData.GraphEdge;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;
import com.example.zooseeker.models.Graph.SymmetricPair;

import java.util.ArrayList;
import java.util.List;

public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.DirectionHolder> {
    private Graph graph;
    private List<GraphEdge> directions = new ArrayList<>();

    public DirectionAdapter(Graph graph) {
        this.graph = graph;
    }

    @NonNull
    @Override
    public DirectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.direction_item, parent, false);
        return new DirectionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DirectionHolder holder, int position) {
        // TODO: Add logic to determine if the Detailed toggle is active and handle accordingly
        StringBuilder sb = new StringBuilder();
        if (position == 0 || position == directions.size() - 1) {
            sb.append("Proceed on ");
        } else {
            sb.append("Continue to ");
        }

        GraphEdge edge = directions.get(position);
        sb.append(graph.edgeInfo.get(edge.id).street);
        sb.append(" towards ");
        sb.append(graph.nodeInfo.get(edge.target).name);

        holder.directions.setText(sb);
        holder.distance.setText(String.format("%d feet", (int) directions.get(position).weight));
    }

    @Override
    public int getItemCount() {
        return directions.size();
    }

    public void setDirections(List<GraphEdge> directions) {
        this.directions.clear();
        this.directions = directions;
        notifyDataSetChanged();
    }

    class DirectionHolder extends RecyclerView.ViewHolder {
        private TextView directions;
        private TextView distance;

        public DirectionHolder(@NonNull View itemView) {
            super(itemView);

            directions = itemView.findViewById(R.id.path_tv);
            distance = itemView.findViewById(R.id.dist_tv);
        }
    }
}
