package com.example.team17zooseeker;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Itinerary {
    //Static itinerary list to hold a single organized itinerary
    private static List<String> itinerary = null;

    //Graph data of the zoo to calculate distances between locations.
    //private static Graph<String, IdentifiedWeightedEdge> zooMap;

    public static void createItinerary(List<String> visitationList){
        if(itinerary == null){
            Itinerary.buildItinerary(visitationList);
        }
    }

    private static void buildItinerary(List<String> visitationList){
        //Implement algorithm
    }

    //Helper Function for building Itinerary
    //Returns the shortest distance between the start and end locations on the zooMap
    @VisibleForTesting
    public static int distance(String start, String end){
        //Implement algorithm
        return -1;
    }

    public static List<String> getItinerary(){ return itinerary; }

    @VisibleForTesting
    public void injectTestItinerary(List<String> itinerary){
        this.itinerary = itinerary;
    }
}
