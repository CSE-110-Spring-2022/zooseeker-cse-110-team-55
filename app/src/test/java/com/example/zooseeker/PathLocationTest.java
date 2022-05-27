package com.example.zooseeker;

import static com.example.zooseeker.util.Helper.getLast;
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
        vm.initRoute(selected, "entrance_exit_gate");
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

    @Test
    /**
     * Test if navigation reroutes properly when there is a new closest exhibit
     */
    public void testRerouteWhenOtherExhibitIsCloser() {
        vm = new PlanViewModel(app);
        List<String> selected = new ArrayList<>();
        selected.add("siamang");
        selected.add("gorilla");
        vm.setExhibitGroups(new HashMap<>());
        vm.initRoute(selected, "entrance_exit_gate");

        var directions = vm.getRoute().getRoute();

        // Pathing to siamangs first
        assertEquals("siamang", getLast(directions.get(0)).id);

        // Teleport to Scripps Aviary
        vm.lastKnownLocation.setValue(new Pair<>(32.748538318135594, -117.17255093386991));
        vm.adjustToNewLocation(vm.lastKnownLocation.getValue());

        // Gorillas is our new closest exhibit
        assertEquals("Gorillas", vm.closestExhibit.getValue());

        // Accept reroute
        vm.acceptHandler();
        directions = vm.getRoute().getRoute();

        // Number of exhibits is unchanged
        assertEquals(3, directions.size());
        // We are routing to new closest exhibit
        assertEquals("gorilla", getLast(directions.get(0)).id);
        assertEquals("siamang", getLast(directions.get(1)).id);
    }

    @Test
    public void testNewLocationIsOnClosestExhibit() {
        vm = new PlanViewModel(app);
        List<String> selected = new ArrayList<>();
        selected.add("siamang");
        selected.add("gorilla");
        vm.setExhibitGroups(new HashMap<>());
        vm.initRoute(selected, "entrance_exit_gate");
        var directions = vm.getRoute().getRoute();

        // Teleport to gorillas
        vm.lastKnownLocation.setValue(new Pair<>(32.74812588554637, -117.17565073656901));
        vm.adjustToNewLocation(vm.lastKnownLocation.getValue());

        // Gorillas is our new closest exhibit
        assertEquals("Gorillas", vm.closestExhibit.getValue());

        // Accept reroute
        vm.acceptHandler();
        directions = vm.getRoute().getRoute();

        // We're already on gorillas, so route to the next one
        assertEquals(2, directions.size());
        assertEquals("siamang", getLast(directions.get(0)).id);
    }

    @Test
    /**
     * Tests that when the user is on an exhibit that is not the closest one, rerouting removes the
     * exhibit and routes to the others
     */
    public void testNewLocationIsOnAnotherExhibit() {
        vm = new PlanViewModel(app);
        List<String> selected = new ArrayList<>();
        selected.add("siamang");
        selected.add("owens_aviary");
        selected.add("gorilla");
        vm.setExhibitGroups(new HashMap<>());
        vm.initRoute(selected, "entrance_exit_gate");
        var directions = vm.getRoute().getRoute();

        // Teleport to gorillas
        vm.lastKnownLocation.setValue(new Pair<>(32.74812588554637, -117.17565073656901));
        vm.adjustToNewLocation(vm.lastKnownLocation.getValue());

        // Gorillas is our new closest exhibit
        assertEquals("Gorillas", vm.closestExhibit.getValue());

        // Accept reroute
        vm.acceptHandler();
        directions = vm.getRoute().getRoute();

        // We're already on gorillas, so route to the next one
        assertEquals(3, directions.size());
        assertEquals("owens_aviary", getLast(directions.get(0)).id);
    }
}
