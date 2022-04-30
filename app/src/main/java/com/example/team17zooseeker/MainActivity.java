package com.example.team17zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this code will create a directions object with its two points being
        // start = entrance_exit_gate
        // end = elephant_odyssey
        Directions d = new Directions("entrance_exit_gate", "elephant_odyssey");
        // this code will create the directions between the start and end
        d.createDirections(this);
        // this code will hold the list of directions to be iterated through
        List<String> dirs = d.getDirs();
    }
}