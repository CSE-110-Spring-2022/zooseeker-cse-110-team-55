package com.example.zooseeker.viewmodels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zooseeker.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class RouteSummaryFragment extends BottomSheetDialogFragment {

    public RouteSummaryFragment() {

    }

    @Nullable
    //@Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstance ) {
        View view = inflater.inflate(R.layout.activity_plan, container, false);
        //Button btnAdd = view.findViewById(R.id.)
        RecyclerView recyclerView = view.findViewById(R.id.plansummary_rv);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        PlanViewModel planViewModel = new ViewModelProvider(requireActivity()).get(PlanViewModel.class);
        planViewModel.getGraph();
        // pass into adapter
        planViewModel.getPlan()

        RecyclerView recyclerView = view.findViewById(R.id.plansummary_rv);

    }

}
