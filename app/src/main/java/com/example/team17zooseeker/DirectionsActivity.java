package com.example.team17zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.strictmode.DiskReadViolation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private TextView fromTxt;
    private TextView toTxt;

    private DirectionsAdapter adapter;

    private ZooKeeperDatabase database;
    private StateDao stateDao;

    private SharedPreferences preferences;
    private SharedPreferences directionsPreferences;
    private SharedPreferences.Editor editor;

    public static boolean theLastButtonPressedWasPrevious = false;

    private boolean directionType;

    ArrayList<String> VList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        //Allows for user prompts on this page
        DynamicDirections.setCurrActivity(this);
        DynamicDirections.setDynamicEnabled(true);

        // For Directions Activity
        database = ZooKeeperDatabase.getSingleton(this);
        stateDao = database.stateDao();

        stateDao.delete(stateDao.get());
        stateDao.insert(new State("2"));

        // gets shared preferences from the preferences we made with the fragment
        directionsPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences = getPreferences(MODE_PRIVATE);
        editor = preferences.edit();

        // checks what the current value is in the shared preference
        if(directionsPreferences.getBoolean("direction_type", true)){
            // if true, sets the directionType to true
            directionType = true;
        } else {
            // otherwise, false (meaning simple directions)
            directionType = false;
        }

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

        fromTxt = findViewById(R.id.from_text);
        toTxt = findViewById(R.id.to_text);

        skipBtn = findViewById(R.id.skip_btn);
        skipBtn.setOnClickListener(this::onSkipClicked);

        prevBtn = findViewById(R.id.prev_btn);
        prevBtn.setOnClickListener(this::onPrevClicked);

        nextBtn = findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(this::onNextClicked);

        Directions d = new Directions(Itinerary.getItinerary(), index);
        adapter = new DirectionsAdapter(d, prevBtn, skipBtn, nextBtn);
        d.setDetailedDirections(directionType);

        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.directions_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setDirectItems(DirectionsActivity.this, true, false);
    }

    public void onPrevClicked (View view) {
        if(theLastButtonPressedWasPrevious){
            //Now the person has moved so decrease their position on directions
            Directions.decreaseCurrentPosition();

            //Set Save info
            editor.putInt("ItinIndex", Directions.getCurrentIndex());
            editor.apply();
        }
        theLastButtonPressedWasPrevious = true;
        adapter.setDirectItems(DirectionsActivity.this, false, false);

        Log.d("Current Position", Itinerary.getItinerary().get(Directions.getCurrentIndex()));
    }

    public void onSkipClicked (View view) {
        adapter.setDirectItems(DirectionsActivity.this, true, true);
        //Position stays the same because we just skipped the next thing
        editor.putStringSet("VList", new HashSet(Itinerary.getItinerary()));
        editor.apply();
        Log.d("Current Position", Itinerary.getItinerary().get(Directions.getCurrentIndex()));
    }

    public void onNextClicked (View view){
        if(nextBtn.getText().equals("FINISH")){
            Itinerary.deleteItinerary();
            Itinerary.setItineraryCreated(false);
            //Setting current index position
            Directions.resetCurrentIndex();

            stateDao.delete(stateDao.get());
            stateDao.insert(new State("0"));

            editor.putStringSet("VList", null);
            editor.putInt("ItinIndex", 0);
            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            if(theLastButtonPressedWasPrevious){
                //Now the person has moved so decrease their position on directions
                Directions.decreaseCurrentPosition();
            }else{
                //Now the person has moved so increase their position on directions
                Directions.increaseCurrentPosition();
            }

            //Set Save info
            editor.putInt("ItinIndex", Directions.getCurrentIndex());
            editor.apply();

            adapter.setDirectItems(DirectionsActivity.this, true, false);
            Log.d("Current Position", Itinerary.getItinerary().get(Directions.getCurrentIndex()));
        }
        theLastButtonPressedWasPrevious = false;
    }

    public DirectionsAdapter getAdapter(){ return adapter; }
}