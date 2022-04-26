package com.example.zooseeker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.zooseeker.R;
import com.example.zooseeker.adapters.AnimalAdapter;
import com.example.zooseeker.databinding.ActivityAnimalListBinding;
import com.example.zooseeker.models.Animal;
import com.example.zooseeker.viewmodels.AnimalListViewModel;

public class AnimalListActivity extends AppCompatActivity implements AnimalAdapter.OnAnimalClickListener {

    private ActivityAnimalListBinding binding;
    private AnimalListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create viewmodel instance
        viewModel = new ViewModelProvider(this).get(AnimalListViewModel.class);

        // Set view binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_animal_list);
        // Inject viewmodel instance
        binding.setVm(viewModel);

        // Sets RecyclerView
        RecyclerView rv = binding.recyclerView;
        rv.setHasFixedSize(true);
        AnimalAdapter adapter = new AnimalAdapter(this);
        rv.setAdapter(adapter);

        // Observe changes of animals list
        viewModel.getAnimals().observe(this, adapter::setAnimals);
    }

    @Override
    public void onAnimalClick(int position) {
        Animal selectedAnimal = viewModel.getAnimals().getValue().get(position);
        viewModel.toggleSelectedAnimal(selectedAnimal);
    }
}