package com.webshrub.citizencomplaint.androidapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.sbstrm.appirater.Appirater;

public class CitizenComplaintActivity extends SherlockFragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appirater.appLaunched(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu_citizen_complaint, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_rate_us:
                String marketUrl = "market://details?id=com.webshrub.citizencomplaint.androidapp";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl)));
                return true;
            case R.id.menu_settings:
                Intent preferenceIntent = new Intent(this, CitizenComplaintPreferenceActivity.class);
                preferenceIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(preferenceIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
