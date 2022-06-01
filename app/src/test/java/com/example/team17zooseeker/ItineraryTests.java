package com.example.team17zooseeker;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;

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
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class ItineraryTests {

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
    public void testShortestPathInItinerary(){
        //In case an itinerary has been created
        ItineraryActivity.setTesting(true);
        Itinerary.injectTestItinerary(null);
        Itinerary.injectTestNodeDao(nodeDao);
        Itinerary.updateCurrentLocation("entrance_exit_gate");

        String[] vL = {"dove"};
        ArrayList<String> testVisitationList = new ArrayList<String>(Arrays.asList(vL));
        Itinerary.createItinerary(context, testVisitationList);

        //Check the distance function
        assertEquals(5300, Itinerary.distance("entrance_exit_gate", "flamingo"));
    }

    //Note: for a previous test when going from lions the distances to gorillas and the
    //elephants are the same. Because of the order in which they were added we go to gorillas
    //fist and have to back track to the elephants. This isn't optimal but works fine for now.
    @Test
    public void testValidCreationOfItinerary() {
        //In case an itinerary has been created
        ItineraryActivity.setTesting(true);
        Itinerary.injectTestItinerary(null);
        Itinerary.injectTestNodeDao(nodeDao);
        Itinerary.updateCurrentLocation("entrance_exit_gate");

        String[] vL = {"dove","mynah","capuchin","gorilla","hippo","siamang"};
        ArrayList<String> testVisitationList = new ArrayList<String>(Arrays.asList(vL));

        Itinerary.createItinerary(context, testVisitationList);
        List<String> testItinerary = Itinerary.getItinerary();

        String[] cI = {"siamang","owens_aviary","hippo","capuchin","gorilla","entrance_exit_gate"};
        ArrayList<String> correctItinerary = new ArrayList<String>(Arrays.asList(cI));

        assertEquals(correctItinerary, testItinerary);
    }

    @Test
    public void testVisitationListFormatsFunction() {
        //In case an itinerary has been created
        ItineraryActivity.setTesting(true);
        Itinerary.injectTestItinerary(null);
        Itinerary.injectTestNodeDao(nodeDao);
        Itinerary.updateCurrentLocation("entrance_exit_gate");

        String[] vL = {"dove", "gorilla"};
        ArrayList<String> testVisitationList = new ArrayList<String>(Arrays.asList(vL));

        Itinerary.createItinerary(context, testVisitationList);
        List<String> testItinerary = Itinerary.getItinerary();

        //Checking the aviary is added
        String[] cI = {"owens_aviary", "gorilla", "entrance_exit_gate"};
        ArrayList<String> correctItinerary = new ArrayList<String>(Arrays.asList(cI));

        assertEquals(correctItinerary, testItinerary);
    }

    @Test
    public void testSkipItinerary(){
        //In case an itinerary has been created
        ItineraryActivity.setTesting(true);
        Itinerary.injectTestItinerary(null);
        Itinerary.injectTestNodeDao(nodeDao);
        Itinerary.updateCurrentLocation("entrance_exit_gate");

        String[] vL = {"dove","mynah","capuchin","gorilla","hippo","siamang"};
        ArrayList<String> testVisitationList = new ArrayList<String>(Arrays.asList(vL));
        Itinerary.createItinerary(context, testVisitationList);

        String[] ciBefore = {"siamang","owens_aviary","hippo","capuchin","gorilla","entrance_exit_gate"};
        ArrayList<String> b_Itinerary = new ArrayList<String>(Arrays.asList(ciBefore));
        assertEquals(b_Itinerary,Itinerary.getItinerary());
        String[] ciAfter = {"owens_aviary","hippo","capuchin","gorilla","entrance_exit_gate"};
        ArrayList<String> a_Itinerary = new ArrayList<String>(Arrays.asList(ciAfter));
        Itinerary.skip();
        assertEquals(a_Itinerary,Itinerary.getItinerary());
    }
}