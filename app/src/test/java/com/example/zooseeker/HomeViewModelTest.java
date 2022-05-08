package com.example.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;

import com.example.zooseeker.models.Animal;
import com.example.zooseeker.viewmodels.HomeActivityViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

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
        Animal animal = new Animal("Example Animal", "example_animal");

        // Add animal
        vm.toggleSelectedAnimal(animal);
        assertTrue(vm.getSelectedAnimals().getValue().contains(animal));

        // Remove animal
        vm.toggleSelectedAnimal(animal);
        assertFalse(vm.getSelectedAnimals().getValue().contains(animal));
        assertEquals(0, vm.getSelectedAnimals().getValue().size());
    }
}
