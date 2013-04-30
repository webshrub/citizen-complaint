package com.webshrub.citizencomplaint.androidapp;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: Ahsan.Javed
 * Date: 4/29/13
 * Time: 8:20 PM
 */
public class CitizenComplaintGeoLocationAsyncTask extends AsyncTask<Void, Void, CitizenComplaint> implements LocationListener {
    private Context context;
    private CitizenComplaint citizenComplaint;
    private LocationManager locationManager;

    public CitizenComplaintGeoLocationAsyncTask(Context context, CitizenComplaint citizenComplaint) {
        this.context = context;
        this.citizenComplaint = citizenComplaint;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String bestProvider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(bestProvider, 99, 0, this);
    }

    @Override
    protected CitizenComplaint doInBackground(Void... params) {
        while (citizenComplaint.getLatitude() == null || citizenComplaint.getLongitude() == null) {
            try {
                Thread.sleep(99);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        locationManager.removeUpdates(this);
        return citizenComplaint;
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(context, "Your location is: Latitude = " + location.getLatitude() + ", Longitude = " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        citizenComplaint.setLatitude(Double.toString(location.getLatitude()));
        citizenComplaint.setLongitude(Double.toString(location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
}
