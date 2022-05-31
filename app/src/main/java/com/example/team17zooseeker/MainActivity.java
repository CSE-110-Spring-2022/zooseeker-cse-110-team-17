package com.example.team17zooseeker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Ignore;
import androidx.room.Room;

import org.jgrapht.Graph;
import org.xml.sax.DTDHandler;

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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private final PermissionsChecker permissionsChecker = new PermissionsChecker(this);

    private DynamicDirections dynoDirections;

    private final String STATE_ITINERARY = "1";
    private final String STATE_DIRECTIONS = "2";

    private ZooKeeperDatabase database;
    private NodeItemDao nodeDao;
    private EdgeItemDao edgeDao;
    private StateDao stateDao;

    private Map<String, nodeItem> nodeMap;

    private ArrayList<String> visitationList;

    private SharedPreferences settingsPreferences;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private boolean gpsEnable;
    private Button settings;

    private List<nodeItem> addedNodesList = new ArrayList<nodeItem>();
    private NodeListAdapter adapter = new NodeListAdapter();


    private static boolean currentlyTesting = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        // finding settings button, and setting onClickListener accordingly to helper method
        settings = findViewById(R.id.settings_btn);
        settings.setOnClickListener(this::openSettings);

        database = ZooKeeperDatabase.getSingleton(this);

        nodeDao = database.nodeItemDao();
        edgeDao  = database.edgeItemDao();
        stateDao = database.stateDao();

        /////// /////// /////// /////// /////// /////// /////// /////// /////// /////// /////// ///////

        Map<String, nodeItem> nodeZ = null;
        Map<String, edgeItem> edgeZ = null;

        try {
            nodeZ = nodeItem.loadNodeInfoJSON(this, "node.json");
            edgeZ = edgeItem.loadEdgeInfoJSON(this, "edge.json");
            //state = State.loadStateInfoJSON(context, "state.json");

        } catch (IOException e) {
            e.printStackTrace();
        }
//
          List<nodeItem> nodeList = new ArrayList<nodeItem>(nodeZ.values());
//        List<edgeItem> edgeList = new ArrayList<edgeItem>(edgeZ.values());
//
          this.nodeMap = nodeList.stream().collect(Collectors.toMap(nodeItem::getName, Function.identity()));

