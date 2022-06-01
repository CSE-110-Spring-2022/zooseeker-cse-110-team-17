package com.example.team17zooseeker;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
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
    private SharedPreferences settingsPreferences;
    private SharedPreferences.Editor editor;

    private boolean directionType;

    private Button mocker;
    private Button update;

    ArrayList<String> VList;

    private static boolean currentlyTesting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        // Allows for user prompts on this page
        DynamicDirections.setCurrActivity(this);
        DynamicDirections.setDynamicEnabled(true);

        // find Mocker button
        mocker = findViewById(R.id.mocking_btn);
        mocker.setOnClickListener(this::openMocker);

        // find update button
        update = findViewById(R.id.update_btn);
        update.setOnClickListener(this::onUpdateClicked);

        // For Directions Activity
        database = ZooKeeperDatabase.getSingleton(this);
        stateDao = database.stateDao();

        // For Preserve Testing
        if(!currentlyTesting) {
            stateDao.delete(stateDao.get());
            stateDao.insert(new State("2"));
        }
        else if(stateDao == null) {
            stateDao.insert(new State("2"));
        }

        // gets shared preferences from the preferences we made with the fragment
        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences = getPreferences(MODE_PRIVATE);
        editor = preferences.edit();

        // checks what the current value is in the shared preference
        if(settingsPreferences.getBoolean("direction_type", true)){
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

        Directions d = new Directions(Itinerary.getItinerary(), DynamicDirections.getSingleDyno(this,this), index);
        adapter = new DirectionsAdapter(d, prevBtn, skipBtn, nextBtn);
        d.setDetailedDirections(directionType);

        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.directions_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setDirectItems(this, false);
    }

    // updates mocked location
    public void onUpdateClicked (View view) {
        setMock();
    }

    public void onPrevClicked (View view) {
        Directions.decreaseCurrentPosition();

        //Save info
        editor.putInt("ItinIndex", Directions.getCurrentIndex());
        editor.apply();

        //Update UI
        adapter.setDirectItems(this, false);
    }

    public void onSkipClicked (View view) {
        adapter.setDirectItems(this, true);
        //Position stays the same because we just skipped the next thing
        editor.putStringSet("VList", new HashSet(Itinerary.getItinerary()));
        editor.apply();
    }

    public void onNextClicked (View view){
        if(nextBtn.getText().equals("FINISH")){
            Itinerary.deleteItinerary();
            Itinerary.setItineraryCreated(false);
            //Resetting current index position
            Directions.resetCurrentIndex();

            stateDao.delete(stateDao.get());
            stateDao.insert(new State("0"));

            editor.putStringSet("VList", null);
            editor.putInt("ItinIndex", 0);
            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Directions.increaseCurrentPosition();
            //Save info
            editor.putInt("ItinIndex", Directions.getCurrentIndex());
            editor.apply();

            //Update UI
            adapter.setDirectItems(this, false);
        }
    }

    // For Preserve Testing
    @VisibleForTesting
    public static void setTesting(boolean s) {
        currentlyTesting = s;
    }

    public DirectionsAdapter getAdapter(){ return adapter; }

    // sets mocking capabilities if used during demo
    public void setMock(){
        double lat,lng;
        // extract lat and long from shared preferences
        lat = Double.parseDouble(settingsPreferences.getString("mock_lat", "32.73459618734685"));
        lng = Double.parseDouble(settingsPreferences.getString("mock_lng", "-117.14936"));

        // if mocking is enabled, then we set update location to mocked location
        if (settingsPreferences.getBoolean("mock_enable", true)) {
            DynamicDirections.setLocationCurrentlyMocked(true);
            DynamicDirections.getSingleDyno(this,this).updateUserLocation(new Pair<Double, Double>(lat,lng));
        } else {
            // otherwise proceed as normal
            DynamicDirections.setLocationCurrentlyMocked(false);
        }
        //adapter.itineraryUpdated();

        // logging because difficult
        Log.d("mock enable", Boolean.toString(settingsPreferences.getBoolean("mock_enable", true)));
        Log.d("mock lat", Double.toString(lat));
        Log.d("mock lng", Double.toString(lng));

    }

    // method for opening Mocker Activity
    public void openMocker(View view) {
        Intent intent = new Intent(this, MockingActivity.class);
        startActivity(intent);
    }


}