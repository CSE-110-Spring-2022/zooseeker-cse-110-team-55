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
        viewModel = new ViewModelProvider(this).get(PlanViewModel.class);
        binding.setVm(viewModel);

        RecyclerView rv = findViewById(R.id.direction_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        DirectionAdapter adapter = new DirectionAdapter(viewModel.getGraph());
        rv.setAdapter(adapter);

        viewModel.getDirections().observe(this, graphNodes -> {
            List<GraphEdge> edges = new ArrayList<>();
            for (int i = 0; i < graphNodes.size() - 1; i++) {
                String source = graphNodes.get(i).id;
                String dest = graphNodes.get(i + 1).id;

                SymmetricPair key = new SymmetricPair(source, dest);
                GraphEdge existingEdge = viewModel.getGraph().edges.get(key);
                GraphEdge edge = new GraphEdge(existingEdge.id, existingEdge.weight, source, dest);
                edges.add(edge);
            }

            adapter.setDirections(edges);
        });

        viewModel.initRoute(intent.getStringArrayListExtra("selected_animals"));
    }

    public void onLaunchNextClicked(View view) {
        if (viewModel.remainingExhibits.get() == 2) {
            TextView button = binding.nextButton;
            button.setText("END");
        }
        if(viewModel.remainingExhibits.get() == 1) {
            finish();
        } else {
            viewModel.getNextDirections();
        }
    }
}