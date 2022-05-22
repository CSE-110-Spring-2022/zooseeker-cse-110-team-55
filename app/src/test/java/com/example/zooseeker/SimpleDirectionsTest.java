package com.example.zooseeker;

import static org.junit.Assert.assertEquals;

import android.app.Application;

import com.example.zooseeker.viewmodels.PlanViewModel;

import org.junit.After;
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
public class SimpleDirectionsTest {

    Application app;
    PlanViewModel vm;
    @Before
    public void init() {
        // Runs before every test
        app = RuntimeEnvironment.getApplication();
        vm = new PlanViewModel(app);
        List<String> selected = new ArrayList<>();
        selected.add("elephant_odyssey");
        selected.add("arctic_foxes");
        selected.add("gorillas");
        vm.initRoute(selected);
    }


    @Test
    public void testDetailedDirections() {
        vm.detailedDirectionToggle.setValue(true);

        assertEquals("Entrance Plaza", vm.getDirections().getValue().get(0).target);
        assertEquals("Gorillas", vm.getDirections().getValue().get(1).target);
        vm.getDirectionsToNextExhibit();

        assertEquals("Lions", vm.getDirections().getValue().get(0).target);
        assertEquals("Elephant Odyssey", vm.getDirections().getValue().get(1).target);
        vm.getDirectionsToNextExhibit();

        assertEquals("Lions", vm.getDirections().getValue().get(0).target);
        assertEquals("Alligators", vm.getDirections().getValue().get(1).target);
        assertEquals("Entrance Plaza", vm.getDirections().getValue().get(2).target);
        assertEquals("Arctic Foxes", vm.getDirections().getValue().get(3).target);
        vm.getDirectionsToNextExhibit();

        assertEquals("Entrance Plaza", vm.getDirections().getValue().get(0).target);
        assertEquals("Entrance and Exit Gate", vm.getDirections().getValue().get(1).target);
    }

    @Test
    public void testSimpleDirections() {
        vm.detailedDirectionToggle.setValue(false);

        assertEquals("Entrance Plaza", vm.getDirections().getValue().get(0).target);
        assertEquals("Gorillas", vm.getDirections().getValue().get(1).target);
        vm.getDirectionsToNextExhibit();

        assertEquals("Elephant Odyssey", vm.getDirections().getValue().get(0).target);
        vm.getDirectionsToNextExhibit();

        assertEquals("Lions", vm.getDirections().getValue().get(0).target);
        assertEquals("Alligators", vm.getDirections().getValue().get(1).target);
        assertEquals("Entrance Plaza", vm.getDirections().getValue().get(2).target);
        assertEquals("Arctic Foxes", vm.getDirections().getValue().get(3).target);
        vm.getDirectionsToNextExhibit();

        assertEquals("Entrance Plaza", vm.getDirections().getValue().get(0).target);
        assertEquals("Entrance and Exit Gate", vm.getDirections().getValue().get(1).target);

    }


}
