package com.example.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;

import com.example.zooseeker.models.Animal;
import com.example.zooseeker.models.SelectedAnimalParams;
import com.example.zooseeker.viewmodels.HomeActivityViewModel;

import org.checkerframework.checker.units.qual.A;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class)
public class HomeViewModelTest {
    Application app;
    HomeActivityViewModel vm;
    @Before
    public void init() {
        app = RuntimeEnvironment.getApplication();
        vm = new HomeActivityViewModel();
    }

    @Test
    public void testToggleAnimal() {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Animal("Animal One", "animal_one"));
        vm.setAnimals(animals);

        Animal animal = animals.get(0);
        SelectedAnimalParams p = new SelectedAnimalParams(0);

        // Add animal
        vm.selectAnimalCommand.execute(p);
        assertTrue(vm.getSelectedAnimals().contains(animal));

        // Remove animal
        vm.selectAnimalCommand.execute(p);
        assertFalse(vm.getSelectedAnimals().contains(animal));
        assertEquals(0, vm.getSelectedAnimals().size());
    }
}
