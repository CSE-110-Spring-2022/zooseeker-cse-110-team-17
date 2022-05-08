package com.example.team17zooseeker;

import static org.junit.Assert.assertEquals;

import android.app.Instrumentation;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.jgrapht.Graph;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;
import java.util.logging.Handler;
import java.util.stream.Collectors;

@LargeTest
@RunWith( AndroidJUnit4.class )
public class SBMTests {
    ZooKeeperDatabase testDb;
    NodeItemDao nodeItemDao;

    @Before
    public void resetDatabase() throws IOException {
        //Create database
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ZooKeeperDatabase.class)
                .allowMainThreadQueries()
                .build();
        ZooKeeperDatabase.injectTestDatabase(testDb);

        //Populate Database
        List<String> temp = new ArrayList<String>();
        testDb.nodeItemDao().insert(new nodeItem("lions", "exhibit", "Lions", temp));
        testDb.nodeItemDao().insert(new nodeItem("gorillas", "exhibit", "Gorillas", temp));

        //Inject a test itinerary
        Itinerary.injectTestItinerary(null);
        Itinerary.injectTestNodeDao(testDb.nodeItemDao());
        String[] cI = {"gorillas", "lions"};
        ArrayList<String> correctItinerary = new ArrayList<String>(Arrays.asList(cI));
        Itinerary.createItinerary(context, correctItinerary);
    }

    @Test
    public void MainActivityTest() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity -> {

            //Grab test items from database
            nodeItem temp1 = testDb.nodeItemDao().get("lions");
            nodeItem temp2 = testDb.nodeItemDao().get("gorillas");
            Log.e("Node 1: ", temp1.toString());
            Log.e("Node 2: ", temp2.toString());

            EditText searchText = activity.findViewById(R.id.search_text);
            RecyclerView noRe = activity.findViewById(R.id.visitation_list_view);
            NodeListAdapter nodeAdapter = new NodeListAdapter();
            nodeAdapter.setHasStableIds(true);
            noRe.setAdapter(nodeAdapter);

            searchText.requestFocus();
            searchText.setText("Lions");
            searchText.clearFocus();
            List<nodeItem> addedNodesList = new ArrayList<nodeItem>();

            Log.e("Node 1: ", temp1.name);
            Log.e("Text Search: ", String.valueOf(searchText.getText()));
            if(String.valueOf(searchText.getText()).equals(temp1.name)) {
                Log.e("ENTERED HERE!", "HELLO");
                addedNodesList.add(temp1);
                nodeAdapter.setNodeItems(addedNodesList);
            }

            Log.e("Search Text: ", String.valueOf(searchText.getText()));
            Log.e("Adapter: ->>>", String.valueOf(nodeAdapter.getItemCount()));
            Log.e("VH: ", String.valueOf(nodeAdapter.getItemName(0)));
            assertEquals(String.valueOf(searchText.getText()), String.valueOf(nodeAdapter.getItemName(0)));

            searchText.requestFocus();
            searchText.setText("Gorillas");
            searchText.clearFocus();

            Log.e("Node 2: ", temp2.name);
            Log.e("Text Search: ", String.valueOf(searchText.getText()));
            if(String.valueOf(searchText.getText()).equals(temp2.name)) {
                Log.e("Entered IF statement", "Added to ViewHolder");
                addedNodesList.add(temp2);
                nodeAdapter.setNodeItems(addedNodesList);
            }

            Log.e("Search Text: ", String.valueOf(searchText.getText()));
            Log.e("Adapter: ->>>", String.valueOf(nodeAdapter.getItemCount()));
            Log.e("VH: ", String.valueOf(nodeAdapter.getItemName(1)));
            assertEquals(String.valueOf(searchText.getText()), String.valueOf(nodeAdapter.getItemName(1)));

            Button plan_btn = activity.findViewById(R.id.plan_btn);
            plan_btn.performClick();
        });
    }

    @Test
    public void ItineraryActivityTest() {
        ActivityScenario<ItineraryActivity> scenario = ActivityScenario.launch(ItineraryActivity.class);

        scenario.onActivity(activity -> {

            assertEquals("gorillas", Itinerary.getItinerary().get(1));

            Button dir_btn = activity.findViewById(R.id.get_direction);
            dir_btn.performClick();
        });
    }

    @Test
    public void DirectionsTest() {
        ActivityScenario<DirectionsActivity> scenario = ActivityScenario.launch(DirectionsActivity.class);

        scenario.onActivity(activity -> {
            DirectionsAdapter dirAdapter = activity.getAdapter();
            assertEquals("1. Walk 10 meters along Entrance Way from 'Entrance and Exit Gate' to 'Entrance Plaza'.", dirAdapter.getItemName(0));

            Button next_btn = activity.findViewById(R.id.next_btn);
            next_btn.performClick();
            assertEquals("1. Walk 200 meters along Africa Rocks Street from 'Gorillas' to 'Lions'.", dirAdapter.getItemName(0));

            //Finish Click
            next_btn.performClick();
        });
    }
}