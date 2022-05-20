package com.example.zooseeker.viewmodels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zooseeker.R;
import com.example.zooseeker.adapters.RouteSummaryAdapter;
import com.example.zooseeker.models.Graph;
import com.example.zooseeker.util.Helper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RouteSummaryFragment extends BottomSheetDialogFragment {

    public RouteSummaryFragment() {

    }

    @Nullable
    //@Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstance) {
        View view = inflater.inflate(R.layout.activity_plan, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        PlanViewModel planViewModel = new ViewModelProvider(requireActivity()).get(PlanViewModel.class);


        RecyclerView recyclerView = view.findViewById(R.id.plansummary_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setHasFixedSize(true);
        RouteSummaryAdapter routeSummaryAdapter = new RouteSummaryAdapter(planViewModel.getGraph(), planViewModel);
        recyclerView.setAdapter(routeSummaryAdapter);

        List<Graph.GraphData.GraphNode> graphNodes = new ArrayList<Graph.GraphData.GraphNode>();

        for (int i = 0; i < planViewModel.getPlan().size() - 1; i++) {

            graphNodes.add(Helper.getLast(planViewModel.getPlan().get(i)));


        }

        routeSummaryAdapter.setDirections(graphNodes);


    }

}
