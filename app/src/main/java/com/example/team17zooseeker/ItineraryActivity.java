package com.example.team17zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ItineraryActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    private Button Get_direction;

    private ZooKeeperDatabase database;
    private StateDao stateDao;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private ArrayList<String> VList;
    private Set<String> VSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        //Allows for user prompts on this page
        //DynamicDirections.setCurrActivity(this);
        //Forcing that we cannot update itinerary from this activity. (pathApproved)
        DynamicDirections.setDynamicEnabled(false);

        database = ZooKeeperDatabase.getSingleton(this);
        stateDao = database.stateDao();

        stateDao.delete(stateDao.get());
        stateDao.insert(new State("1"));

        // For Itinerary Activity
        preferences = getPreferences(MODE_PRIVATE);
        editor = preferences.edit();

        Bundle extras = getIntent().getExtras();

        VList = null;

        //Store Planned VisitationList if New One Supplied
        if(extras != null) {

            VList = extras.getStringArrayList("VList");
            editor.putStringSet("VList", new HashSet(VList));
            editor.apply();

        }

        VSet = preferences.getStringSet("VList", null);

        if (VSet != null) {

            VList = new ArrayList(VSet);

            Itinerary.createItinerary(this, VList);

            editor.putStringSet("VList", new HashSet(VList));
            editor.apply();

        }

        //Setting the adapter of the recyclerView
        ItineraryItemAdapter adapter = new ItineraryItemAdapter();
        adapter.setHasStableIds(true);
        recyclerView = findViewById(R.id.itinerary_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Setting the directions button event
        this.Get_direction = this.findViewById(R.id.get_direction);
        Get_direction.setOnClickListener(this::onGetDirectionClicked);
    }

    void onGetDirectionClicked (View view){
        Intent intent = new Intent(this, DirectionsActivity.class);
        intent.putExtra("VList", new ArrayList(VSet));
        startActivity(intent);

        editor.putStringSet("VList", null);
        editor.apply();

        finish();
    }
}



