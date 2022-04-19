package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.zooseeker.adapters.AnimalAdapter;
import com.example.zooseeker.databinding.ActivityHomeBinding;
import com.example.zooseeker.databinding.ActivityMainBinding;
import com.example.zooseeker.models.Animal;
import com.example.zooseeker.viewmodels.HomeActivityViewModel;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements AnimalAdapter.OnAnimalClickListener {
    private ActivityHomeBinding binding;
    private HomeActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Create viewmodel instance
        viewModel = new ViewModelProvider(this).get(HomeActivityViewModel.class);
        // Set view binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        // Inject viewmodel instance
        binding.setVm(viewModel);

        // TODO: Observe changes of LiveData objects
        // Sets RecyclerView
        RecyclerView rv = binding.recyclerView;
        rv.setHasFixedSize(true);
        AnimalAdapter adapter = new AnimalAdapter(this);
        rv.setAdapter(adapter);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }

    }

    private List<Animal> doMySearch(String query) {

        AnimalItemDao animalItemDao = AnimalDatabase.getSingleton(this).animalItemDao();
        List<Animal> animalItems = animalItemDao.get(query);
        Log.d("SearchActivity", String.valueOf(animalItems.size()));
        return animalItems;

    }

    public void onLaunchPlanClicked(View view) {
        Intent intent = new Intent(this, PlanActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAnimalClick(int position) {
        Animal selectedAnimal = viewModel.getAnimals().getValue().get(position);
        viewModel.toggleSelectedAnimal(selectedAnimal);
    }



}