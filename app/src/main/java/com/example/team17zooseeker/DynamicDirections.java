package com.example.team17zooseeker;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;

public class DynamicDirections {

    private static DynamicDirections singleDyno = null;

    //For holding user location data
    private final MutableLiveData<Pair<Double, Double>> lastKnownCoordinates;
    private Location lastKnownLocation;
    private static NodeItemDao nodeDao;
    //Needs the activity to prompt user in.
    private static Activity currActivity = null;

    private static boolean dynamicEnabled = false;

    private static String closestLocationTitle;

    private static final String pathChangedPrompt = "You are close to %s. Would you like to be rerouted from there?";

    private static boolean nodeDaoWasInjected = false;

    private static boolean locationCurrentlyMocked = false;

    private DynamicDirections(Context context, Activity activity) {
        //Conditional for testing with database. Tests should always inject a nodeDao
        if(!nodeDaoWasInjected){
            ZooKeeperDatabase database = ZooKeeperDatabase.getSingleton(context);
            nodeDao = database.nodeItemDao();
            Log.d("DynoDirections", "DynoDirections created with no injected Database.");
        }
        lastKnownCoordinates = new MutableLiveData<>(null);
        lastKnownLocation = new Location("lastKnownLocation");
        currActivity = activity;
    }

    public synchronized static DynamicDirections getSingleDyno(Context context, Activity activity){
        if(singleDyno == null){
            singleDyno = new DynamicDirections(context, activity);
        }
        DynamicDirections.setCurrActivity(activity);
        return singleDyno;
    }

    private void checkForReRouteFromCurrentLocation(){
        //The closest location in zoo to their live coordinates
        String closestLocation = findClosestLocation();
        closestLocationTitle = nodeDao.get(closestLocation).name;

        //Check if they are currently on predicted path. If so don't reroute them.
        String start = Directions.getCurrStartPos();
        String end = Itinerary.getItinerary().get(Directions.getCurrentIndex());

        if(Itinerary.existsOnPath(start, end, closestLocation)){ Log.d("DynoDirections-OnPredictedPath", "True"); return; }

        //Edge case if start and end are the same
        if(start.equals(end)) { return; }

        Utilities.promptUpdatePath(currActivity, String.format(pathChangedPrompt, closestLocationTitle));
    }

    public static void pathApproved(){
        //Rebase Itinerary, update directions itinerary, update display
        Log.d("Path Update Approved", "True");

        boolean reRoute = Itinerary.checkForReRoute(singleDyno.findClosestLocation(), Directions.getCurrentIndex());
        Log.d("DynoDirections-ReRoute", String.valueOf(reRoute));
        if(reRoute){
            Itinerary.newItineraryAccepted();
            Directions.updateItinerary();
        }

        //This assumes we only check if the path is approved from the directions activity
        DirectionsActivity dA = (DirectionsActivity) currActivity;
        dA.getAdapter().itineraryUpdated();
    }

    //return the node in the graph that is closest to our last known location
    private String findClosestLocation(){
        List<nodeItem> nodes = nodeDao.getAll();
        nodeItem closestNode = null; //new nodeItem("scripps_aviary", "Test", "Test", "Test", 1,1, null) should always be set but just in case
        double shortestDistance = Double.MAX_VALUE;
        //Loop through all nodes in database. Save closest node
        for(nodeItem node : nodes){
            double currDistance = distanceToNode(node);
            //Redundant check because the distance to these exhibits is massive because the lat and lng are 0
            if(node.group_id != null){
                continue;
            }
            if(currDistance < shortestDistance){
                Log.d("DynoDirections-CheckedNode", node.id);
                closestNode = node;
                shortestDistance = currDistance;
            }
        }
        return closestNode.id;
    }

    //Helper distance function for finding closest node
    private double distanceToNode(nodeItem node){
        Location destination = new Location(node.id);
        destination.setLatitude(node.lat);
        destination.setLongitude(node.lng);

        double distance = lastKnownLocation.distanceTo(destination);
        return distance;
    }

    //For Mocking and Updating User location
    public void updateUserLocation(Pair<Double, Double> updatedLocation){

        lastKnownCoordinates.setValue(updatedLocation);
        //new Pair<Double, Double>(32.73459618734685,-117.14936) Entrance Gate
        //new Pair<Double, Double>(32.7440416465169,-117.15952052282296) Flamingo
        //new Pair<Double, Double>(32.74531131120979,-117.16626781198586) Hippo

        //Set our location
        lastKnownLocation.setLatitude(lastKnownCoordinates.getValue().first);
        lastKnownLocation.setLongitude(lastKnownCoordinates.getValue().second);

        Log.d("DynoDirections", lastKnownCoordinates.getValue().toString());

        Itinerary.updateCurrentLocation(findClosestLocation());

        //Check if we need to reroute if Itinerary has been created
        if(Itinerary.isItineraryCreated() && dynamicEnabled){
            checkForReRouteFromCurrentLocation();
        }
    }

    public void updateUserLocationFromLocationListener(Pair<Double, Double> updatedLocation){
        //Set our coordinates until they are mocked coordinates
        if(!locationCurrentlyMocked){
            updateUserLocation(updatedLocation);
        }
    }

    //Getting user location
    public Pair<Double, Double> getUserLocation(){ return lastKnownCoordinates.getValue(); }

    public static void setCurrActivity(Activity activity){
        currActivity = activity;
        if(Utilities.getUpdateCurrentlyPrompted()){
            Utilities.promptUpdatePath(currActivity, String.format(pathChangedPrompt, closestLocationTitle));
        }
    }

    public static void setDynamicEnabled(boolean enable){ dynamicEnabled = enable; }

    public static void setLocationCurrentlyMocked(boolean mocked){ locationCurrentlyMocked = mocked; }

    public String getClosestLocationID(){
        return findClosestLocation();
    }

    //Must inject a nodeDao for tests to work
    @VisibleForTesting
    public static void injectTestNodeDao(NodeItemDao noDao){
        nodeDaoWasInjected = true;
        nodeDao = noDao;
    }
}