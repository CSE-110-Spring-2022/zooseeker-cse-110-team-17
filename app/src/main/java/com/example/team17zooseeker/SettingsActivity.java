package com.example.team17zooseeker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // below line is to change
        // the title of our action bar.
        getSupportActionBar().setTitle("Settings");

        // below line is used to check if
        // frame layout is empty or not.
        if (findViewById(R.id.fragment_container_view) != null) {
            if (savedInstanceState != null) {
                return;
            }
            // below line is to inflate our fragment.
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container_view, new SettingsFragment())
                    .commit();
        }

        // for logging values on change
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        boolean type = sharedPreferences.getBoolean("direction_type", true);
        Log.d("Direction Type", Boolean.toString(type));
    }

}
