package com.example.zooseeker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.zooseeker.R;
import com.example.zooseeker.adapters.DirectionAdapter;
import com.example.zooseeker.databinding.ActivityDirectionBinding;
import com.example.zooseeker.fragments.RouteSummaryFragment;
import com.example.zooseeker.models.Graph.GraphData.GraphEdge;
import com.example.zooseeker.models.Graph.SymmetricPair;
import com.example.zooseeker.viewmodels.PlanViewModel;

import java.util.ArrayList;
import java.util.List;

public class DirectionActivity extends AppCompatActivity {
    private PlanViewModel vm;
    private ActivityDirectionBinding binding;
    private Button buttonDialog;
    private RouteSummaryFragment routeSummaryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_direction);
        // Set viewmodel
        vm = new ViewModelProvider(this).get(PlanViewModel.class);
        binding.setVm(vm);

        // Initialize the route
        vm.initRoute(intent.getStringArrayListExtra("selected_animals"));

        // Initialize recyclerview and adapter
        RecyclerView rv = findViewById(R.id.direction_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        DirectionAdapter adapter = new DirectionAdapter(vm.getRoute().getGraph());
        rv.setAdapter(adapter);

        // Observe changes to list of current directions
        vm.getDirections().observe(this, graphNodes -> {
            List<GraphEdge> edges = vm.getRoute().getGraph().getEdgesFromNodes(graphNodes);
            // Update adapter with new directions
            adapter.setDirections(edges);
        });

        // Show route summary fragment
        buttonDialog = findViewById(R.id.routeSummaryButton);
        buttonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routeSummaryFragment = new RouteSummaryFragment();
                routeSummaryFragment.show(getSupportFragmentManager(), "TAG");
            }
        });
    }

    // Hide route summary fragment
    public void onHideClicked(View view){
        routeSummaryFragment.dismiss();
    }

    /**
     * Called when the next button is clicked
     * @param view
     */
    public void onLaunchNextClicked(View view) {
        vm.nextExhibitCommand.execute(this);
    }
}