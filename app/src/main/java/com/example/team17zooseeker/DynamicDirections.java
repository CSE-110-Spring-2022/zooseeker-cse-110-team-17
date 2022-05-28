package com.example.team17zooseeker;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class DynamicDirections {

    //For holding user location data
    private final MutableLiveData<Pair<Double, Double>> lastKnownCoordinates;
    private Location lastKnownLocation;
    private static NodeItemDao nodeDao;
    //Needs the activity to prompt user in.
    private static Activity currActivity = null;

    private static boolean dynamicEnabled = true;

    public DynamicDirections(Context context, Activity activity) {
        ZooKeeperDatabase database = ZooKeeperDatabase.getSingleton(context);
        nodeDao = database.nodeItemDao();
        lastKnownCoordinates = new MutableLiveData<>(null);
        lastKnownLocation = new Location("lastKnownLocation");
        currActivity = activity;
    }

    private void checkForReRouteFromCurrentLocation(){
        String closestLocation = findClosestLocation();
        boolean reRoute = Itinerary.checkForReRoute(closestLocation, Directions.getCurrentIndex());
        Log.d("DynoDirections-ReRoute", String.valueOf(reRoute));
        if(reRoute){


            //Check if they are currently on predicted path. TO_DO!!!
            //If itinerary has already approved a reroute. Ask directions if we also need a reroute


            Utilities.promptUpdatePath(currActivity, "TBD");
            //"You are close to 'this' exhibit. Would you like to reroute from here?
        }
    }

    //TO-DO
    public static void pathApproved(){
        //Rebase Itinerary
        Log.d("Path Approved", "yes");
    }

    //return the node in the graph that is closest to our last known location
    private String findClosestLocation(){
        List<nodeItem> nodes = nodeDao.getAll();
        nodeItem closestNode = null; //new nodeItem("scripps_aviary", "Test", "Test", "Test", 1,1, null) should always be set but just in case
        double shortestDistance = Double.MAX_VALUE;
        //Loop through all nodes in database. Save closest node
        for(nodeItem node : nodes){
            double currDistance = distanceToNode(node);
            if(!node.kind.equals("exhibit") && !node.kind.equals("exhibit_group")){
                continue;
            }
            if(node.lat != 0 && currDistance < shortestDistance){
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
        //Set our coordinates
        lastKnownCoordinates.setValue(updatedLocation);
        //Set our location
        lastKnownLocation.setLatitude(lastKnownCoordinates.getValue().first);
        lastKnownLocation.setLongitude(lastKnownCoordinates.getValue().second);

        Log.d("DynoDirections", updatedLocation.toString());

        //Check if we need to reroute if Itinerary has been created
        if(Itinerary.isItineraryCreated() && dynamicEnabled){
            checkForReRouteFromCurrentLocation();
        }
    }

    //Getting user location
    public Pair<Double, Double> getUserLocation(){ return lastKnownCoordinates.getValue(); }

    public static void setCurrActivity(Activity activity){
        currActivity = activity;
        if(Utilities.getUpdateCurrentlyPrompted()){
            Utilities.promptUpdatePath(currActivity, "TBD");
        }
    }

    public static void setDynamicEnabled(boolean enable){ dynamicEnabled = enable; }
}