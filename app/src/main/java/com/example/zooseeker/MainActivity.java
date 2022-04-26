package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.zooseeker.databinding.ActivityMainBinding;
import com.example.zooseeker.viewmodels.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set view binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // Inject viewmodel instance
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        // TODO: Observe changes of LiveData objects

        Intent intent = new Intent (this, HomeActivity.class);
        startActivity(intent);
    }

    public void onLaunchPlanClicked(View view) {
        Intent intent = new Intent(this, PlanActivity.class);
        startActivity(intent);
    }
}