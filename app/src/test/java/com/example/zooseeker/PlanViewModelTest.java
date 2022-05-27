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
import java.util.HashMap;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class)
public class PlanViewModelTest {
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
        vm.setExhibitGroups(new HashMap<>());
        vm.initRoute(selected, "entrance_exit_gate");
    }

    @Test
    public void testRemainingExhibits() {
        assertEquals(new Integer(3), vm.remainingExhibits.get());
        vm.getDirectionsToNextExhibit();
        assertEquals(new Integer(2), vm.remainingExhibits.get());
        vm.getDirectionsToNextExhibit();
        assertEquals(new Integer(1), vm.remainingExhibits.get());
    }

    @Test
    public void testCurExhibitName() {
        assertEquals("Flamingos", vm.curExhibitName.get());
        vm.getDirectionsToNextExhibit();
        assertEquals("Capuchin Monkeys", vm.curExhibitName.get());
        vm.getDirectionsToNextExhibit();
        assertEquals("Entrance and Exit Gate", vm.curExhibitName.get());
    }

    @Test
    public void testCurExhibitDist() {
        assertEquals(new Integer(90), vm.curExhibitDist.get());
        vm.getDirectionsToNextExhibit();
        assertEquals(new Integer(150), vm.curExhibitDist.get());
        vm.getDirectionsToNextExhibit();
        assertEquals(new Integer(240), vm.curExhibitDist.get());
    }

    @Test
    public void testNextExhibitName() {
        // Starting from flamingos
        assertEquals("Capuchin Monkeys", vm.nextExhibitName.get());
        vm.getDirectionsToNextExhibit();
        assertEquals("Entrance and Exit Gate", vm.nextExhibitName.get());
    }

    @Test
    public void testNextExhibitDist() {
        // starting from flamingos
        assertEquals(new Integer(150), vm.nextExhibitDist.get());
        vm.getDirectionsToNextExhibit();
        assertEquals(new Integer(240), vm.nextExhibitDist.get());
    }




    @After
    public void finalize() {
        // Runs after every test
    }
}
