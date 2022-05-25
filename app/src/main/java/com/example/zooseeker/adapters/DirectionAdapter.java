package com.example.zooseeker.adapters;

import static com.example.zooseeker.util.Helper.getLast;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zooseeker.R;
import com.example.zooseeker.models.DirectionItem;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.models.Graph.GraphData.GraphEdge;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;
import com.example.zooseeker.models.Graph.SymmetricPair;

import java.util.ArrayList;
import java.util.List;

public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.DirectionHolder> {
    private Graph graph;
    private List<DirectionItem> directions = new ArrayList<>();

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
            sb.append("Continue on ");
        }

        DirectionItem edge = directions.get(position);
        sb.append(String.format("%s towards %s", edge.streetName, edge.target));

        // Format exhibit groups
        if (edge.containedExhibits != null) {
            sb.append(" and find ");
            if (edge.containedExhibits.size() == 1) {
                sb.append(getLast(edge.containedExhibits));
            } else if (edge.containedExhibits.size() == 2) {
                sb.append(edge.containedExhibits.get(0));
                sb.append(" and ");
                sb.append(edge.containedExhibits.get(1));
            } else {
                for (int i = 0; i < edge.containedExhibits.size() - 1; i++) {
                    sb.append(edge.containedExhibits.get(i)).append(", ");
                }
                sb.append("and ").append(getLast(edge.containedExhibits));
            }
        }

        holder.directions.setText(sb);
        holder.distance.setText(String.format("%d ft", (int) directions.get(position).weight));
    }

    @Override
    public int getItemCount() {
        return directions.size();
    }

    public void setDirections(List<DirectionItem> directions) {
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
