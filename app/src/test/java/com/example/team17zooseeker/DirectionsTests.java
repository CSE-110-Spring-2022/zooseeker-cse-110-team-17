package com.example.team17zooseeker;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class DirectionsTests {
    private ZooKeeperDatabase db;
    private NodeItemDao nodeDao;

    private static Context context = null;

    @Before
    public void createDb() {
        context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ZooKeeperDatabase.class)
                .allowMainThreadQueries()
                .build();
        nodeDao = db.nodeItemDao();

        Map<String, nodeItem> nodes = null;
        Map<String, edgeItem> edges = null;

        try {
            nodes = nodeItem.loadNodeInfoJSON(context, "node.json");
            edges = edgeItem.loadEdgeInfoJSON(context, "edge.json");

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<nodeItem> nodeList = new ArrayList<nodeItem>(nodes.values());
        List<edgeItem> edgeList = new ArrayList<edgeItem>(edges.values());

        db.nodeItemDao().insertAll(nodeList);
        db.edgeItemDao().insertAll(edgeList);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }
    @Test
    public void testSimpleDirectionsList(){
        List<String> itinerary = Arrays.asList("entrance_exit_gate","siamang","owens_aviary","hippo","capuchin","gorilla","entrance_exit_gate");
        Directions d = new Directions(itinerary, null,1);
        d.setDetailedDirections(false);
        Context context = ApplicationProvider.getApplicationContext();
        // this code will hold the list of directions to be iterated through
        List<String> dirs = d.createTestDirections(context);


        assertEquals(dirs.size(), 3);
        assertEquals(dirs.get(0), "1. Walk 1100 feet along Gate Path from 'Entrance and Exit Gate' to 'Front Street / Treetops Way'.");
        assertEquals(dirs.get(1), "2. Walk 2500 feet along Treetops Way from 'Front Street / Treetops Way' to 'Treetops Way / Orangutan Trail'.");
        assertEquals(dirs.get(2), "3. Walk 1200 feet along Orangutan Trail from 'Treetops Way / Orangutan Trail' to 'Siamangs'.");
    }


    @Test
    public void testDetailedDirectionsList(){
        List<String> itinerary = Arrays.asList("entrance_exit_gate","siamang","owens_aviary","hippo","capuchin","gorilla","entrance_exit_gate");
        Directions d = new Directions(itinerary, null,1);
        d.setDetailedDirections(true);
        Context context = ApplicationProvider.getApplicationContext();
        List<String> dirs = d.createTestDirections(context);

        assertEquals(dirs.get(0), "1. Walk 1100 feet along Gate Path from 'Entrance and Exit Gate' to 'Front Street / Treetops Way'.");   // in the "" is the string line for the 1. walking .... , so we can test it.
        assertEquals(dirs.get(1), "2. Walk 1100 feet along Treetops Way from 'Front Street / Treetops Way' to 'Treetops Way / Fern Canyon Trail'.");
        assertEquals(dirs.get(2), "3. Walk 1400 feet along Treetops Way from 'Treetops Way / Fern Canyon Trail' to 'Treetops Way / Orangutan Trail'.");
        assertEquals(dirs.get(3), "4. Walk 1200 feet along Orangutan Trail from 'Treetops Way / Orangutan Trail' to 'Siamangs'.");
        assertEquals(dirs.size(), 4);
    }

    @Test
    public void testPreviousDirectionsList(){
        List<String> itinerary = Arrays.asList("entrance_exit_gate","siamang","owens_aviary","hippo","capuchin","gorilla","entrance_exit_gate");
        Directions d = new Directions(itinerary, null, 1);
        d.setDetailedDirections(false);
        Context context = ApplicationProvider.getApplicationContext();
        Directions.decreaseCurrentPosition();
        // this code will hold the list of directions to be iterated through
        List<String> dirs = d.createTestDirections(context);


        assertEquals(dirs.size(), 0);
//        assertEquals(dirs.get(0), "1. Walk 1100 feet along Gate Path from 'Entrance and Exit Gate' to 'Front Street / Treetops Way'.");
//        assertEquals(dirs.get(1), "2. Walk 2500 feet along Treetops Way from 'Front Street / Treetops Way' to 'Treetops Way / Orangutan Trail'.");
//        assertEquals(dirs.get(2), "3. Walk 1200 feet along Orangutan Trail from 'Treetops Way / Orangutan Trail' to 'Siamangs'.");
    }
  
    @Test
    public void testSkipDirectionsList(){
        Itinerary.injectTestItinerary(null);
        Itinerary.injectTestNodeDao(nodeDao);
        Context context = ApplicationProvider.getApplicationContext();

        Itinerary.updateCurrentLocation("entrance_exit_gate");
        String[] vL = {"dove","mynah","capuchin","gorilla","hippo","siamang"};
        ArrayList<String> testVisitationList = new ArrayList<String>(Arrays.asList(vL));

        Itinerary.createItinerary(context, testVisitationList);
        List<String> testItinerary = Itinerary.getItinerary();
        List<String> itinerary = Arrays.asList("siamang","owens_aviary","hippo","capuchin","gorilla","entrance_exit_gate");

        Directions d = new Directions(itinerary, null,1);
        d.skipDirections();

        d.setItinerary(Itinerary.getItinerary());
        List<String> nowItinerary = Itinerary.getItinerary();
        int index = Directions.getCurrentIndex();

        List<String> dirs = d.createTestDirections(context);

//        assertEquals(dirs.get(0), "1. Walk 1200 feet along Orangutan Trail from 'Siamangs' to 'Treetops Way / Orangutan Trail'.");
//        assertEquals(dirs.get(1), "2. Walk 1900 feet along Treetops Way from 'Treetops Way / Orangutan Trail' to 'Treetops Way / Hippo Trail'.");
//        assertEquals(dirs.get(2), "3. Walk 1900 feet along Hippo Trail from 'Treetops Way / Hippo Trail' to 'Hippos'.");
        // we now in 0, 1, 2, 3, ..., If we skip the currentIndex now, we just remove 1, and decreace index to 0, so we from 0 to 0 now.
        assertEquals(dirs.size(), 4);
        assertEquals(dirs.get(0), "1. Walk 1100 feet along Gate Path from 'Entrance and Exit Gate' to 'Front Street / Treetops Way'.");
        assertEquals(dirs.get(3), "4. Walk 1200 feet along Orangutan Trail from 'Treetops Way / Orangutan Trail' to 'Siamangs'.");
    }

    // if it is out of bound, createDirections function just return empty list.
    @Test (expected=IndexOutOfBoundsException.class)
    public void TestDirectionsOutOfBoundException() {
        List<String> itinerary = Arrays.asList("entrance_exit_gate", "siamang", "owens_aviary", "hippo", "capuchin", "gorilla", "entrance_exit_gate" );
        Directions d = new Directions(itinerary, null,10);
        Context context = ApplicationProvider.getApplicationContext();
        List<String> dirs = d.createTestDirections(context);

    }

}