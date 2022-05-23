package com.example.zooseeker.activities;

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
    private SharedPreferences sharedPreferences;
    private final String SHARRED_PREF = "SHARED_PREF";
    private final String ANIMALS_ID = "ANIMALS";
    private String id = "NULL";


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

            startActivity(intent);
        }
    }

    @Override
    public void onAnimalClick(int position) {
        // Add or remove animal from list
        viewModel.selectAnimalCommand.execute(new SelectedAnimalParams(position));

        sharedPreferences = getSharedPreferences(SHARRED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // editor.putInt(COUNT, viewModel.getCount());
        StringBuilder sb = new StringBuilder();
        List<Animal> list = viewModel.getSelectedAnimals();
        for (int i = 0; i < list.size(); i ++){
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
            SharedPreferences sharedPreferences = getSharedPreferences(SHARRED_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(ANIMALS_ID);
            editor.apply();
            this.recreate();
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        sharedPreferences = getSharedPreferences(SHARRED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
       // editor.putInt(COUNT, viewModel.getCount());
        StringBuilder sb = new StringBuilder();
        List<Animal> list = viewModel.getSelectedAnimals();
        for (int i = 0; i < list.size(); i ++){
            sb.append(list.get(i).id);
            sb.append("@");
        }
        editor.putString(ANIMALS_ID, sb.toString());
        editor.apply();
    }*/

    /*@Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARRED_PREF, MODE_PRIVATE);
        id = sharedPreferences.getString(ANIMALS_ID,null);

        String[] saved_id = id.split("[\\s@&.?$+-]+");
        List<Animal> animalList = new ArrayList<>();

        for(String s :saved_id){
            animalList.add(viewModel.searchInDatabaseById(this, s));
        }

        viewModel.setSelectedAnimals(animalList);
    }*/
}



