package com.example.team17zooseeker;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DirectionsTests {

    ZooKeeperDatabase testDb;
    NodeItemDao nodeDao;
    EdgeItemDao edgeDao;

    @Before
    public void resetDatabase() throws IOException {
//        Context context = ApplicationProvider.getApplicationContext();
//        testDb = Room.inMemoryDatabaseBuilder(context, ZooKeeperDatabase.class)
//                .allowMainThreadQueries()
//                .build();
//        edgeDao = testDb.edgeItemDao();
////        graphDao = db.graphItemDao();
//        nodeDao = testDb.nodeItemDao();
//
//        List<String> temp = Collections.emptyList();

    }

    @Test
    public void testDirectionsList(){
        List<String> itinerary = Arrays.asList("entrance_exit_gate", "elephant_odyssey");
        Directions d = new Directions(itinerary, 0);
        Context context = ApplicationProvider.getApplicationContext();
        // this code will hold the list of directions to be iterated through
        List<String> dirs = d.createTestDirections(context);

        assertEquals(dirs.get(0), "1. Walk 10 meters along Entrance Way from 'Entrance and Exit Gate' to 'Entrance Plaza'.");   // in the "" is the string line for the 1. walking .... , so we can test it.
        assertEquals(dirs.get(1), "2. Walk 100 meters along Reptile Road from 'Entrance Plaza' to 'Alligators'.");
        assertEquals(dirs.get(2), "3. Walk 200 meters along Sharp Teeth Shortcut from 'Alligators' to 'Lions'.");
        assertEquals(dirs.get(3), "4. Walk 200 meters along Africa Rocks Street from 'Lions' to 'Elephant Odyssey'.");
    }

    @Test
    public void testMultipleDirectionsList(){
        List<String> itinerary = Arrays.asList("entrance_exit_gate", "elephant_odyssey", "arctic_foxes");
        Directions d = new Directions(itinerary, 0);
        Context context = ApplicationProvider.getApplicationContext();
        // this code will hold the list of directions to be iterated through
        //d.createDirections(context);
        List<String> dirs = d.createTestDirections(context);

        assertEquals(dirs.get(0), "1. Walk 10 meters along Entrance Way from 'Entrance and Exit Gate' to 'Entrance Plaza'.");   // in the "" is the string line for the 1. walking .... , so we can test it.
        assertEquals(dirs.get(1), "2. Walk 100 meters along Reptile Road from 'Entrance Plaza' to 'Alligators'.");
        assertEquals(dirs.get(2), "3. Walk 200 meters along Sharp Teeth Shortcut from 'Alligators' to 'Lions'.");
    }

    // if it is out of bound, createDirections function just return empty list.
    @Test
    public void TestDirectionsOutOfBoundException() {
        List<String> itinerary = Arrays.asList("entrance_exit_gate", "elephant_odyssey");
        Directions d = new Directions(itinerary, 1);
        Context context = ApplicationProvider.getApplicationContext();
        List<String> dirs = d.createDirections(context);
        assertEquals(dirs.isEmpty(), Boolean.TRUE);
    }

}