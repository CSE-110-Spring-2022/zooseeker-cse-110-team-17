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
import android.widget.EditText;

import androidx.core.content.IntentCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class PreserveTests {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    ZooKeeperDatabase testDb;

    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ZooKeeperDatabase.class)
                .allowMainThreadQueries()
                .build();
        ZooKeeperDatabase.injectTestDatabase(testDb);
    }

    public void resetApplication(Context context) {
        Intent resetApplicationIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (resetApplicationIntent != null) {
            resetApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(resetApplicationIntent);
    }

    @Test
    public void TestPreserveVisitation() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            preferences = activity.getPreferences(MODE_PRIVATE);
            editor = preferences.edit();
            List<String> temp = new ArrayList<String>();

            State state = testDb.stateDao().get();

            Set<String> vSet = new HashSet<String>();

            if(state == null) {
                testDb.stateDao().insert(new State("0"));
                state = testDb.stateDao().get();
            }

            testDb.nodeItemDao().insert(new nodeItem("gorillas", "exhibit", "Gorillas","000",1,1, temp));
            testDb.nodeItemDao().insert(new nodeItem("koi", "exhibit", "Koi Fish", "111", 0,0, temp));

            nodeItem temp1 = testDb.nodeItemDao().get("gorillas");
            nodeItem temp2 = testDb.nodeItemDao().get("koi");
            Log.e("Node 1: ", temp1.toString());
            Log.e("Node 2: ", temp2.toString());

            EditText searchText = activity.findViewById(R.id.search_text);
            RecyclerView vh = activity.findViewById(R.id.visitation_list_view);
            NodeListAdapter adapter = new NodeListAdapter();
            adapter.setHasStableIds(true);
            vh.setAdapter(adapter);

            List<nodeItem> addedNodesList = new ArrayList<nodeItem>();

            searchText.requestFocus();
            searchText.setText("Gorillas");
            searchText.clearFocus();

            Log.e("Node 1: ", temp1.name);
            Log.e("Text Search: ", String.valueOf(searchText.getText()));
            if(String.valueOf(searchText.getText()).equals(temp1.name)) {
                Log.e("ENTERED HERE!", "HELLO");
                addedNodesList.add(temp1);
                adapter.setNodeItems(addedNodesList);
                vSet.add("Gorillas");
                editor.putStringSet("visitationList", new HashSet(vSet));
                editor.apply();
            }

            searchText.requestFocus();
            searchText.setText("Koi Fish");
            searchText.clearFocus();

            Log.e("Node 2: ", temp2.name);
            Log.e("Text Search: ", String.valueOf(searchText.getText()));
            if(String.valueOf(searchText.getText()).equals(temp2.name)) {
                Log.e("ENTERED HERE!", "HELLO");
                addedNodesList.add(temp2);
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
}
