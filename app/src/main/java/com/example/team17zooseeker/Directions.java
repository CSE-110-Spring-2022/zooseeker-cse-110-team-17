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
    private final List<String> itinerary;
    /**
     * Can we possibly make this list of strings, exhibit names, not id names?
     */
    // current index for iterating through the itinerary
    private static int currentIndex;

    private boolean detailedDirection = true;
    private boolean dataLoaded = false;

    //Zoo Data
    Graph<String, IdentifiedWeightedEdge> g = null;
    Map<String, ZooData.VertexInfo> vInfo = null;
    Map<String, ZooData.EdgeInfo> eInfo = null;
    /**
     * The constructor
     *
     * @param itinerary    the list of zoo itinerary from the create-itinerary
     * @param currentIndex the current index for the itinerary
     */
    public Directions(List<String> itinerary, int currentIndex) {
        //What does this do????
        if(itinerary.size() >= 2 && itinerary.get(0).equals(itinerary.get(1)))
            itinerary.remove(0);

        this.itinerary = itinerary;
        this.currentIndex = currentIndex;
    }

    public static int getCurrentIndex(){
        return currentIndex;
    }

    public int getItinerarySize(){
        return itinerary.size();
    }

    /**
     * This creates the directions list from current index to current index + 1
     *
     * @param context the current application environment
     */
    @VisibleForTesting
    public List<String> createDirections(Context context, boolean forward) {

        //Database stuff
        ZooKeeperDatabase database = ZooKeeperDatabase.getSingleton(context);

        NodeItemDao nodeDao = database.nodeItemDao();
        EdgeItemDao edgeDao  = database.edgeItemDao();

        List<edgeItem> edges = edgeDao.getAll();
        List<nodeItem> nodes = nodeDao.getAll();

        Map<String, nodeItem> nodeMap = nodes.stream().collect(Collectors.toMap(nodeItem::getId, Function.identity()));
        Map<String, edgeItem> edgeMap = edges.stream().collect(Collectors.toMap(edgeItem::getId, Function.identity()));

        String start;
        String end;
        List<String> dirs = new ArrayList<>();

        if (currentIndex <= itinerary.size() - 2 && currentIndex >= 0) {

            if(forward) {
                start = itinerary.get(currentIndex);
                end = itinerary.get(currentIndex + 1);
                currentIndex++;
            } else {
                currentIndex--;
                start = itinerary.get(currentIndex);
                end = itinerary.get(currentIndex - 1);
            }
        } else {
            return new ArrayList<>();
        }

        Graph<String, IdentifiedWeightedEdge> g = null;
        try {
            g = ZooData.loadZooGraphJSON(context, "graph.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, start, end);

        /*
         Graph<String, IdentifiedWeightedEdge> g,
         GraphPath<String, IdentifiedWeightedEdge> path,
         Map<String, ZooData.VertexInfo> vInfo,
         Map<String, ZooData.EdgeInfo> eInfo
         */
        // Load the graph...

        System.out.printf("The shortest path from '%s' to '%s' is:\n", start, end);

        int i = 1;
        // state for maintaining proper direction
        String tempEnd = "";
        //string builder to build instruction
        StringBuilder instructionBuilder = new StringBuilder();

        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            instructionBuilder.setLength(0); // reset/empty the string builder

            //distance to be walked along an edge (street)
            @SuppressLint("DefaultLocale") String street = String.format("%d. Walk %.0f meters along %s ",
                    i,
                    g.getEdgeWeight(e),
                    // calls could throw null pointer exceptions
                    // use wrappers until we can ensure input is valid
                    Objects.requireNonNull(edgeMap.get(e.getId())).street);
            instructionBuilder.append(street); //append to string builder

            //keep source and target data
            nodeItem target = Objects.requireNonNull(nodeMap.get(g.getEdgeTarget(e)));
            nodeItem source = Objects.requireNonNull(nodeMap.get(g.getEdgeSource(e)));
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
            // Log.d("sizePATH",String.valueOf(path.getLength()));
            dirs.add(res);
            i++;
        }
        return dirs;
    }

    @VisibleForTesting
    public List<String> createTestDirections(Context context, boolean forward) {

        String start;
        String end;
        List<String> dirs = new ArrayList<>();
        Log.d("pre index", String.valueOf(currentIndex));
        if (currentIndex <= itinerary.size() - 1 && currentIndex >= 0) {
            if(!forward) {
                currentIndex--;
                start = itinerary.get(currentIndex);
                end = itinerary.get(currentIndex - 1);
            }
            else if (currentIndex <= itinerary.size() - 2) {
                start = itinerary.get(currentIndex);
                end = itinerary.get(currentIndex + 1);
                currentIndex++;
            } else {
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }

        //Load Data if needed
        if(!dataLoaded){
            this.getZooData(context);
        }

        //Find a path between start and end
        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, start, end);

        if(detailedDirection){
            return getDetailedDirections(path, start);
        }

        return getSimpleDirections(path, start);
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
            String street = String.format("%d. Walk %.0f meters along %s ",
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


    //TO-DO
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
            String street = String.format("%d. Walk %.0f meters along %s ",
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
        itinerary.remove(currentIndex);
        currentIndex--;
    }

}