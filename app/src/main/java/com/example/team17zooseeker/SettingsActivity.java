package com.example.team17zooseeker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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

    @Override
    public void finish(){
        SharedPreferences settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Enable or disable gps location tracking
        if(settingsPreferences.getBoolean("gps_enable", false)){
            DynamicDirections.setLocationCurrentlyMocked(false);
            Log.d("GPS_State", "On");
        } else {
            DynamicDirections.setLocationCurrentlyMocked(true);
            //Update to Entrance Exit gate to force this case
            DynamicDirections.getSingleDyno(this, this).updateUserLocation(new Pair<Double, Double>(32.73459618734685,-117.14936));
            Log.d("GPS_State", "Off");
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
