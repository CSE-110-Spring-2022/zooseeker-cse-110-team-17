package com.example.team17zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;

public class DirectionsActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private Button nextBtn;
    private Button skipBtn;

    private DirectionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        Directions d = new Directions(Itinerary.getItinerary(),0);
        adapter = new DirectionsAdapter(d);

        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.directions_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        skipBtn = findViewById(R.id.skip_btn);
        skipBtn.setOnClickListener(this::onSkipClicked);

        nextBtn = findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(this::onNextClicked);
        adapter.setDirectItems(DirectionsActivity.this, skipBtn, nextBtn, false);
    }

    public void onSkipClicked (View view) {
        adapter.setDirectItems(DirectionsActivity.this, skipBtn, nextBtn, true);
    }

    public void onNextClicked (View view){
        if(nextBtn.getText().equals("FINISH")){
            Itinerary.deleteItinerary();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            adapter.setDirectItems(DirectionsActivity.this, skipBtn, nextBtn, false);
        }
    }

    //For testing
    public DirectionsAdapter getAdapter(){ return adapter; }
}