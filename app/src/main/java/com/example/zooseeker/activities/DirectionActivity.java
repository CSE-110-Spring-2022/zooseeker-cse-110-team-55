package com.example.zooseeker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.zooseeker.R;
import com.example.zooseeker.databinding.ActivityDirectionBinding;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.models.Graph.GraphData.GraphEdge;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;
import com.example.zooseeker.models.Graph.SymmetricPair;
import com.example.zooseeker.viewmodels.PlanViewModel;

import java.util.List;

public class DirectionActivity extends AppCompatActivity {
    private PlanViewModel viewModel;
    private ActivityDirectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_direction);

        Intent intent = getIntent();
        viewModel = new ViewModelProvider(this).get(PlanViewModel.class);
        binding.setVm(viewModel);

        viewModel.getDirections().observe(this, graphNodes -> {
            StringBuilder stringBuilder = new StringBuilder();
            int size = graphNodes.size();
            for (int i = 0; i < size - 1; i++) {
                if (i == 0 || i == size - 2) {
                    stringBuilder.append("Proceed on ");
                } else {
                    stringBuilder.append("Continue to ");
                }

                Graph graph = viewModel.getGraph();
                SymmetricPair edgeKey = new SymmetricPair(graphNodes.get(i).id, graphNodes.get(i + 1).id);
                GraphEdge edge = graph.edges.get(edgeKey);

                stringBuilder.append(graph.edgeInfo.get(edge.id).street);
                stringBuilder.append(" for ");
                stringBuilder.append(edge.weight.intValue());
                stringBuilder.append(" feet towards ");
                stringBuilder.append(graph.nodeInfo.get(edge.target).name);
                stringBuilder.append("\n");
            }

            TextView textView = findViewById(R.id.direction_text);
            textView.setText(stringBuilder);
        });

        viewModel.initRoute(intent.getStringArrayListExtra("selected_animals"));
    }

    public void onLaunchEndClicked(View view) {
        viewModel.getNextDirections();
    }
}