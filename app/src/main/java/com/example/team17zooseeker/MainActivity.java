package com.example.team17zooseeker;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.jgrapht.Graph;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplication().getApplicationContext();

        Graph<String, identifiedWeightedEdge> g = null;
        try {
            g = graphItem.loadZooGraphJSON(context,"graph.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("GRAPH ITEM", g.toString());

        Map<String, nodeItem> vInfo = null;
        try {
            vInfo = nodeItem.loadNodeInfoJSON(context,"node.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("NODE ITEM", vInfo.toString());

        Map<String, edgeItem> eInfo = null;
        try {
            eInfo = edgeItem.loadEdgeInfoJSON(context, "edge.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("EDGE ITEM", eInfo.toString());
    }
}