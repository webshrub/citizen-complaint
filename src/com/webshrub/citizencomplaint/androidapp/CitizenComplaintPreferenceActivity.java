package com.webshrub.citizencomplaint.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockActivity;

public class CitizenComplaintPreferenceActivity extends SherlockActivity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citizen_complaint_preference_activity);
        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        switch (position) {
            case 0:
                Intent helpIntent = new Intent(CitizenComplaintPreferenceActivity.this, CitizenComplaintHelpActivity.class);
                startActivity(helpIntent);
                break;
            case 1:
                Intent aboutIntent = new Intent(CitizenComplaintPreferenceActivity.this, CitizenComplaintAboutActivity.class);
                startActivity(aboutIntent);
                break;
            case 2:
                Intent profileIntent = new Intent(CitizenComplaintPreferenceActivity.this, CitizenComplaintProfileActivity.class);
                startActivity(profileIntent);
                break;
        }
    }
}