//        nodeDao.insertAll(nodeList);
//        edgeDao.insertAll(edgeList);

        /////// /////// /////// /////// /////// /////// /////// /////// /////// /////// /////// ///////

        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // checks what the current value is in the shared preference

        if(settingsPreferences.getBoolean("gps_enable", true)){
            // if true, sets the gpsEnable to true
            gpsEnable = true;
        } else {
            // otherwise, false (meaning no GPS)
            gpsEnable = false;
        }
        Log.d("gpsEnable", Boolean.toString(gpsEnable));
        Log.d("gpsPreference", Boolean.toString(settingsPreferences.getBoolean("gps_enable",true)));

        //Permissions Setup
        {
            //If location permissions aren't granted then use default app functionality
            if (permissionsChecker.ensurePermissions()) return;
        }

        //Configure Location Listener and Setup Dynamic Directions
        dynoDirections = DynamicDirections.getSingleDyno(this,this);
        setupLocationListener(dynoDirections::updateUserLocationFromLocationListener);

        //TESTING
        //stateDao.delete(stateDao.get());
        //stateDao.insert(new State("0"));

        State state = stateDao.get();

        if(state == null) {
            stateDao.insert(new State("0"));
            state = stateDao.get();
        }

        // For Preserve Testing
        if(currentlyTesting) {
            state = new State("0");
        }
        else if(state == null) {
            stateDao.insert(new State("0"));
            state = stateDao.get();
        }

        List<edgeItem> edges = edgeDao.getAll();
        List<nodeItem> nodes = nodeDao.getAll();

        // For MainActivity
        preferences = getPreferences(MODE_PRIVATE);
        editor = preferences.edit();

        Set<String> vSet = preferences.getStringSet("visitationList", null);

        //Database stuff

        //Uncomment Later
        //this.nodeMap = nodes.stream().collect(Collectors.toMap(nodeItem::getName, Function.identity()));

        //Visitation List recycler
        adapter.setHasStableIds(true);

        RecyclerView visitationView = findViewById(R.id.visitation_list_view);
        visitationView.setLayoutManager(new LinearLayoutManager(this));
        visitationView.setAdapter(adapter);

        //Adding an exhibit to visitation list
        EditText searchText = findViewById(R.id.search_text);
        TextView exhibitText = findViewById(R.id.exhibit_count_txt);

        if(vSet != null) {

            visitationList = new ArrayList(vSet);

            for(String node : vSet)
                addedNodesList.add(nodeMap.get(node));

            adapter.setNodeItems(addedNodesList);

            exhibitText.setText("( " + visitationList.size() + " )");

        }

        //If no persisted vList data, there is no vList to extract from memory
        else {
            this.visitationList = new ArrayList<String>();
        }

        //Check to see if user closed app on non-main activity
        handleNMState(state);

        //Search Bar Configuration
        {
            //If not, proceed using default main activity functionality
            searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    int result = actionId & EditorInfo.IME_MASK_ACTION;

                    if (result == EditorInfo.IME_ACTION_DONE) {
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
        }

        Button plan = findViewById(R.id.plan_btn);
        plan.setOnClickListener(this::onPlanClicked);

        Button clear = findViewById(R.id.clear_btn);
        clear.setOnClickListener(this::onClearClicked);

        //Setting up the autocomplete text field with custom adapter
        AutoCompleteTextView searchTextView = (AutoCompleteTextView)findViewById(R.id.search_text);
        ArrayAdapter<String> autoCompleteAdapter = new AutoCompleteAdapter(this);
        searchTextView.setAdapter(autoCompleteAdapter);
    }

    @SuppressLint("MissingPermission")
    private void setupLocationListener(Consumer<Pair<Double, Double>> handleUserLocationUpdate) {
        String provider = LocationManager.GPS_PROVIDER;
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener(){
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d("Position From Listener", location.getLatitude() + ", " + location.getLongitude());
                Pair<Double, Double> updatedLocation = Pair.create(
                        location.getLatitude(),
                        location.getLongitude()
                );
                handleUserLocationUpdate.accept(updatedLocation);
            }
        };

        //Waits five seconds to check location
        locationManager.requestLocationUpdates(provider,10000,0f, locationListener);
    }

    private void handleNMState(State state) {

        if(state.state.equals(STATE_ITINERARY)) {

            for(int i = 0; i < this.visitationList.size(); i++){
                this.visitationList.set(i, this.nodeMap.get(this.visitationList.get(i)).getId());
            }

            this.visitationList.clear();
            editor.putStringSet("visitationList", null);
            editor.apply();

            Intent intent = new Intent(this, ItineraryActivity.class);

            startActivity(intent);
            finish();

        }

        else if(state.state.equals(STATE_DIRECTIONS)) {

            Intent intent = new Intent(this, DirectionsActivity.class);

            startActivity(intent);
            finish();

        }
    }

    void onClearClicked(View view) {
        TextView exhibitText = findViewById(R.id.exhibit_count_txt);

        visitationList.clear();
        addedNodesList.clear();

        editor.putStringSet("visitationList", new HashSet(visitationList));
        editor.apply();

        adapter.setNodeItems(addedNodesList);
        exhibitText.setText("( " + visitationList.size() + " )");
    }

    void onPlanClicked (View view){
        Log.d("Visitation List: ", this.visitationList.toString());
        if(visitationList.size()==0){
            Utilities.showAlert(this, "Please add Exhibits to your Visitation Plan :D");
            return;
        }
        //Visitation List needs to be in Ids and not names
        for(int i = 0; i < this.visitationList.size(); i++){
            this.visitationList.set(i, this.nodeMap.get(this.visitationList.get(i)).getId());
        }

        Intent intent = new Intent(this, ItineraryActivity.class);
        intent.putStringArrayListExtra("VList", this.visitationList);

        Itinerary.createItinerary(this, new ArrayList<>(visitationList));

        editor.putStringSet("visitationList", null);
        editor.apply();

        //this.visitationList.clear();


        startActivity(intent);
        finish();
    }

    // method for opening Settings Activity
    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    // For Preserve Testing
    @VisibleForTesting
    public static void setTesting(boolean s) {
        currentlyTesting = s;
    }

}
