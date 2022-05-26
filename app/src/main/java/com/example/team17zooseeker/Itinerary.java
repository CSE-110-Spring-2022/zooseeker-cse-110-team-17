package com.example.team17zooseeker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Itinerary {
    //Static itinerary list to hold a single organized itinerary
    private static List<String> itinerary = null;

    //Graph data of the zoo to calculate distances between locations.
    private static Graph<String, IdentifiedWeightedEdge> zooMap;
    private static NodeItemDao nodeDao;

    public static void createItinerary(Context context, List<String> visitationList){
        if(itinerary == null){
            try {
                zooMap = ZooData.loadZooGraphJSON(context, "graph.json");
                ZooKeeperDatabase database = ZooKeeperDatabase.getSingleton(context);
                nodeDao = database.nodeItemDao();
            }catch (IOException e){ return; }

            Itinerary.buildItinerary(visitationList);
        }
    }

    private static void buildItinerary(List<String> visitationList){

        itinerary = new ArrayList<String>(visitationList.size() + 1);
        int finalCapacity = visitationList.size() + 1;
        itinerary.add("entrance_exit_gate"); //Always start at the entrance

        //itinerary.add("entrance_exit_gate"); //Always start at the entrance

        //Until the itinerary has every location from the visitation list find the next location
        int indexOfCurrLocation = 0;
        while(itinerary.size() < finalCapacity){

            String currentLocation = itinerary.get(indexOfCurrLocation);

            // for every location in the visitation list calculate the distance to the
            // current location and keep track of smallest distance.
            int smallestDistOfDestinations = Integer.MAX_VALUE;
            int indexOfLocationWithSmallestDistance = 0;
            for(int i = 0; i < visitationList.size(); i++){
                int currDist = Itinerary.distance(currentLocation, visitationList.get(i));
                if(currDist < smallestDistOfDestinations){
                    smallestDistOfDestinations = currDist;
                    indexOfLocationWithSmallestDistance = i;
                }
            }

            //Add the closest destination in visitation list to itinerary and remove it from the
            //visitation list.
            itinerary.add(visitationList.get(indexOfLocationWithSmallestDistance));
            visitationList.remove(indexOfLocationWithSmallestDistance);
            indexOfCurrLocation++;
        }
        //When finish rollback to exit
        itinerary.add("entrance_exit_gate");
    }

    //Helper Function for building Itinerary
    //Returns the shortest distance between the start and end locations on the zooMap
    public static int distance(String start, String end){
        int minDistance = 0;

        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(zooMap, start, end);

        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            minDistance += zooMap.getEdgeWeight(e);
        }

        Log.d("Edge Weight: ", "" + minDistance);
        return minDistance;
    }

    public static List<String> getItinerary(){ return itinerary; }

    public static String getNameFromId(String id){ return nodeDao.get(id).getName(); }

    //Allows for a new itinerary if the use of the previous itinerary has been completed.
    public static void deleteItinerary(){
        itinerary.clear();
        itinerary = null;
    }

    //So when running multiple tests at one time you can reset the static itinerary.
    @VisibleForTesting
    public static void injectTestItinerary(List<String> itin){
        itinerary = itin;
    }

    public static void injectTestNodeDao(NodeItemDao noDao){
        nodeDao = noDao;
    }

    //Developer Notes----------
    // for (IdentifiedWeightedEdge e : zooMap.edgeSet()) {
    //     Log.d("Edge: ", e.toString());
    // }
}
