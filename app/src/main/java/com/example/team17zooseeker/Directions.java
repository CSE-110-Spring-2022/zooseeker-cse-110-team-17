package com.example.team17zooseeker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Directions {
    // two locations(vertices) for creating directions
    private static String start,end;
    // List to hold single set of directions from start to end
    private static List<String> dirs;

    private List<String> itinerary; // that's the list of zoo itinerary from the create-itinerary

    private int currentIndex;   // this one is the current index for the itinerary
    // Itinerary class
    // current index

    /**
     * The constructor
     * @param itinerary the list of zoo itinerary from the create-itinerary
     * @param currentIndex the current index for the itinerary
     * for the currentIndex, we can get the value for the start and end.
     */
    public Directions(List<String> itinerary, int currentIndex){


            this.itinerary = itinerary;
            this.currentIndex = currentIndex;
            // the start location is the currentIndex of the itinerary one, end is the next one.
            if (currentIndex <=  itinerary.size() - 2 && currentIndex >= 0){
                Directions.start = itinerary.get(currentIndex);
                Directions.end = itinerary.get(currentIndex + 1);
                Directions.dirs = new ArrayList<>();
            }else {
                throw new RuntimeException("The current Index for the itinerary is out of boundary." +
                        "it should be [0, itineray.size() - 2]");
            }

    }

    /**
     * This creates the directions list from start to end
     * @param context the current application environment
     */
    public void createDirections(Context context){
        /*
         Graph<String, IdentifiedWeightedEdge> g,
         GraphPath<String, IdentifiedWeightedEdge> path,
         Map<String, ZooData.VertexInfo> vInfo,
         Map<String, ZooData.EdgeInfo> eInfo
         */
        // Load the graph...
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(context,"sample_zoo_graph.json");
        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, start, end);

        // 2. Load the information about our nodes and edges...
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(context,"sample_node_info.json");
        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON(context,"sample_edge_info.json");

        System.out.printf("The shortest path from '%s' to '%s' is:\n", start, end);

        int i = 1;
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            @SuppressLint("DefaultLocale") String instruction = String.format("%d. Walk %.0f meters along %s from '%s' to '%s'.",
                    i,
                    g.getEdgeWeight(e),
                    // calls could throw null pointer exceptions
                    // use wrappers until we can ensure input is valid
                    Objects.requireNonNull(eInfo.get(e.getId())).street,
                    Objects.requireNonNull(vInfo.get(g.getEdgeSource(e))).name,
                    Objects.requireNonNull(vInfo.get(g.getEdgeTarget(e))).name);
            Log.d("direction",instruction);
            Log.d("sizePATH",String.valueOf(path.getLength()));
            dirs.add(instruction);
            i++;
        }
    }

    /**
     * This is a getter for the list of directions
     * @return the list of directions from start to end
     */
    public static List<String> getDirs(){
        return dirs;
    }
}
