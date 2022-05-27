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
import java.util.HashMap;
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
        vm.setExhibitGroups(new HashMap<>());
        vm.initRoute(selected, "entrance_exit_gate");
    }


    @Test
    public void testDetailedDirections() {
        vm.detailedDirectionToggle.setValue(true);

        assertEquals("Front Street / Treetops Way", vm.getDirections().getValue().get(0).target.name);
        assertEquals("Flamingos", getLast(vm.getDirections().getValue()).target.name);
        vm.getDirectionsToNextExhibit();

        assertEquals("Capuchin Monkeys", vm.getDirections().getValue().get(0).target.name);
        vm.getDirectionsToNextExhibit();

        assertEquals("Scripps Aviary", vm.getDirections().getValue().get(1).target.name);
        vm.getDirectionsToNextExhibit();

        assertEquals("Monkey Trail / Hippo Trail", vm.getDirections().getValue().get(0).target.name);
        assertEquals("Treetops Way / Hippo Trail", vm.getDirections().getValue().get(3).target.name);
        assertEquals("Front Street / Treetops Way", vm.getDirections().getValue().get(6).target.name);
        assertEquals("Entrance and Exit Gate", vm.getDirections().getValue().get(7).target.name);
    }

    @Test
    public void testSimpleDirections() {
        vm.detailedDirectionToggle.setValue(false);

        assertEquals("Front Street / Treetops Way", vm.getDirections().getValue().get(0).target.name);
        assertEquals("Front Street / Monkey Trail", vm.getDirections().getValue().get(1).target.name);
        assertEquals("Flamingos", getLast(vm.getDirections().getValue()).target.name);
        vm.getDirectionsToNextExhibit();

        assertEquals("Capuchin Monkeys", vm.getDirections().getValue().get(0).target.name);
        vm.getDirectionsToNextExhibit();

        assertEquals("Scripps Aviary", vm.getDirections().getValue().get(0).target.name);
        vm.getDirectionsToNextExhibit();

        assertEquals("Monkey Trail / Hippo Trail", vm.getDirections().getValue().get(0).target.name);
        assertEquals("Treetops Way / Hippo Trail", vm.getDirections().getValue().get(1).target.name);
        assertEquals("Front Street / Treetops Way", vm.getDirections().getValue().get(2).target.name);
        assertEquals("Entrance and Exit Gate", vm.getDirections().getValue().get(3).target.name);
    }


}
