package com.example.team17zooseeker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class MockingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mocking);

        // below line is used to check if
        // frame layout is empty or not.
        if (findViewById(R.id.mocking_fragment_container_view) != null) {
            if (savedInstanceState != null) {
                return;
            }
            // below line is to inflate our fragment.
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mocking_fragment_container_view, new MockingFragment())
                    .commit();
        }
    }
}
