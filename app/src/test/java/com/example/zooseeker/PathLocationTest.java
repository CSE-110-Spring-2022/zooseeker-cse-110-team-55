package com.example.zooseeker;

import static org.junit.Assert.assertEquals;

import android.app.Application;
import android.util.Pair;

import com.example.zooseeker.viewmodels.PlanViewModel;

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
public class PathLocationTest {
    Application app;
    PlanViewModel vm;
    @Before
    public void init() {
        // Runs before every test
        app = RuntimeEnvironment.getApplication();
        vm = new PlanViewModel(app);
        List<String> selected = new ArrayList<>();
        selected.add("siamang");
        vm.setExhibitGroups(new HashMap<>());
        vm.initRoute(selected);
    }

    @Test
    /**
     * Tests if navigation changes based on current location
     */
    public void testRelocate() {
        var directions = vm.getRoute().getRoute();
        // Start location
        assertEquals("entrance_exit_gate", directions.get(0).get(0).id);
        // Move to orangutans
        vm.lastKnownLocation.setValue(new Pair<>(32.736864688333235, -117.16364410510093));
        vm.adjustToNewLocation(vm.lastKnownLocation.getValue());
        assertEquals("orangutan", directions.get(0).get(0).id);
    }

    @Test
    /**
     * Test if navigation changes based on current location and has minimum cost
     */
    public void testRelocateShortestPath() {
        var directions = vm.getRoute().getRoute();
        // Start location
        assertEquals("entrance_exit_gate", directions.get(0).get(0).id);
        assertEquals(130, (int) vm.curExhibitDist.get());
        // Move to treetops way / hippo trail intersection
        vm.lastKnownLocation.setValue(new Pair<>(32.74213959255212, -117.16066409380507));
        vm.adjustToNewLocation(vm.lastKnownLocation.getValue());
        assertEquals(115, (int) vm.curExhibitDist.get());
    }
}
