package com.example.zooseeker;


import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.widget.SearchView;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.zooseeker.activities.HomeActivity;
import com.example.zooseeker.databinding.ActivityHomeBinding;
import com.example.zooseeker.models.Animal;
import com.example.zooseeker.repositories.AnimalItemDao;
import com.example.zooseeker.repositories.AnimalDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AnimalActivityTest {
    AnimalDatabase testDb;
    AnimalItemDao animalItemDao;

    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, AnimalDatabase.class)
                .allowMainThreadQueries()
                .build();
        AnimalDatabase.injectTestDatabase(testDb);

        List<Animal> animals = Animal.loadJSON(context, "sample_node_info.json");
        animalItemDao = testDb.animalItemDao();
        animalItemDao.insertAll(animals);
    }

    @Test
    public void testListShowsDbItems() {
        ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            ActivityHomeBinding binding = activity.getBinding();

            SearchView searchView = activity.getBinding().search;
            searchView.setQuery("Lion", true);

            RecyclerView recyclerView = binding.recyclerView;
            recyclerView.getAdapter().getItemCount();
            List<Animal> animals = binding.getVm().getAnimals().getValue();

            assertTrue(animals.get(0).name.contains(searchView.getQuery()));
        });

    }
}
