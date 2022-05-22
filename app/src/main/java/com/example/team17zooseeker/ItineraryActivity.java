package com.example.team17zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ItineraryActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    private Button Get_direction;

    private ZooKeeperDatabase database;
    private StateDao stateDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        database = ZooKeeperDatabase.getSingleton(this);
        stateDao = database.stateDao();

        stateDao.delete(stateDao.get());
        stateDao.insert(new State("1"));

        // For Itinerary Activity
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("state", "1");
        editor.apply();

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
        startActivity(intent);
        finish();
    }
}



