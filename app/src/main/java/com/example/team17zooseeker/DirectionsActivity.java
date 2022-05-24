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

import java.util.Arrays;
import java.util.List;

public class DirectionsActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private Button nextBtn;

    private DirectionsAdapter adapter;

    private ZooKeeperDatabase database;
    private StateDao stateDao;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        // For Directions Activity
        database = ZooKeeperDatabase.getSingleton(this);
        stateDao = database.stateDao();

        stateDao.delete(stateDao.get());
        stateDao.insert(new State("2"));

        Directions d = new Directions(Itinerary.getItinerary(),0);
        adapter = new DirectionsAdapter(d);

        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.directions_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        nextBtn = findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(this::onNextClicked);
        adapter.setDirectItems(DirectionsActivity.this, nextBtn);
    }

    public void onNextClicked (View view){
        if(nextBtn.getText().equals("FINISH")){
            Itinerary.deleteItinerary();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            adapter.setDirectItems(DirectionsActivity.this, nextBtn);
        }
    }

    //For testing
    public DirectionsAdapter getAdapter(){ return adapter; }
}