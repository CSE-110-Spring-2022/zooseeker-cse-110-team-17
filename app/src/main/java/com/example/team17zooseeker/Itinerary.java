package com.example.team17zooseeker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import org.jgrapht.Graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Itinerary {
    //Static itinerary list to hold a single organized itinerary
    private static List<String> itinerary = null;

    //Graph data of the zoo to calculate distances between locations.
    private static Graph<String, IdentifiedWeightedEdge> zooMap;

    public static void createItinerary(Context context, List<String> visitationList){
        if(itinerary == null){
            try {
                zooMap = ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");
            }catch (IOException e){ return; }
            Itinerary.buildItinerary(visitationList);
        }
    }

    private static void buildItinerary(List<String> visitationList){
        //Implement
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
