package com.example.team17zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;

public class DirectionsActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private int i = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
        List<String> itinerary = Arrays.asList("entrance_exit_gate", "elephant_odyssey", "arctic_foxes");
        Directions d = new Directions(itinerary,0);
        final DirectionsAdapter adapter = new DirectionsAdapter(d);

        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.directions_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        Context context = this;
        Button next = findViewById(R.id.next_btn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setDirectItems(DirectionsActivity.this,next);
            }
        });
        adapter.setDirectItems(DirectionsActivity.this,next);

    }


}