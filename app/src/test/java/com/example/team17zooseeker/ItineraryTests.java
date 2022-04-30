package com.example.team17zooseeker;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ItineraryTests {
    @Test
    public void testShortestPathInItinerary(){
        String[] vL = {"entrance_plaza", "entrance_exit_gate", "lions"};
        ArrayList<String> testVisitationList = new ArrayList<String>(Arrays.asList(vL));
        Context context = ApplicationProvider.getApplicationContext();
        Itinerary.createItinerary(context, testVisitationList);

        assertEquals(310, Itinerary.distance("entrance_exit_gate", "lions"));
    }

    @Test
    public void testValidCreationOfItinerary() {
        String[] vL = {"entrance_plaza", "entrance_exit_gate", "lions"};
        ArrayList<String> testVisitationList = new ArrayList<String>(Arrays.asList(vL));

        Context context = ApplicationProvider.getApplicationContext();
        Itinerary.createItinerary(context, testVisitationList);
        List<String> testItinerary = Itinerary.getItinerary();

        String[] cI = {"entrance_exit_gate", "entrance_plaza", "lions"};
        ArrayList<String> correctItinerary = new ArrayList<String>(Arrays.asList(cI));

        assertEquals(correctItinerary, testItinerary);
    }
}
