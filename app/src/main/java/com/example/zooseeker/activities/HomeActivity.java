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
import android.widget.TextView;

import com.example.zooseeker.R;
import com.example.zooseeker.adapters.AnimalAdapter;
import com.example.zooseeker.databinding.ActivityHomeBinding;
import com.example.zooseeker.models.Animal;
import com.example.zooseeker.models.SearchCommandParams;
import com.example.zooseeker.models.SelectedAnimalParams;
import com.example.zooseeker.viewmodels.HomeActivityViewModel;

import java.util.ArrayList;
import java.util.List;
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

        // Load saved animals and update view model
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        String id = sharedPreferences.getString(ANIMALS_ID, null);
        if (id != null) {
            String[] saved_id = id.split("[\\s@&.?$+-]+");
            List<Animal> animalList = new ArrayList<>();
            for (String s : saved_id) {
                animalList.add(viewModel.searchInDatabaseById(this, s));
            }
            viewModel.setSelectedAnimals(animalList);
            List<Animal> tempList = new ArrayList<>(animalList);
            viewModel.setAnimals(tempList);
        }

        // Load direction index if exists
        int curr_index = sharedPreferences.getInt(CURR_INDEX, -1);
        if (curr_index != -1) {
            Intent intent = new Intent(this, DirectionActivity.class);
            // Convert list of selected animals to list of their id strings
            ArrayList<String> selectedAnimals = new ArrayList<>(
                    viewModel.getSelectedAnimals()
                            .stream()
                            .map(a -> a.id)
                            .collect(Collectors.toList()));
            // Add to intent
            intent.putStringArrayListExtra("selected_animals", selectedAnimals);
            startActivity(intent);
        }

        binding.search.setOnQueryTextListener(this);
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
            // Convert list of selected animals to list of their id strings
            ArrayList<String> selectedAnimals = new ArrayList<>(
                    viewModel.getSelectedAnimals()
                            .stream()
                            .map(a -> a.id)
                            .collect(Collectors.toList()));
            // Add to intent
            intent.putStringArrayListExtra("selected_animals", selectedAnimals);

            // Clear searchbar and close keyboard
            SearchView searchBar = binding.search;
            searchBar.setQuery("", false);
            searchBar.clearFocus();

            // Todo comment here
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("CURR_INDEX", 0);
            editor.apply();

            startActivity(intent);
        }
    }

    @Override
    public void onAnimalClick(int position) {
        // Add or remove animal from list
        viewModel.selectAnimalCommand.execute(new SelectedAnimalParams(position));

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        StringBuilder sb = new StringBuilder();
        List<Animal> list = viewModel.getSelectedAnimals();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).id);
            sb.append("@");
        }
        editor.putString(ANIMALS_ID, sb.toString());
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

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
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



