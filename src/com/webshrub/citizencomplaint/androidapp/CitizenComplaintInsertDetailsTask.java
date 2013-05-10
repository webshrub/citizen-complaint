package com.webshrub.citizencomplaint.androidapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: Ahsan.Javed
 * Date: 4/26/13
 * Time: 5:44 PM
 */
public class CitizenComplaintInsertDetailsTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private LinearLayout progressBarLayout;
    private CitizenComplaint citizenComplaint;
    private CitizenComplaintDataSource citizenComplaintDataSource;

    public CitizenComplaintInsertDetailsTask(Context context, LinearLayout progressBarLayout, CitizenComplaint citizenComplaint) {
        this.context = context;
        this.progressBarLayout = progressBarLayout;
        this.citizenComplaint = citizenComplaint;
        citizenComplaintDataSource = CitizenComplaintDataSource.getInstance(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPreExecute() {
        try {
            progressBarLayout.setVisibility(View.VISIBLE);
            new CitizenComplaintGeoLocationAsyncTask(context, citizenComplaint).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Void doInBackground(Void... params) {
        citizenComplaintDataSource.createCitizenComplaint(citizenComplaint);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        progressBarLayout.setVisibility(View.GONE);
        String addressText = "Latitude = " + citizenComplaint.getLatitude() + ", Longitude = " + citizenComplaint.getLongitude();
        if (citizenComplaint.getComplaintAddress() != null && !citizenComplaint.getComplaintAddress().equals("")) {
            addressText = addressText + "\n" + citizenComplaint.getComplaintAddress();
        }
        Toast.makeText(context, "Your location is: " + addressText, Toast.LENGTH_SHORT).show();
        Intent newIntent = new Intent(context, CitizenComplaintHomeActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
        Toast.makeText(context, "Complaint submitted successfully.", Toast.LENGTH_SHORT).show();
    }
}
