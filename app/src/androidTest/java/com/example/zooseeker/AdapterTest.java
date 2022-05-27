package com.example.zooseeker;

import static android.content.Context.MODE_PRIVATE;
import static com.example.zooseeker.util.Constant.ANIMALS_ID;
import static com.example.zooseeker.util.Constant.CURR_INDEX;
import static com.example.zooseeker.util.Constant.SHARED_PREF;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.zooseeker.adapters.AnimalAdapter;
import com.example.zooseeker.models.db.Animal;
import com.example.zooseeker.repositories.AnimalDatabase;
import com.example.zooseeker.viewmodels.HomeActivityViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
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

    @Before
    public void clearSharedPreferences() {
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Test
    public void testAnimalGetItemCount() {
        AnimalAdapter adapter = new AnimalAdapter(onAnimalClickListener, vm);
        var x = new ArrayList<Animal.AnimalDisplay>();
        for (var a : animals) {
            var y = new Animal.AnimalDisplay();
            y.id = a.id;
            y.name = a.name;
            x.add(y);
        }
        adapter.setAnimals(x);
        assertEquals(animals.size(), adapter.getItemCount());
    }
}
