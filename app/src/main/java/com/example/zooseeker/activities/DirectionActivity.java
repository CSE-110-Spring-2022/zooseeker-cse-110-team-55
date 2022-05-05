package com.example.zooseeker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.zooseeker.R;
import com.example.zooseeker.databinding.ActivityDirectionBinding;
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

        //new
        List<Graph.GraphData.GraphNode> directions = viewModel.getDirections().getValue();
        StringBuilder stringBuilder = new StringBuilder();
        int size = directions.size();
        for(int i =0; i<size ; i++){
            if(i==0 || i==size-1){
                stringBuilder.append("Proceed ");
            }else{
                stringBuilder.append("Continue to ");
            }
            stringBuilder.append(directions.get(i).id);
            stringBuilder.append("\n");
        }

        TextView textView = findViewById(R.id.direction_text);
        textView.setText(stringBuilder);
    }

    public void onLaunchEndClicked(View view) {
        viewModel.getNextDirections();
    }
}