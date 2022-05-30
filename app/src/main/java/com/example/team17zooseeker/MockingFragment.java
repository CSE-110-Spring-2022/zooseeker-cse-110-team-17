package com.example.team17zooseeker;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class MockingFragment extends PreferenceFragmentCompat{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.mocking_pref);
    }
}
