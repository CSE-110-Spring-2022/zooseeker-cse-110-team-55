package com.example.zooseeker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.example.zooseeker.R;
import com.example.zooseeker.adapters.AnimalAdapter;
import com.example.zooseeker.databinding.ActivityHomeBinding;
import com.example.zooseeker.models.Animal;
import com.example.zooseeker.models.AnimalItemDao;
import com.example.zooseeker.repositories.AnimalDatabase;
import com.example.zooseeker.viewmodels.HomeActivityViewModel;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements AnimalAdapter.OnAnimalClickListener, SearchView.OnQueryTextListener {
    private ActivityHomeBinding binding;
    private HomeActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create viewmodel instance
        viewModel = new ViewModelProvider(this).get(HomeActivityViewModel.class);
        // Set view binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        // Inject viewmodel instance
        binding.setVm(viewModel);

        // TODO: Observe changes of LiveData objects
        // Sets RecyclerView
        RecyclerView rv = binding.recyclerView;
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        AnimalAdapter adapter = new AnimalAdapter(this, viewModel);
        rv.setAdapter(adapter);

        viewModel.getAnimals().observe(this, adapter::setAnimals);

        binding.search.setOnQueryTextListener(this);
    }

    public void onLaunchDirectionClicked(View view) {
        Intent intent = new Intent(this, DirectionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAnimalClick(int position) {
        Animal selectedAnimal = viewModel.getAnimals().getValue().get(position);
        viewModel.toggleSelectedAnimal(selectedAnimal);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        List<Animal> results = viewModel.searchInDatabase(this, query);
        viewModel.setAnimals(results);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        List<Animal> results = viewModel.searchInDatabase(this, query);
        // TODO: Implement autocomplete
        Log.d("[HomeActivity]", String.valueOf(results.size()));
        return true;
    }

    public ActivityHomeBinding getBinding() {
        return this.binding;
    }

}