package com.webshrub.citizencomplaint.androidapp;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class CitizenComplaintPreferenceActivity extends SherlockPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
