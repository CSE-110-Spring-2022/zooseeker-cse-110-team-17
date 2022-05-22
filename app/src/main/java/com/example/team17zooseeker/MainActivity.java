package com.example.team17zooseeker;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ZooKeeperDatabase database;
    private NodeItemDao nodeDao;
    private EdgeItemDao edgeDao;
    private StateDao stateDao;

    private Map<String, nodeItem> nodeMap;

    private List<String> visitationList;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // For MainActivity
        preferences = getPreferences(MODE_PRIVATE);
        String state_retrieved = preferences.getString("state", "default_if_not_found");
        editor = preferences.edit();
        editor.putString("state", "0");
        editor.apply();

        Set<String> vSet = preferences.getStringSet("visitationList", null);

        //if state == 0
        //  We have a non-empty visitation list to populate
        //  After populating, clear the state
        //
        //if state == 1
        //  We have to go to the itinerary activity and refill it
        //  After populating, clear the state

        //
        //if state == 2
        //  We have to go to the specific page in the directions they were on
        //  After populating, clear the state

        //Database stuff
        database = ZooKeeperDatabase.getSingleton(this);

        nodeDao = database.nodeItemDao();
        edgeDao  = database.edgeItemDao();
        stateDao = database.stateDao();

        List<edgeItem> edges = edgeDao.getAll();
        List<nodeItem> nodes = nodeDao.getAll();
        State state = stateDao.get();

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

        if(vSet != null) {

            visitationList = new ArrayList(vSet);

            for(String node : vSet)
                addedNodesList.add(nodeMap.get(node));

            adapter.setNodeItems(addedNodesList);

            exhibitText.setText("( " + visitationList.size() + " )");

        }

        else {
            this.visitationList = new ArrayList<String>();
        }

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

                        editor.putStringSet("visitationList", new HashSet(visitationList));
                        editor.apply();

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
        Log.d("Visitation List: ", this.visitationList.toString());
        //Visitation List needs to be in Ids and not names
        for(int i = 0; i < this.visitationList.size(); i++){
            this.visitationList.set(i, this.nodeMap.get(this.visitationList.get(i)).getId());
        }

        Itinerary.createItinerary(this, this.visitationList);

        this.visitationList.clear();
        editor.putStringSet("visitationList", null);
        editor.apply();

        Intent intent = new Intent(this, ItineraryActivity.class);
        startActivity(intent);
        finish();
    }
}