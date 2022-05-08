package com.example.zooseeker;

import static org.junit.Assert.assertEquals;

import android.app.Application;
import android.content.Context;

import androidx.databinding.ObservableField;
import androidx.test.core.app.ApplicationProvider;

import com.example.zooseeker.models.Graph;
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
public class PlanViewModelTest {
    Application app;
    PlanViewModel vm;
    @Before
    public void init() {
        // Runs before every test
        app = RuntimeEnvironment.getApplication();
        vm = new PlanViewModel(app);
        List<String> selected = new ArrayList<>();
        selected.add("lions");
        vm.initRoute(selected);
    }

    @Test
    public void testDirections() {
        assertEquals("entrance_exit_gate", vm.getDirections().getValue().get(0).id);
        assertEquals("entrance_plaza", vm.getDirections().getValue().get(1).id);
        assertEquals("gators", vm.getDirections().getValue().get(2).id);
        assertEquals("lions", vm.getDirections().getValue().get(3).id);
    }

    @Test
    public void testRemainingExhibits() {
        assertEquals(new Integer(2), vm.remainingExhibits.get());
    }

    @Test
    public void testCurExhibitName() {
        assertEquals(new String("Lions"), vm.curExhibitName.get());
    }

    @Test
    public void testCurExhibitDist() {
        assertEquals(new Integer(310), vm.curExhibitDist.get());
    }

    @Test
    public void testNextExhibitName() {
        assertEquals(new String("Entrance and Exit Gate"), vm.nextExhibitName.get());
    }

    @Test
    public void testNextExhibitDist() {
        assertEquals(new Integer(310), vm.nextExhibitDist.get());
    }

    @After
    public void finalize() {
        // Runs after every test
    }
}
