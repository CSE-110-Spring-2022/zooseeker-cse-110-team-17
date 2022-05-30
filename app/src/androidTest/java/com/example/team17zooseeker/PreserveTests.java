package com.example.team17zooseeker;

import static android.content.Context.MODE_PRIVATE;

import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.IntentCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class PreserveTests {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private ZooKeeperDatabase db;
    private NodeItemDao nodeDao;
    private StateDao stateDao;

    private static Context context = null;

    @Before
    public void createDb() {
        context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ZooKeeperDatabase.class)
                .allowMainThreadQueries()
                .build();
        nodeDao = db.nodeItemDao();
        stateDao = db.stateDao();

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
        db.stateDao().insert(new State("0"));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    public void resetApplication(Context context) {
        Intent resetApplicationIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (resetApplicationIntent != null) {
            resetApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(resetApplicationIntent);
    }

    @Test
    public void TestPreserveMain() {
            MainActivity.setTesting(true);
            ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
            db.stateDao().delete(db.stateDao().get());
            scenario.onActivity(activity -> {
                preferences = activity.getPreferences(MODE_PRIVATE);
                editor = preferences.edit();
                Set<String> vSet = new HashSet<String>();
                nodeItem g = db.nodeItemDao().get("gorilla");
                nodeItem k = db.nodeItemDao().get("koi");
                State state = db.stateDao().get();

                if(state == null) {
                    db.stateDao().insert(new State("0"));
                    state = db.stateDao().get();
                }

                Log.d("TestPreserveMain-g", g.toString());
                Log.d("TestPreserveMain-k", k.toString());

                EditText searchText = activity.findViewById(R.id.search_text);
                RecyclerView vh = activity.findViewById(R.id.visitation_list_view);
                NodeListAdapter adapter = new NodeListAdapter();
                adapter.setHasStableIds(true);
                vh.setAdapter(adapter);

                List<nodeItem> addedNodesList = new ArrayList<nodeItem>();

                searchText.requestFocus();
                searchText.setText("Gorillas");
                searchText.clearFocus();

                Log.e("Node 1: ", g.name);
                Log.e("Text Search: ", String.valueOf(searchText.getText()));
                if(String.valueOf(searchText.getText()).equals(g.name)) {
                    addedNodesList.add(g);
                    adapter.setNodeItems(addedNodesList);
                    vSet.add("Gorillas");
                    editor.putStringSet("visitationList", new HashSet(vSet));
                    editor.apply();
                }

                searchText.requestFocus();
                searchText.setText("Koi Fish");
                searchText.clearFocus();

                Log.e("Node 2: ", k.name);
                Log.e("Text Search: ", String.valueOf(searchText.getText()));
                if(String.valueOf(searchText.getText()).equals(k.name)) {
                    addedNodesList.add(k);
                    adapter.setNodeItems(addedNodesList);
                    vSet.add("Koi Fish");
                    editor.putStringSet("visitationList", new HashSet(vSet));
                    editor.apply();
                }

                resetApplication(activity);

                Log.e("PreserveTests-State:", state.state);

                vSet = preferences.getStringSet("visitationList", null);
                Log.e("PreserveTests-State:", vSet.toString());

                Vector<String> siteIdVector = new Vector<>(vSet);
                String first = siteIdVector.firstElement();
                String last = siteIdVector.lastElement();

                preferences.edit().remove("visitationList").apply();

                assertEquals("Gorillas", first);
                assertEquals("Koi Fish", last);
            });
    }

    @Test
    public void TestPreserveItinerary() {
        ItineraryActivity.setTesting(true);
        ActivityScenario<ItineraryActivity> scenario = ActivityScenario.launch(ItineraryActivity.class);
        scenario.onActivity(activity -> {
            preferences = activity.getPreferences(MODE_PRIVATE);
            editor = preferences.edit();

            State state = db.stateDao().get();

            if(state == null) {
                db.stateDao().insert(new State("1"));
                state = db.stateDao().get();
            }

            Itinerary.injectTestItinerary(null);
            Itinerary.injectTestNodeDao(nodeDao);

            String[] cI = {"gorilla", "koi"};
            editor.putStringSet("visitationList", new HashSet(Arrays.asList(cI)));
            editor.apply();

            resetApplication(activity);

            Log.e("PreserveTests-State:", state.state);

            Set<String> vSet = preferences.getStringSet("visitationList", null);

            ArrayList<String> visitationList = new ArrayList<String>(vSet);
            Itinerary.updateCurrentLocation("entrance_exit_gate");
            Itinerary.createItinerary(activity, visitationList);

            assertEquals("gorilla", Itinerary.getItinerary().get(1));

            preferences.edit().remove("visitationList").apply();
            db.stateDao().delete(db.stateDao().get());
        });
    }

    @Test
    public void TestPreserveDirections() {
        DirectionsActivity.setTesting(true);
        Itinerary.injectTestItinerary(null);
        Itinerary.injectTestNodeDao(nodeDao);
        Itinerary.injectMockItinerary();

        ActivityScenario<DirectionsActivity> scenario = ActivityScenario.launch(DirectionsActivity.class);
        scenario.onActivity(activity -> {

            State state = db.stateDao().get();

            if(state == null) {
                db.stateDao().insert(new State("2"));
                state = db.stateDao().get();
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
