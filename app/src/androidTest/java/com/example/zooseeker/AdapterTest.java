package com.example.zooseeker;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.zooseeker.adapters.AnimalAdapter;
import com.example.zooseeker.models.Animal;
import com.example.zooseeker.repositories.AnimalDatabase;
import com.example.zooseeker.viewmodels.HomeActivityViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;


@RunWith(AndroidJUnit4.class)
public class AdapterTest {
    AnimalDatabase testDb;
    List<Animal> animals;
    AnimalAdapter.OnAnimalClickListener onAnimalClickListener;
    HomeActivityViewModel vm;

    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, AnimalDatabase.class)
                .allowMainThreadQueries()
                .build();
        AnimalDatabase.injectTestDatabase(testDb);
        animals = Animal.loadJSON(context, "sample_animals.json");
    }

    @Test
    public void testAnimalGetItemCount() {
        AnimalAdapter adapter = new AnimalAdapter(onAnimalClickListener, vm);
        adapter.setAnimals(animals);
        assertEquals(animals.size(), adapter.getItemCount());
    }
}
