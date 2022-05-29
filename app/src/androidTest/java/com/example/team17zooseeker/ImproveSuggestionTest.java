package com.example.team17zooseeker;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
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

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ImproveSuggestionTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    private MainActivity mActivity = null;

    @Before
    public void setActivity() {
        mActivity = mActivityRule.getActivity();
    }

    @Test
    public void testForNoGate() {
        onView(withId(R.id.search_text))
                .perform(typeText("en"), closeSoftKeyboard());
        try {
            onView(withText("Entrance and Exit Gate")).check(matches(isDisplayed()));
            fail();
        } catch (NoMatchingViewException e) {
            assertTrue(true);
        }
        onView(withText("Blue Capped Motmot"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withText("Bali Mynah"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withText("Fern Canyon"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withText("Blue Capped Motmot"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .perform(click());
        onView(withId(R.id.search_text))
                .check(matches(withText("Blue Capped Motmot")));
    }

    @Test
    public void testForNoInter() {
        onView(withId(R.id.search_text))
                .perform(typeText("fr"), closeSoftKeyboard());
        try {
            onView(withText("Front Street / Monkey Trail")).check(matches(isDisplayed()));
            fail();
        } catch (NoMatchingViewException e) {
            assertTrue(true);
        }
        try {
            onView(withText("Front Street / Terrace Lagoon Loop (South)")).check(matches(isDisplayed()));
            fail();
        } catch (NoMatchingViewException e) {
            assertTrue(true);
        }
        try {
            onView(withText("Front Street / Terrace Lagoon Loop (North)")).check(matches(isDisplayed()));
            fail();
        } catch (NoMatchingViewException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testForNoGroup() {
        onView(withId(R.id.search_text))
                .perform(typeText("aviary"), closeSoftKeyboard());
        try {
            onView(withText("Owens Aviary")).check(matches(isDisplayed()));
            fail();
        } catch (NoMatchingViewException e) {
            assertTrue(true);
        }
        try {
            onView(withText("Scripps Aviary")).check(matches(isDisplayed()));
            fail();
        } catch (NoMatchingViewException e) {
            assertTrue(true);
        }
        try {
            onView(withText("Parker Aviary")).check(matches(isDisplayed()));
            fail();
        } catch (NoMatchingViewException e) {
            assertTrue(true);
        }
    }
}
