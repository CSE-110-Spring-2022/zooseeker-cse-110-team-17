package com.example.team17zooseeker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Itinerary {
    //Static itinerary list to hold a single organized itinerary
    private static List<String> itinerary = null;

    //Graph data of the zoo to calculate distances between locations.
    private static Graph<String, IdentifiedWeightedEdge> zooMap;
    private static NodeItemDao nodeDao;

    private static boolean itineraryCreated = false;

    private static List<String> newItinerary;

    private static boolean nodeDaoWasInjected = false;

    private static Map<String, ArrayList<String>> groupIdMap = new HashMap<>();

    private static String currLocation = "entrance_exit_gate";

    public static void createItinerary(Context context, List<String> visitationList){
        if(itinerary == null){
            try {
                zooMap = ZooData.loadZooGraphJSON(context, "graph.json");
            }catch (IOException e){ return; }

            //Conditional for testing with database. Tests should always inject a nodeDao
            if(!nodeDaoWasInjected){
                ZooKeeperDatabase database = ZooKeeperDatabase.getSingleton(context);
                nodeDao = database.nodeItemDao();
                Log.d("Itinerary", "Itinerary created with no injected Database.");
            }

            Itinerary.buildItinerary(visitationList);
            itineraryCreated = true;
        }
    }

    private static void buildItinerary(List<String> visitationList1){
        //Handle parent Id edge cases
        List<String> visitationList = Itinerary.Formats(visitationList1);

        itinerary = new ArrayList<String>(visitationList.size() + 1);
        int finalCapacity = visitationList.size();

        //First start from current location
        String nextLocation = findClosestLocation(visitationList, currLocation);
        itinerary.add(nextLocation);
        visitationList.remove(nextLocation);

        //Until the itinerary has every location from the visitation list find the next location
        while(itinerary.size() < finalCapacity){

            nextLocation = findClosestLocation(visitationList, itinerary.get(itinerary.size() - 1));

            //Add the closest destination in visitation list to itinerary and remove it from the
            //visitation list.
            itinerary.add(nextLocation);
            visitationList.remove(nextLocation);
        }
        //When finish rollback to exit
        itinerary.add("entrance_exit_gate");
    }

    private static String findClosestLocation(List<String> visitationList, String from){
        // for every location in the visitation list calculate the distance to the
        // current location and keep track of smallest distance.
        int smallestDistOfDestinations = Integer.MAX_VALUE;
        int indexOfLocationWithSmallestDistance = 0;


        for(int i = 0; i < visitationList.size(); i++){
            int currDist = Itinerary.distance(from, visitationList.get(i));;

            if(currDist < smallestDistOfDestinations){
                smallestDistOfDestinations = currDist;
                indexOfLocationWithSmallestDistance = i;
            }
        }
        return visitationList.get(indexOfLocationWithSmallestDistance);
    }

    public static void updateCurrentLocation(String location){
        if(nodeDaoWasInjected){
            currLocation="entrance_exit_gate";
        }
        else{
            currLocation = location;
        }
    }

    //Helper Function for building Itinerary
    //Returns the shortest distance between the start and end locations on the zooMap
    public static int distance(String start, String end){
        int minDistance = 0;

        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(zooMap, start, end);

        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            minDistance += zooMap.getEdgeWeight(e);
        }

        //Log.d("Edge Weight: ", "" + minDistance);
        return minDistance;
    }

    //Returns true if the query is on the path between start and end
    public static boolean existsOnPath(String start, String end, String query){

        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(zooMap, start, end);

        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            if(e.getFrom().equals(query)){ return true; }
            if(e.getTo().equals(query)){ return true; }
            //Log.d("Edge Info: ", e.toString());
        }

        return false;
    }

    private static List<String> Formats(List<String> visitationList){
        HashSet<String> resultsSet = new HashSet<String>();
        //Loop through all results including tags triggered

        for(String place : visitationList){
            if(nodeDao.get(place).group_id != null){
                resultsSet.add(nodeDao.get(place).group_id);
                //Adds the animals to a map of group Ids so we can keep track of what user wanted to see.
                if(groupIdMap.get(nodeDao.get(place).group_id) == null){
                    ArrayList<String> listToInsert = new ArrayList<>();
                    listToInsert.add(place);
                    groupIdMap.put(nodeDao.get(place).group_id, listToInsert);
                } else {
                    groupIdMap.get(nodeDao.get(place).group_id).add(place);
                }
            }else{
                resultsSet.add(place);
            }
        }

        return new ArrayList<String>(resultsSet);
    }

    public static List<String> getItinerary(){ return itinerary; }

    public static String getNameFromId(String id){ return nodeDao.get(id).getName(); }

    public static String getCurrLocation(){ return currLocation; }

    //Allows for a new itinerary if the use of the previous itinerary has been completed.
    public static void deleteItinerary(){
        itinerary.clear();
        itinerary = null;
    }

    //If false no need for new itinerary if true a better path exists (closest node must be an ID)
    public static boolean checkForReRoute(String closestNode, int currIndex){

        newItinerary = new ArrayList<>();
        ArrayList<String> remainingExhibitVisitationList = new ArrayList<>();

        //Loop through already visited locations
        for(int i = 0; i < currIndex; i++){ //Don't reroute the things we've seen
            //If the closest Node to user is already in itinerary and visited don't add
            if(itinerary.get(i).equals(closestNode)){
                continue;
            }
            newItinerary.add(itinerary.get(i));
        }

        //if the closest node is in our itinerary make it the next thing we see
        if(itinerary.contains(closestNode) && !closestNode.equals("entrance_exit_gate")){
            newItinerary.add(closestNode);
        }
        Log.d("CheckForReRoute-FirstHalfItinerary", newItinerary.toString());

        //Loop through all unvisited locations
        for(int i = currIndex; i < itinerary.size(); i++){
            //Get all remaining exhibits that we haven't been to
            if(!itinerary.get(i).equals("entrance_exit_gate") && !itinerary.get(i).equals(closestNode)){
                remainingExhibitVisitationList.add(itinerary.get(i));
            }
        }
        Log.d("CheckForReRoute-SecondHalfVisitationList", remainingExhibitVisitationList.toString());

        int finalCapacity = itinerary.size() - 1; //It's -1 because we don't have entrance at the end

        //First start from current location
        String nextLocation = findClosestLocation(remainingExhibitVisitationList, currLocation);
        newItinerary.add(nextLocation);
        remainingExhibitVisitationList.remove(nextLocation);

        while(newItinerary.size() < finalCapacity){

            nextLocation = findClosestLocation(remainingExhibitVisitationList, newItinerary.get(newItinerary.size() - 1));

            //Add the closest destination our of remaining items to new itinerary and remove it.
            newItinerary.add(nextLocation);
            remainingExhibitVisitationList.remove(nextLocation);
        }
        //We want to leave the zoo at the end
        newItinerary.add("entrance_exit_gate");

        Log.d("CheckForReRoute-FinalNewItinerary", newItinerary.toString());
        Log.d("CheckForReRoute-CurrentItinerary", itinerary.toString());

        //Check the ends of the current Itinerary to the new itinerary and see if they are different
        int currItinSize = itinerary.size();
        int newItinSize = newItinerary.size();
        //Loop over the total remaining items in the itinerary
        for(int i = 0; i < itinerary.size() - (currIndex); i++){
            //If the ends of the each itinerary are the not the same we need to reRoute
            if(!itinerary.get(currItinSize - i - 1).equals(newItinerary.get(newItinSize - i - 1))){
                Log.d("CheckForReRoute", "Better Path Exists");
                return true;
            }
        }

        Log.d("CheckForReRoute", "Same route using current location");
        return false;
    }

    public static void newItineraryAccepted(){
        itinerary = newItinerary;
    }

    //So when running multiple tests at one time you can reset the static itinerary.
    @VisibleForTesting
    public static void injectTestItinerary(List<String> itin){
        itinerary = itin;
    }

    //Must inject a nodeDao for tests to work
    @VisibleForTesting
    public static void injectTestNodeDao(NodeItemDao noDao){
        nodeDaoWasInjected = true;
        nodeDao = noDao;
    }

    public static boolean isItineraryCreated(){ return itineraryCreated; }

    public static void setItineraryCreated(boolean created){ itineraryCreated = created; }

    public static void skip(){
        itinerary.remove(Directions.getCurrentIndex());
    }

    //Must already exist or will through a null pointer
    public static ArrayList<String> getAnimalsVisited(String query){
        if(groupIdMap.get(query) == null){
            return new ArrayList<>();
        }
        return groupIdMap.get(query);
    }

    public static void injectMockItinerary() {
        itinerary = new ArrayList<String>();
        itinerary.add("entrance_exit_gate");
        itinerary.add("gorilla");
        itinerary.add("koi");
        itinerary.add("entrance_exit_gate");
    }

    //Developer Notes----------
    // for (IdentifiedWeightedEdge e : zooMap.edgeSet()) {
    //     Log.d("Edge: ", e.toString());
    // }
}
