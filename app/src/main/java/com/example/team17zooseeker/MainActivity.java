package com.example.team17zooseeker;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ZooKeeperDatabase db;
    private NodeItemDao nodeDao;
    private EdgeItemDao edgeDao;
    private Map<String, nodeItem> nodeMap;

    private List<String> visitationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Database stuff
        ZooKeeperDatabase database = ZooKeeperDatabase.getSingleton(this);

        nodeDao = database.nodeItemDao();
        edgeDao  = database.edgeItemDao();

        List<edgeItem> edges = edgeDao.getAll();
        List<nodeItem> nodes = nodeDao.getAll();

        this.nodeMap = nodes.stream().collect(Collectors.toMap(nodeItem::getName, Function.identity()));

        //Visitation List recycler
        NodeListAdapter adapter = new NodeListAdapter();
        adapter.setHasStableIds(true);

        RecyclerView visitationView = findViewById(R.id.visitation_list_view);
        visitationView.setLayoutManager(new LinearLayoutManager(this));
        visitationView.setAdapter(adapter);

        //Adding an exhibit to visitation list
        EditText searchText = findViewById(R.id.search_text);
        TextView exhibitText = findViewById(R.id.exhibit_count_txt);

        List<nodeItem> addedNodesList = new ArrayList<nodeItem>();
        this.visitationList = new ArrayList<String>();

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;

                if(result == EditorInfo.IME_ACTION_DONE) {
                    String searchQuery = searchText.getText().toString();

                    if (nodeMap.containsKey(searchQuery) &&
                            !(visitationList.contains(searchQuery))) {

                        addedNodesList.add(nodeMap.get(searchQuery));
                        visitationList.add(nodeMap.get(searchQuery).getName());

                        adapter.setNodeItems(addedNodesList);

                        exhibitText.setText("( " + visitationList.size() + " )");
                    }
                }
                searchText.setText("");
                return true;
            }
        });

        Button plan = findViewById(R.id.plan_btn);
        plan.setOnClickListener(this::onPlanClicked);

        //Setting up the autocomplete text field with custom adapter
        AutoCompleteTextView searchTextView = (AutoCompleteTextView)findViewById(R.id.search_text);
        ArrayAdapter<String> autoCompleteAdapter = new AutoCompleteAdapter(this);
        searchTextView.setAdapter(autoCompleteAdapter);
    }

    void onPlanClicked (View view){
        //Visitation List needs to be in Ids and not names
        for(int i = 0; i < this.visitationList.size(); i++){
            this.visitationList.set(i, this.nodeMap.get(this.visitationList.get(i)).getId());
        }
        Itinerary.createItinerary(this, this.visitationList);
        this.visitationList.clear();
        Intent intent = new Intent(this, ItineraryActivity.class);
        startActivity(intent);
    }
}