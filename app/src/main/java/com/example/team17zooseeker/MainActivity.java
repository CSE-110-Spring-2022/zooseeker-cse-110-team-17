package com.example.team17zooseeker;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import org.jgrapht.Graph;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ZooKeeperDatabase db;
    private NodeItemDao nodeDao;
    private EdgeItemDao edgeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ZooKeeperDatabase database = ZooKeeperDatabase.getSingleton(this);

        NodeListAdapter adapter = new NodeListAdapter();
        adapter.setHasStableIds(true);

        nodeDao = database.nodeItemDao();
        edgeDao  = database.edgeItemDao();

        List<nodeItem> nodes = nodeDao.getAll();
        List<edgeItem> edges = edgeDao.getAll();

        Map<String, nodeItem> nodeMap = nodes.stream().collect(Collectors.toMap(nodeItem::getName, Function.identity()));
        Map<String, edgeItem> edgeMap = edges.stream().collect(Collectors.toMap(edgeItem::getId, Function.identity()));

        Log.e("Starting Node List: ", nodes.toString());
        Log.e("Starting Node Map: ", nodeMap.toString());

        Button plan = findViewById(R.id.plan_btn);
        EditText searchText = findViewById(R.id.search_text);
        TextView exhibitText = findViewById(R.id.exhibit_count_txt);

        RecyclerView visitationView = findViewById(R.id.visitation_list_view);
        visitationView.setLayoutManager(new LinearLayoutManager(this));
        visitationView.setAdapter(adapter);

        //List<String> visitationList = Collections.emptyList();
        List<nodeItem> addedNodesList = new ArrayList<nodeItem>();
        List<String> visitationList = new ArrayList<String>();

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;

                if(result == EditorInfo.IME_ACTION_DONE) {
                    String searchQuery = searchText.getText().toString();

                    if (nodeMap.containsKey(searchQuery) &&
                            !(visitationList.contains(searchQuery))) {

                        searchText.setText("");

                        addedNodesList.add(nodeMap.get(searchQuery));
                        visitationList.add(nodeMap.get(searchQuery).getName());

                        Log.e("Current VList: ", visitationList.toString());

                        adapter.setNodeItems(addedNodesList);

                        exhibitText.setText("( " + visitationList.size() + " )");

                    }
                }

                return true;

            }

        });
    }
}