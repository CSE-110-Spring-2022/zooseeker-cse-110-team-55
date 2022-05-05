package com.example.zooseeker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.zooseeker.R;
import com.example.zooseeker.databinding.ActivityDirectionBinding;
import com.example.zooseeker.databinding.ActivityHomeBinding;
import com.example.zooseeker.models.Graph;
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

        viewModel.initRoute(intent.getStringArrayListExtra("selected_animals"));
    }

    public void onLaunchEndClicked(View view) { finish(); }
}