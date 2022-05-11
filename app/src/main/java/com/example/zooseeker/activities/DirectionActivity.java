package com.example.zooseeker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.zooseeker.R;
import com.example.zooseeker.adapters.DirectionAdapter;
import com.example.zooseeker.databinding.ActivityDirectionBinding;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.models.Graph.GraphData.GraphEdge;
import com.example.zooseeker.models.Graph.GraphData.GraphNode;
import com.example.zooseeker.models.Graph.SymmetricPair;
import com.example.zooseeker.viewmodels.PlanViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DirectionActivity extends AppCompatActivity {
    private PlanViewModel viewModel;
    private ActivityDirectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_direction);

        Intent intent = getIntent();
        // Set viewmodel
        viewModel = new ViewModelProvider(this).get(PlanViewModel.class);
        binding.setVm(viewModel);

        // Initialize recyclerview and adapter
        RecyclerView rv = findViewById(R.id.direction_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        DirectionAdapter adapter = new DirectionAdapter(viewModel.getGraph());
        rv.setAdapter(adapter);

        // Observe changes to list of current directions
        viewModel.getDirections().observe(this, graphNodes -> {
            List<GraphEdge> edges = new ArrayList<>();
            // Loop through each edge in new directions
            for (int i = 0; i < graphNodes.size() - 1; i++) {
                String source = graphNodes.get(i).id;
                String dest = graphNodes.get(i + 1).id;

                // Get graph edge
                SymmetricPair key = new SymmetricPair(source, dest);
                GraphEdge existingEdge = viewModel.getGraph().edges.get(key);
                // Create duplicate edge to ensure source and destination nodes are not swapped
                GraphEdge edge = new GraphEdge(existingEdge.id, existingEdge.weight, source, dest);
                // Add graph edge to list of directions
                edges.add(edge);
            }

            // Update adapter with new directions
            adapter.setDirections(edges);
        });

        // Initialize the route
        viewModel.initRoute(intent.getStringArrayListExtra("selected_animals"));
    }

    /**
     * Called when the next button is clicked
     * @param view
     */
    public void onLaunchNextClicked(View view) {
        // TODO: Fix control flow
        // Display end only when on last exhibit
        if (viewModel.remainingExhibits.get() == 2) {
            TextView button = binding.nextButton;
            button.setText("END");
        }

        // Close activity if no more exhibits remain
        if(viewModel.remainingExhibits.get() == 1) {
            finish();
        } else {
            // Get directions to next exhibit
            viewModel.getNextDirections();
        }
    }
}