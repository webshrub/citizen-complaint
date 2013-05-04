package com.webshrub.citizencomplaint.androidapp;

import android.content.Context;
import android.location.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
        String addressText = "Latitude = " + location.getLatitude() + ", Longitude = " + location.getLongitude();
        if (CitizenComplaintUtility.isGeocoderPresent() && CitizenComplaintUtility.isDeviceOnline(context)) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                addressText = addressText + "\n" + String.format("%s, %s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getLocality(), address.getCountryName());
                citizenComplaint.setComplaintAddress(addressText);
            }
        }
        Toast.makeText(context, "Your location is: " + addressText, Toast.LENGTH_SHORT).show();
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
