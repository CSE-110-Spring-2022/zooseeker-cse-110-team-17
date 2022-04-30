package com.example.team17zooseeker;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DirectionsTests {
    @Test
    public void testDirectionslist(){
        List<String> itinerary = Arrays.asList("entrance_exit_gate", "elephant_odyssey");
        Directions d = new Directions(itinerary, 0);
        Context context = ApplicationProvider.getApplicationContext();
        d.createDirections(context);
        // this code will hold the list of directions to be iterated through
        List<String> dirs = d.getDirs();


        assertEquals(dirs.get(0), "1. Walk 10 meters along Entrance Way from 'Entrance and Exit Gate' to 'Entrance Plaza'.");   // in the "" is the string line for the 1. walking .... , so we can test it.
        assertEquals(dirs.get(1), "2. Walk 100 meters along Reptile Road from 'Entrance Plaza' to 'Alligators'.");
        assertEquals(dirs.get(2), "3. Walk 200 meters along Sharp Teeth Shortcut from 'Alligators' to 'Lions'.");
        assertEquals(dirs.get(3), "4. Walk 200 meters along Africa Rocks Street from 'Lions' to 'Elephant Odyssey'.");
    }

    // This test tests the exception for out of boundary.
    @Test(expected = RuntimeException.class)
    public void TestDirectionsOutOfBoundException() {
        List<String> itinerary = Arrays.asList("entrance_exit_gate", "elephant_odyssey");
        Directions d = new Directions(itinerary, 1);
        Context context = ApplicationProvider.getApplicationContext();
        d.createDirections(context);
        // this code will hold the list of directions to be iterated through
        List<String> dirs = d.getDirs();
    }

}