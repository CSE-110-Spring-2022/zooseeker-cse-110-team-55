package com.example.zooseeker.activities;

import static com.example.zooseeker.util.Constant.ANIMALS_ID;
import static com.example.zooseeker.util.Constant.CURR_INDEX;
import static com.example.zooseeker.util.Constant.SHARED_PREF;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.example.zooseeker.R;
import com.example.zooseeker.adapters.AnimalAdapter;
import com.example.zooseeker.databinding.ActivityHomeBinding;
import com.example.zooseeker.models.db.Animal.AnimalDisplay;
import com.example.zooseeker.models.command.SearchCommandParams;
import com.example.zooseeker.models.command.SelectedAnimalParams;
import com.example.zooseeker.util.PermissionChecker;
import com.example.zooseeker.viewmodels.HomeActivityViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.zooseeker.util.Alert;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HomeActivity extends AppCompatActivity implements AnimalAdapter.OnAnimalClickListener, SearchView.OnQueryTextListener {
    private ActivityHomeBinding binding;
    private HomeActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new PermissionChecker(this).ensurePermissions();

        // Create view model instance
        viewModel = new ViewModelProvider(this).get(HomeActivityViewModel.class);
        // Set view binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        // Inject view model instance
        binding.setVm(viewModel);

        // Sets RecyclerView
        RecyclerView rv = binding.recyclerView;
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        AnimalAdapter adapter = new AnimalAdapter(this, viewModel);
        rv.setAdapter(adapter);

        // Observe changes
        viewModel.getAnimals().observe(this, adapter::setAnimals);

        // Load shared preferences and update home activity view model
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        String saved = sharedPreferences.getString(ANIMALS_ID, null);
        if (saved != null) {
            // Load selected animals from json
            var type = new TypeToken<List<AnimalDisplay>>() {
            }.getType();
            List<AnimalDisplay> animals = new Gson().fromJson(saved, type);
            // Add to list
            var temp = new ArrayList<>(animals);
            viewModel.setSelectedAnimals(temp);
            viewModel.setAnimals(animals);
        }

        // TODO: Load last known location
        // Load shared preferences and launch direction activity if direction index != -1
        int curr_index = sharedPreferences.getInt(CURR_INDEX, -1);
        if (curr_index != -1) {
            Intent intent = new Intent(this, DirectionActivity.class);
            addSelectionToIntent(intent);
            startActivity(intent);
        }
        binding.search.setOnQueryTextListener(this);
    }

    private void addSelectionToIntent(Intent intent) {
        // Convert list of selected animals to list of their id strings
        Set<String> selectedIds = new HashSet<>();
        HashMap<String, List<String>> exhibitGroups = new HashMap<>();
        for (var animal : viewModel.getSelectedAnimals()) {
            // Add the animal as an exhibit if not part of a group
            if (animal.groupId == null) {
                selectedIds.add(animal.id);
                continue;
            }

            // Otherwise, add it as a group
            if (!selectedIds.contains(animal.groupId)) {
                selectedIds.add(animal.groupId);
                exhibitGroups.put(animal.groupId, new ArrayList<>(List.of(animal.name)));
            } else {
                exhibitGroups.get(animal.groupId).add(animal.name);
            }
        }
        var groups = new Gson().toJson(exhibitGroups);
        intent.putExtra("exhibit_groups", groups);

        // Add to intent
        intent.putStringArrayListExtra("selected_animals", new ArrayList<>(selectedIds));
    }

    /**
     * Called when user presses directions button
     *
     * @param view
     */
    public void onLaunchDirectionClicked(View view) {
        if (viewModel.numSelectedAnimals.get() == 0) {
            Alert.emptyListAlert(this, "Please select some exhibits.");
        } else {
            Intent intent = new Intent(this, DirectionActivity.class);
            addSelectionToIntent(intent);

            // Clear searchbar and close keyboard
            SearchView searchBar = binding.search;
            searchBar.setQuery("", false);
            searchBar.clearFocus();

            // Update direction index in shared preferences to 0 when direction activity is launched
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(CURR_INDEX, 0);
            editor.apply();

            startActivity(intent);
        }
    }

    @Override
    public void onAnimalClick(int position) {
        // Add or remove animal from list
        viewModel.selectAnimalCommand.execute(new SelectedAnimalParams(position));

        // Reconstruct and update animal id string in shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        var list = viewModel.getSelectedAnimals();
        String saved = new Gson().toJson(list);
        editor.putString(ANIMALS_ID, saved);
        editor.apply();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        SearchCommandParams params = new SearchCommandParams(this, query);
        viewModel.searchCommand.execute(params);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query.equals("")) {
            SearchCommandParams params = new SearchCommandParams(this, query);
            viewModel.searchCommand.execute(params);
        }
        return false;
    }

    public ActivityHomeBinding getBinding() {
        return this.binding;
    }

    // Create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.eraseSelectedExhibitsButton) {
            viewModel.clear();
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(ANIMALS_ID);
            editor.remove(CURR_INDEX);
            editor.apply();
            this.recreate();
        }
        return super.onOptionsItemSelected(item);
    }
}



