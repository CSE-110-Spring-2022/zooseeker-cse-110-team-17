package com.example.team17zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private Button plan_btn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] VisitationList = {"entrance_plaza", "gators", "lions", "lions","gorillas", "gorillas","gorillas","gorillas","gorillas","elephant_odyssey", "arctic_foxes"};
        ArrayList<String> VisList = new ArrayList<String>(Arrays.asList(VisitationList));
        Itinerary.createItinerary(this, VisList);

        this.plan_btn = this.findViewById(R.id.plan_btn);
        plan_btn.setOnClickListener(this::onPlanClicked);
    }

    void onPlanClicked (View view){
        Intent intent = new Intent(this, ItineraryActivity.class);
        startActivity(intent);
    }
}