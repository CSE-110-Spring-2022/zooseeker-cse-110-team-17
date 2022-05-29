package com.example.team17zooseeker;


import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.annotation.UiThreadTest;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.UiThreadTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@LargeTest
@RunWith( AndroidJUnit4.class )
public class AddExhibitTests {
    ZooKeeperDatabase testDb;
    NodeItemDao nodeItemDao;

    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ZooKeeperDatabase.class)
                .allowMainThreadQueries()
                .build();
        ZooKeeperDatabase.injectTestDatabase(testDb);
    }

    @Test
    public void testAddValidExhibit() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            List<String> temp = new ArrayList<String>();
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

            searchText.requestFocus();
            searchText.setText("Gorillas");
            searchText.clearFocus();
            List<nodeItem> addedNodesList = new ArrayList<nodeItem>();

            Log.e("Node 1: ", temp1.name);
            Log.e("Text Search: ", String.valueOf(searchText.getText()));
            if(String.valueOf(searchText.getText()).equals(temp1.name)) {
                Log.e("ENTERED HERE!", "HELLO");
                addedNodesList.add(temp1);
                adapter.setNodeItems(addedNodesList);
            }

            Log.e("Search Text: ", String.valueOf(searchText.getText()));
            Log.e("Adapter: ->>>", String.valueOf(adapter.getItemCount()));
            Log.e("VH: ", String.valueOf(adapter.getItemName(0)));
            assertEquals(String.valueOf(searchText.getText()), String.valueOf(adapter.getItemName(0)));
        });
    }

    @Test
    public void testAddInvalidExhibit() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity -> {
            List<String> temp = new ArrayList<String>();
            testDb.nodeItemDao().insert(new nodeItem("gorillas", "exhibit", "Gorillas","000",1,1, temp));
            testDb.nodeItemDao().insert(new nodeItem("koi", "exhibit", "Koi Fish", "111", 0,0, temp));

            nodeItem temp1 = testDb.nodeItemDao().get("Tapirs");

            EditText searchText = activity.findViewById(R.id.search_text);
            RecyclerView vh = activity.findViewById(R.id.visitation_list_view);
            NodeListAdapter adapter = new NodeListAdapter();
            adapter.setHasStableIds(true);
            vh.setAdapter(adapter);

            searchText.requestFocus();
            searchText.setText("Tapirs");
            searchText.clearFocus();

            assertNull(temp1);

        });
    }
}