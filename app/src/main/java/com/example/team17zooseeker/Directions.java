package com.example.team17zooseeker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Directions {

    // Optimized itinerary to traverse through zoo
    private static List<String> itinerary;
    /**
     * Can we possibly make this list of strings, exhibit names, not id names?
     */
    // current index for iterating through the itinerary
    private static int currentIndex;

    private boolean detailedDirection = true;
    private boolean dataLoaded = false;

    private DynamicDirections dynoDirections;

    private static String currStartPos;

    private static boolean currentlyTesting = false;

    //Zoo Data
    Graph<String, IdentifiedWeightedEdge> g = null;
    Map<String, ZooData.VertexInfo> vInfo = null;
    Map<String, ZooData.EdgeInfo> eInfo = null;

    public Directions(List<String> itinerary, DynamicDirections dynoDirections, int index) {
        this.itinerary = itinerary;
        this.dynoDirections = dynoDirections;
        this.currentIndex = index;
    }

    public static int getCurrentIndex(){
        return currentIndex;
    }
    public static void resetCurrentIndex(){ currentIndex = 0; }
    public int getItinerarySize(){
        return itinerary.size();
    }
    public static String getCurrStartPos(){ return currStartPos; }
    public void setItinerary(List<String> itinerary) {this.itinerary = itinerary;}

    @VisibleForTesting
    public List<String> createTestDirections(Context context) {

        if(dynoDirections == null){
            currStartPos = "entrance_exit_gate";
        } else {
            currStartPos = dynoDirections.getClosestLocationID();
        }
        String end = itinerary.get(currentIndex);

        //Load Data if needed
        if(!dataLoaded){
            this.getZooData(context);
        }

        //Find a path between start and end
        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, currStartPos, end);

        if(detailedDirection){
            return getDetailedDirections(path, currStartPos);
        }

        return getSimpleDirections(path, currStartPos);
    }

    private List<String> getDetailedDirections(GraphPath<String, IdentifiedWeightedEdge> path, String start){
        List<String> dirs = new ArrayList<>();

        int i = 1;
        // state for maintaining proper direction
        String tempEnd = "";
        //string builder to build instruction
        StringBuilder instructionBuilder = new StringBuilder();
        Log.d("edge list", path.getEdgeList().toString());
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            instructionBuilder.setLength(0); // reset/empty the string builder

            //distance to be walked along an edge (street)
            @SuppressLint("DefaultLocale")
            String street = String.format("%d. Walk %.0f feet along %s ",
                    i,
                    g.getEdgeWeight(e),
                    // calls could throw null pointer exceptions
                    // use wrappers until we can ensure input is valid
                    Objects.requireNonNull(eInfo.get(e.getId())).street);
            instructionBuilder.append(street); //append to string builder

            //keep source and target data
            ZooData.VertexInfo target = Objects.requireNonNull(vInfo.get(g.getEdgeTarget(e)));
            ZooData.VertexInfo source = Objects.requireNonNull(vInfo.get(g.getEdgeSource(e)));
            String exhibits;

            // logic for direction checking, both to initialize and end
            if ((i == 1 && source.id.equals(start)) || tempEnd.equals(source.name)) {
                exhibits = String.format("from '%s' to '%s'.",
                        source.name,
                        target.name);
                tempEnd = target.name;
            } else {
                exhibits = String.format("from '%s' to '%s'.",
                        target.name,
                        source.name);
                tempEnd = source.name;
            }

            instructionBuilder.append(exhibits);
            String res = instructionBuilder.toString();
            Log.d("direction", res);
            dirs.add(res);
            i++;
        }

        return dirs;
    }

    private List<String> getSimpleDirections(GraphPath<String, IdentifiedWeightedEdge> path, String start){
        List<String> dirs = new ArrayList<>();
        List<IdentifiedWeightedEdge> edges = path.getEdgeList();
        int edgeSize = edges.size();


        int i = 1;
        // state for maintaining proper direction
        String tempEnd = "";
        String edge1;
        ZooData.VertexInfo ogSource = null;
        ZooData.VertexInfo ogTarget = null;

        double dist = 0;
        //string builder to build instruction
        StringBuilder instructionBuilder = new StringBuilder();
        Log.d("edge list", path.getEdgeList().toString());

        for (int j = 0; j < edgeSize; j++) {
            instructionBuilder.setLength(0); // reset/empty the string builder
            //keep source and target data
            ZooData.VertexInfo source = Objects.requireNonNull(vInfo.get(g.getEdgeSource(edges.get(j))));
            ZooData.VertexInfo target = Objects.requireNonNull(vInfo.get(g.getEdgeTarget(edges.get(j))));

            if(dist == 0){
                ogSource = Objects.requireNonNull(vInfo.get(g.getEdgeSource(edges.get(j))));
                ogTarget = Objects.requireNonNull(vInfo.get(g.getEdgeTarget(edges.get(j))));
            }

            String exhibits;

            // logic for direction checking, both to initialize and end
            if ((i == 1 && source.id.equals(start)) || tempEnd.equals(source.name)) {
                exhibits = String.format("from '%s' to '%s'.",
                        ogSource.name,
                        target.name);
                tempEnd = target.name;
            } else {
                exhibits = String.format("from '%s' to '%s'.",
                        ogTarget.name,
                        source.name);
                tempEnd = source.name;
            }

            //distance to be walked along an edge (street)
            dist += g.getEdgeWeight(edges.get(j));
            edge1 = Objects.requireNonNull(eInfo.get(edges.get(j).getId())).street;

            @SuppressLint("DefaultLocale")
            String street = String.format("%d. Walk %.0f feet along %s ",
                    i,
                    dist,
                    edge1);
            instructionBuilder.append(street); //append to string builder

            if(j < edgeSize - 1){
                String edge2 = Objects.requireNonNull(eInfo.get(edges.get(j+1).getId())).street;
                if(edge1.equals(edge2)){
                    continue;
                }
            }

            dist = 0;
            instructionBuilder.append(exhibits);
            String res = instructionBuilder.toString();

            dirs.add(res);
            i++;
        }
        return dirs;
    }

    private void getZooData(Context context){
        //Get the graph info
        try {
            g = ZooData.loadZooGraphJSON(context, "graph.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Load the information about our nodes and edges
        try {
            vInfo = ZooData.loadVertexInfoJSON(context, "node.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            eInfo = ZooData.loadEdgeInfoJSON(context, "edge.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Data is now loaded
        dataLoaded = true;
    }

    public void setDetailedDirections(boolean bool){ this.detailedDirection = bool; }
      
    @VisibleForTesting
    public void skipDirections(){
        Itinerary.skip();
        itinerary = Itinerary.getItinerary();
        Directions.decreaseCurrentPosition();
    }

    public static void updateItinerary(){
        //Get new path
        itinerary = Itinerary.getItinerary();
    }



    public static void increaseCurrentPosition(){ if(currentIndex < Itinerary.getItinerary().size() - 1) currentIndex++; }
    public static void decreaseCurrentPosition(){ if(currentIndex > 0) currentIndex--; }
}