package com.example.team17zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItineraryActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    private Button Get_direction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);
        ItineraryItemAdapter adaptor = new ItineraryItemAdapter();
        adaptor.setHasStableIds(true);

        recyclerView = findViewById(R.id.ItItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptor);


        this.Get_direction = this.findViewById(R.id.get_direction);
        Get_direction.setOnClickListener(this::onGetDirectionClicked);

    }

    void onGetDirectionClicked (View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}



