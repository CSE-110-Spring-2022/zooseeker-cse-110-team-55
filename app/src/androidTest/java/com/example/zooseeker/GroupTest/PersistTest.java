package com.example.zooseeker.GroupTest;


import static android.content.Context.MODE_PRIVATE;
import static com.example.zooseeker.util.Constant.SHARED_PREF;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.SearchView;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.zooseeker.databinding.ActivityHomeBinding;
import com.example.zooseeker.models.db.Animal;
import com.example.zooseeker.repositories.AnimalDatabase;
import com.example.zooseeker.repositories.AnimalItemDao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PersistTest {
    AnimalDatabase testDb;
    AnimalItemDao animalItemDao;

    private void clearSharedPrefs() {
        var context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        var editor = context.getSharedPreferences(SHARED_PREF, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }


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
        clearSharedPrefs();
        ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            ActivityHomeBinding binding = activity.getBinding();

            SearchView searchView = activity.getBinding().search;
            searchView.setQuery("Bali Mynah", true);

            RecyclerView recyclerView = binding.recyclerView;
            recyclerView.getAdapter().getItemCount();
            var animals = binding.getVm().getAnimals().getValue();

            assertTrue(animals.get(0).name.contains(searchView.getQuery()));
        });

        // https://github.com/android/android-test/issues/768
        // Setting lifecycle state to Created = Stop + Create
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            ActivityHomeBinding binding = activity.getBinding();
            RecyclerView recyclerView = binding.recyclerView;
            recyclerView.getAdapter().getItemCount();
            var animals = binding.getVm().getAnimals().getValue();

            assertEquals("Bali Mynah", animals.get(0).name);
        });
    }
}
