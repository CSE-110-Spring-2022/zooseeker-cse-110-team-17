package com.example.team17zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DirectionsActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private Button nextBtn;
    private Button prevBtn;
    private Button skipBtn;

    private DirectionsAdapter adapter;

    private ZooKeeperDatabase database;
    private StateDao stateDao;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    ArrayList<String> VList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        // For Directions Activity
        database = ZooKeeperDatabase.getSingleton(this);
        stateDao = database.stateDao();

        stateDao.delete(stateDao.get());
        stateDao.insert(new State("2"));

        preferences = getPreferences(MODE_PRIVATE);
        editor = preferences.edit();

        Bundle extras = getIntent().getExtras();

        if(extras != null) {

            VList = extras.getStringArrayList("VList");
            editor.putStringSet("VList", new HashSet(VList));
            editor.putInt("ItinIndex", 0);
            editor.apply();

        }

        Set<String> VSet = preferences.getStringSet("VList", null);
        int index = preferences.getInt("ItinIndex", 0);

        if(VSet != null)
        {

            VList = new ArrayList(VSet);

            Itinerary.createItinerary(this, VList);

        }

        Directions d = new Directions(Itinerary.getItinerary(), index);
        adapter = new DirectionsAdapter(d);

        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.directions_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        skipBtn = findViewById(R.id.skip_btn);
        skipBtn.setOnClickListener(this::onSkipClicked);
      
        prevBtn = findViewById(R.id.prev_btn);
        prevBtn.setOnClickListener(this::onPrevClicked);
      
        nextBtn = findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(this::onNextClicked);
        adapter.setDirectItems(DirectionsActivity.this, prevBtn, skipBtn, nextBtn, true, false);
    }

    public void onPrevClicked (View view) {
        adapter.setDirectItems(DirectionsActivity.this, prevBtn, skipBtn, nextBtn, false, false);
    }

    public void onSkipClicked (View view) {
            adapter.setDirectItems(DirectionsActivity.this, prevBtn, skipBtn, nextBtn, true, true);
    }

    public void onNextClicked (View view){
        if(nextBtn.getText().equals("FINISH")){
            Itinerary.deleteItinerary();

            stateDao.delete(stateDao.get());
            stateDao.insert(new State("0"));

            editor.putStringSet("VList", null);
            editor.putInt("ItinIndex", 0);
            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            editor.putInt("ItinIndex", preferences.getInt("ItinIndex", 0) + 1);
            editor.apply();
            adapter.setDirectItems(DirectionsActivity.this, prevBtn, skipBtn, nextBtn, true, false);
        }
    }

    //For testing
    public DirectionsAdapter getAdapter(){ return adapter; }
}