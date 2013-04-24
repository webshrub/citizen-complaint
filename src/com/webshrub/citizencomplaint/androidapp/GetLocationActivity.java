package com.webshrub.citizencomplaint.androidapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;

public class GetLocationActivity extends SherlockActivity {
    private MyLocationListener mlocListener;
    private LocationManager mlocManager;
    protected static final int PROGRESS_DIALOG = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                mlocListener = new MyLocationListener(GetLocationActivity.this);
                mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                String bestProvider = mlocManager.getBestProvider(criteria, true);
                mlocManager.requestLocationUpdates(bestProvider, 99, 0, mlocListener);
                showDialog(PROGRESS_DIALOG);
            }
        });
        findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(GetLocationActivity.this, PhotoCaptureActivity.class);
                newIntent.putExtras(GetLocationActivity.this.getIntent());
                GetLocationActivity.this.startActivity(newIntent);
            }
        });
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        if (id == PROGRESS_DIALOG) {
            return ProgressDialog.show(this, "Getting Location", "", true, true, new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
        }
        return super.onCreateDialog(id);
    }

    public class MyLocationListener implements LocationListener {
        private GetLocationActivity getLocationActivity;

        public MyLocationListener(GetLocationActivity getLocationActivity) {
            this.getLocationActivity = getLocationActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            if (getLocationActivity.isFinishing()) {
                return;
            }
            mlocManager.removeUpdates(mlocListener);
            loc.getLatitude();
            loc.getLongitude();
            String Text = "My current location is: " + "Latitud = " + loc.getLatitude() + "Longitud = " + loc.getLongitude();
            Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT).show();
            Intent newIntent = new Intent(getLocationActivity, PhotoCaptureActivity.class);
            newIntent.putExtras(getLocationActivity.getIntent());
            newIntent.putExtra(IntentExtraConstants.LATITUDE, loc.getLatitude());
            newIntent.putExtra(IntentExtraConstants.LONGITUDE, loc.getLongitude());
            getLocationActivity.startActivity(newIntent);
            dismissDialog(PROGRESS_DIALOG);
        }

        @Override
        public void onProviderDisabled(String arg0) {
        }

        @Override
        public void onProviderEnabled(String arg0) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
