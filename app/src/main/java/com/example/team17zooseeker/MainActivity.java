package com.example.team17zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Directions
        List<String> itinerary = Arrays.asList("entrance_exit_gate", "elephant_odyssey",
                "arctic_foxes", "entrance_exit_gate");
        Directions d = new Directions(itinerary, 0);
        d.createDirections(this);
        d.createDirections(this);
        List<String> dirs = d.createDirections(this);
    }
}