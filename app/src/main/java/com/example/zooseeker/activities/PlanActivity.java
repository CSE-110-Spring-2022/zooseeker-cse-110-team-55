package com.example.zooseeker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.zooseeker.R;
import com.example.zooseeker.viewmodels.PlanViewModel;

public class PlanActivity extends AppCompatActivity {
    private PlanViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        Intent intent = getIntent();
        viewModel = new ViewModelProvider(this).get(PlanViewModel.class);
        viewModel.setPlan(viewModel.getRoute(intent.getStringArrayListExtra("selected_animals")));
    }




    public void onLaunchDirectionClicked(View view) {
        Intent intent = new Intent(this, DirectionActivity.class);
        startActivity(intent);
    }

    public void onBackClicked(View view) {
        finish();
    }
}