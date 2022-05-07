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

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@LargeTest
@RunWith( AndroidJUnit4.class )
public class AddExhibitTests {

    @Test
    public void exampleActivityTest() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity -> {
            // GIVEN (this test database state)
            ZooKeeperDatabase testDatabase = Room.inMemoryDatabaseBuilder(activity, ZooKeeperDatabase.class)
                    .allowMainThreadQueries()
                    .build();

            // load json for testing
            // insert into test db
            List<String> temp = new ArrayList<String>();
            testDatabase.nodeItemDao().insert(new nodeItem("lions", "exhibit", "Lions", temp));
            testDatabase.nodeItemDao().insert(new nodeItem("gorillas", "exhibit", "Gorillas", temp));

            ZooKeeperDatabase.injectTestDatabase(testDatabase);

            // WHEN
            ViewInteraction textEntry = onView(allOf(withId(R.id.search_text), isDisplayed()));
            textEntry.perform(replaceText("Lions"), closeSoftKeyboard());
            textEntry.perform(pressImeActionButton());

            //ViewInteraction planButton = onView(allOf(withId(R.id.plan_btn), isDisplayed()));
            //planButton.perform(click());
            //ViewInteraction visitationView = onView(allOf(withId(R.id.visitation_list_view), isDisplayed()));

            // THEN
            // assert by checking that the item is indeed in the plans view
            // onView -> getText -> assert, etc
            String insertedName = "Lions";
            //onView(withText(insertedName)).check(matches(isDisplayed()));
            onView(withId(R.id.visitation_list_view))
                    .check(matches(atPosition(0, withText(insertedName))));
        });
    }
    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

}

