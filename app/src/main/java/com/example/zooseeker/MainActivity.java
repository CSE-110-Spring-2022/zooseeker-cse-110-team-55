package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.zooseeker.adapters.AnimalAdapter;
import com.example.zooseeker.databinding.ActivityMainBinding;
import com.example.zooseeker.models.Animal;
import com.example.zooseeker.viewmodels.MainActivityViewModel;

import java.util.List;

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

        // Sets RecyclerView
        RecyclerView rv = binding.recyclerView;
        rv.setHasFixedSize(true);
        AnimalAdapter adapter = new AnimalAdapter();
        rv.setAdapter(adapter);

        // Observe changes of animals list
        viewModel.getAnimals().observe(this, adapter::setAnimals);
    }
}