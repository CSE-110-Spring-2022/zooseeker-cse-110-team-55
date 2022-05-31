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
        assertEquals("Front Street / Monkey Trail", vm.getDirections().getValue().get(1).target.name);
        assertEquals("Flamingos", getLast(vm.getDirections().getValue()).target.name);
        vm.getDirectionsToNextExhibit();

        assertEquals("Front Street / Treetops Way", vm.getDirections().getValue().get(0).target.name);
        assertEquals("Front Street / Monkey Trail", vm.getDirections().getValue().get(1).target.name);
        assertEquals("Flamingos", vm.getDirections().getValue().get(2).target.name);
        assertEquals("Capuchin Monkeys", vm.getDirections().getValue().get(3).target.name);
        vm.getDirectionsToNextExhibit();

        assertEquals("Front Street / Treetops Way", vm.getDirections().getValue().get(0).target.name);
        assertEquals("Treetops Way / Fern Canyon Trail", vm.getDirections().getValue().get(1).target.name);
        assertEquals("Treetops Way / Orangutan Trail", vm.getDirections().getValue().get(2).target.name);
        assertEquals("Treetops Way / Hippo Trail", vm.getDirections().getValue().get(3).target.name);
        assertEquals("Hippos", vm.getDirections().getValue().get(4).target.name);
        assertEquals("Crocodiles", vm.getDirections().getValue().get(5).target.name);
        assertEquals("Monkey Trail / Hippo Trail", vm.getDirections().getValue().get(6).target.name);
        assertEquals("Scripps Aviary", vm.getDirections().getValue().get(7).target.name);
        vm.getDirectionsToNextExhibit();

        assertEquals(0, vm.getDirections().getValue().size());
    }

    @Test
    public void testSimpleDirections() {
        vm.detailedDirectionToggle.setValue(false);

        assertEquals("Front Street / Treetops Way", vm.getDirections().getValue().get(0).target.name);
        assertEquals("Front Street / Monkey Trail", vm.getDirections().getValue().get(1).target.name);
        assertEquals("Flamingos", getLast(vm.getDirections().getValue()).target.name);
        vm.getDirectionsToNextExhibit();

        assertEquals("Front Street / Treetops Way", vm.getDirections().getValue().get(0).target.name);
        assertEquals("Front Street / Monkey Trail", vm.getDirections().getValue().get(1).target.name);
        assertEquals("Capuchin Monkeys", vm.getDirections().getValue().get(2).target.name);
        vm.getDirectionsToNextExhibit();

        assertEquals("Front Street / Treetops Way", vm.getDirections().getValue().get(0).target.name);
        assertEquals("Treetops Way / Hippo Trail", vm.getDirections().getValue().get(1).target.name);
        assertEquals("Monkey Trail / Hippo Trail", vm.getDirections().getValue().get(2).target.name);
        assertEquals("Scripps Aviary", vm.getDirections().getValue().get(3).target.name);
        vm.getDirectionsToNextExhibit();

        assertEquals(0, vm.getDirections().getValue().size());
    }


}
