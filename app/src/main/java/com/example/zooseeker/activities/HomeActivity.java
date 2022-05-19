package com.example.zooseeker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import com.example.zooseeker.R;
import com.example.zooseeker.adapters.AnimalAdapter;
import com.example.zooseeker.databinding.ActivityHomeBinding;
import com.example.zooseeker.models.Animal;
import com.example.zooseeker.viewmodels.HomeActivityViewModel;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.example.zooseeker.util.Alert;

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

        // Sets RecyclerView
        RecyclerView rv = binding.recyclerView;
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        AnimalAdapter adapter = new AnimalAdapter(this, viewModel);
        rv.setAdapter(adapter);

        // Observe changes
        viewModel.getAnimals().observe(this, adapter::setAnimals);

        binding.search.setOnQueryTextListener(this);
    }

    /**
     * Called when user presses directions button
     * @param view
     */
    public void onLaunchDirectionClicked(View view) {
        if (viewModel.numSelectedAnimals.get() == 0){
            Alert.emptyListAlert(this, "Please select some exhibits.");
        } else {
            Intent intent = new Intent(this, DirectionActivity.class);
            // Convert list of selected animals to list of their id strings
            ArrayList<String> selectedAnimals = new ArrayList<>(
                    viewModel.getSelectedAnimals().getValue()
                    .stream()
                    .map(a -> a.id)
                    .collect(Collectors.toList()));
            // Add to intent
            intent.putStringArrayListExtra("selected_animals", selectedAnimals);

            // Clear searchbar and close keyboard
            SearchView searchBar = binding.search;
            searchBar.setQuery("", false);
            searchBar.clearFocus();

            startActivity(intent);
        }
    }

    @Override
    public void onAnimalClick(int position) {
        // Add or remove animal from list
        Animal selectedAnimal = viewModel.getAnimals().getValue().get(position);
        viewModel.toggleSelectedAnimal(selectedAnimal);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        viewModel.submitSearch(this, query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // TODO: Implement autocomplete
        return false;
    }

    public ActivityHomeBinding getBinding() {
        return this.binding;
    }
}