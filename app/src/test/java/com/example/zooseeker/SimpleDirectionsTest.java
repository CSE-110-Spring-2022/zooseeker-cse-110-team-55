package com.example.zooseeker;

import static com.example.zooseeker.util.Helper.getLast;
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
        selected.add("flamingo");
        selected.add("capuchin");
        selected.add("scripps_aviary");
        vm.initRoute(selected);
    }


    @Test
    public void testDetailedDirections() {
        vm.detailedDirectionToggle.setValue(true);

        assertEquals("Front Street / Treetops Way", vm.getDirections().getValue().get(0).target);
        assertEquals("Flamingos", getLast(vm.getDirections().getValue()).target);
        vm.getDirectionsToNextExhibit();

        assertEquals("Capuchin Monkeys", vm.getDirections().getValue().get(0).target);
        vm.getDirectionsToNextExhibit();

        assertEquals("Scripps Aviary", vm.getDirections().getValue().get(1).target);
        vm.getDirectionsToNextExhibit();

        assertEquals("Monkey Trail / Hippo Trail", vm.getDirections().getValue().get(0).target);
        assertEquals("Treetops Way / Hippo Trail", vm.getDirections().getValue().get(3).target);
        assertEquals("Front Street / Treetops Way", vm.getDirections().getValue().get(6).target);
        assertEquals("Entrance and Exit Gate", vm.getDirections().getValue().get(7).target);
    }

    @Test
    public void testSimpleDirections() {
        vm.detailedDirectionToggle.setValue(false);

        assertEquals("Front Street / Treetops Way", vm.getDirections().getValue().get(0).target);
        assertEquals("Front Street / Monkey Trail", vm.getDirections().getValue().get(1).target);
        assertEquals("Flamingos", getLast(vm.getDirections().getValue()).target);
        vm.getDirectionsToNextExhibit();

        assertEquals("Capuchin Monkeys", vm.getDirections().getValue().get(0).target);
        vm.getDirectionsToNextExhibit();

        assertEquals("Scripps Aviary", vm.getDirections().getValue().get(0).target);
        vm.getDirectionsToNextExhibit();

        assertEquals("Monkey Trail / Hippo Trail", vm.getDirections().getValue().get(0).target);
        assertEquals("Treetops Way / Hippo Trail", vm.getDirections().getValue().get(1).target);
        assertEquals("Front Street / Treetops Way", vm.getDirections().getValue().get(2).target);
        assertEquals("Entrance and Exit Gate", vm.getDirections().getValue().get(3).target);
    }


}
