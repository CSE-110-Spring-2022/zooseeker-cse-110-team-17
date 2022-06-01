package com.example.team17zooseeker;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BDDTests {

    private ZooKeeperDatabase testDb;
    private NodeItemDao nodeDao;
    private StateDao stateDao;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private static Context context = null;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    private MainActivity mActivity = null;

    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ZooKeeperDatabase.class)
                .allowMainThreadQueries()
                .build();
        ZooKeeperDatabase.injectTestDatabase(testDb);

        //Populate Database
        List<String> temp = new ArrayList<String>();
        testDb.nodeItemDao().insert(new nodeItem("gorillas", "exhibit", "Gorillas","000",1,1, temp));
        testDb.nodeItemDao().insert(new nodeItem("koi", "exhibit", "Koi Fish", "111", 0,0, temp));
    }

    public void resetApplication(Context context) {
        Intent resetApplicationIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (resetApplicationIntent != null) {
            resetApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(resetApplicationIntent);
    }

    @Before
    public void setActivity() {

        mActivity = mActivityRule.getActivity();

        context = ApplicationProvider.getApplicationContext();
        resetApplication(context);
        testDb = Room.inMemoryDatabaseBuilder(context, ZooKeeperDatabase.class)
                .allowMainThreadQueries()
                .build();
        nodeDao = testDb.nodeItemDao();
        stateDao = testDb.stateDao();

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

        testDb.nodeItemDao().insertAll(nodeList);
        testDb.edgeItemDao().insertAll(edgeList);
        testDb.stateDao().get();
    }

    @Test
    public void MainActivityTest() {
        MainActivity.setTesting(true);
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {

            State state = stateDao.get();

            if(state == null) {
                testDb.stateDao().insert(new State("0"));
                state = testDb.stateDao().get();
            }

            String[] vL = {"dove", "mynah", "capuchin", "gorilla", "hippo", "siamang"};
            ArrayList<String> testVisitationList = new ArrayList<String>(Arrays.asList(vL));

            nodeItem item1 = testDb.nodeItemDao().get("dove");
            nodeItem item2 = testDb.nodeItemDao().get("mynah");
            nodeItem item3 = testDb.nodeItemDao().get("capuchin");
            nodeItem item4 = testDb.nodeItemDao().get("gorilla");
            nodeItem item5 = testDb.nodeItemDao().get("hippo");
            nodeItem item6 = testDb.nodeItemDao().get("siamang");

            EditText searchText = activity.findViewById(R.id.search_text);
            RecyclerView vh = activity.findViewById(R.id.visitation_list_view);
            NodeListAdapter adapter = new NodeListAdapter();
            adapter.setHasStableIds(true);
            vh.setAdapter(adapter);
            List<nodeItem> addedNodesList = new ArrayList<nodeItem>();

            searchText.requestFocus();
            searchText.setText("Emerald Dove");
            searchText.clearFocus();

            Log.e("Node 1", item1.name);
            Log.e("Text Search", String.valueOf(searchText.getText()));
            if(String.valueOf(searchText.getText()).equals(item1.name)) {
                addedNodesList.add(item1);
                adapter.setNodeItems(addedNodesList);
            }

            searchText.requestFocus();
            searchText.setText("Bali Mynah");
            searchText.clearFocus();

            Log.e("Node 2", item2.name);
            Log.e("Text Search", String.valueOf(searchText.getText()));
            if(String.valueOf(searchText.getText()).equals(item2.name)) {
                addedNodesList.add(item2);
                adapter.setNodeItems(addedNodesList);
            }

            searchText.requestFocus();
            searchText.setText("Capuchin Monkeys");
            searchText.clearFocus();

            Log.e("Node 3", item3.name);
            Log.e("Text Search", String.valueOf(searchText.getText()));
            if(String.valueOf(searchText.getText()).equals(item3.name)) {
                addedNodesList.add(item3);
                adapter.setNodeItems(addedNodesList);
            }

            searchText.requestFocus();
            searchText.setText("Gorillas");
            searchText.clearFocus();

            Log.e("Node 4", item4.name);
            Log.e("Text Search", String.valueOf(searchText.getText()));
            if(String.valueOf(searchText.getText()).equals(item4.name)) {
                addedNodesList.add(item4);
                adapter.setNodeItems(addedNodesList);
            }

            searchText.requestFocus();
            searchText.setText("Hippos");
            searchText.clearFocus();

            Log.e("Node 5", item5.name);
            Log.e("Text Search", String.valueOf(searchText.getText()));
            if(String.valueOf(searchText.getText()).equals(item5.name)) {
                addedNodesList.add(item5);
                adapter.setNodeItems(addedNodesList);
            }

            searchText.requestFocus();
            searchText.setText("Siamangs");
            searchText.clearFocus();

            Log.e("Node 6: ", item6.name);
            Log.e("Text Search: ", String.valueOf(searchText.getText()));
            if(String.valueOf(searchText.getText()).equals(item6.name)) {
                addedNodesList.add(item6);
                adapter.setNodeItems(addedNodesList);
            }
            assertEquals(item1.name, String.valueOf(adapter.getItemName(0)));
            assertEquals(item2.name, String.valueOf(adapter.getItemName(1)));
            assertEquals(item3.name, String.valueOf(adapter.getItemName(2)));
            assertEquals(item4.name, String.valueOf(adapter.getItemName(3)));
            assertEquals(item5.name, String.valueOf(adapter.getItemName(4)));
            assertEquals(item6.name, String.valueOf(adapter.getItemName(5)));
        });

    }

    @Test
    public void ItineraryActivityTest() {
        ItineraryActivity.setTesting(true);
        Itinerary.injectTestItinerary(null);
        Itinerary.injectTestNodeDao(nodeDao);
        Itinerary.updateCurrentLocation("entrance_exit_gate");

        String[] vL = {"dove","mynah","capuchin","gorilla","hippo","siamang"};
        ArrayList<String> testVisitationList = new ArrayList<String>(Arrays.asList(vL));

        Itinerary.createItinerary(context, testVisitationList);

        ActivityScenario<ItineraryActivity> scenario = ActivityScenario.launch(ItineraryActivity.class);
        scenario.onActivity(activity -> {

            RecyclerView rv = activity.findViewById(R.id.itinerary_recycler);
            RecyclerView.ViewHolder vh = rv.findViewHolderForAdapterPosition(0);

            TextView textView = vh.itemView.findViewById(R.id.itinerary_textItem);
            String content = textView.getText().toString();

            assertEquals("Siamangs\n(4800 feet)",content);

            vh = rv.findViewHolderForAdapterPosition(1);

            textView = vh.itemView.findViewById(R.id.itinerary_textItem);
            content = textView.getText().toString();

            assertEquals("Owens Aviary\n(8700 feet)",content);

            vh = rv.findViewHolderForAdapterPosition(2);

            textView = vh.itemView.findViewById(R.id.itinerary_textItem);
            content = textView.getText().toString();

            assertEquals("Hippos\n(14200 feet)",content);

            vh = rv.findViewHolderForAdapterPosition(3);

            textView = vh.itemView.findViewById(R.id.itinerary_textItem);
            content = textView.getText().toString();

            assertEquals("Capuchin Monkeys\n(19100 feet)",content);

            vh = rv.findViewHolderForAdapterPosition(4);

            textView = vh.itemView.findViewById(R.id.itinerary_textItem);
            content = textView.getText().toString();

            assertEquals("Gorillas\n(23800 feet)",content);
        });
    }

    @Test
    public void DirectionsActivityTest() {
        DirectionsActivity.setTesting(true);
        Itinerary.injectTestItinerary(null);
        Itinerary.injectTestNodeDao(nodeDao);
        Itinerary.injectMockItinerary();

        ActivityScenario<DirectionsActivity> scenario = ActivityScenario.launch(DirectionsActivity.class);
        scenario.onActivity(activity -> {

            State state = testDb.stateDao().get();

            if(state == null) {
                testDb.stateDao().insert(new State("2"));
                state = testDb.stateDao().get();
            }

            RecyclerView rv = activity.findViewById(R.id.directions_items);
            RecyclerView.ViewHolder vh = rv.findViewHolderForAdapterPosition(0);

            preferences = activity.getPreferences(MODE_PRIVATE);
            editor = preferences.edit();

            TextView v = vh.itemView.findViewById(R.id.directions_item_text);

            editor.putString("direction", v.getText().toString());
            editor.apply();

            resetApplication(activity);

            Log.e("PreserveTests-State:", state.state);

            assertEquals(v.getText().toString(), preferences.getString("direction", null));
            preferences.edit().remove("direction").apply();
            //ZooKeeperDatabase.getSingleton(activity).stateDao().delete(ZooKeeperDatabase.getSingleton(activity).stateDao().get());
        });
    }
}