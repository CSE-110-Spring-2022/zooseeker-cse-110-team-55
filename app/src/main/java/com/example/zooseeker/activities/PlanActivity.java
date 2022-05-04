package com.example.zooseeker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.zooseeker.R;

public class PlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
    }
    public void onLaunchDirectionClicked(View view) {
        Intent intent = new Intent(this, DirectionActivity.class);
        startActivity(intent);
    }
    public void onBackClicked(View view) {
        finish();
    }
}