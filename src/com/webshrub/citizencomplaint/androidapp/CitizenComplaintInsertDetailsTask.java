package com.webshrub.citizencomplaint.androidapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: Ahsan.Javed
 * Date: 4/26/13
 * Time: 5:44 PM
 */
public class CitizenComplaintInsertDetailsTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private CitizenComplaint citizenComplaint;
    private CitizenComplaintDataSource citizenComplaintDataSource;

    public CitizenComplaintInsertDetailsTask(Context context, CitizenComplaint citizenComplaint) {
        this.context = context;
        this.citizenComplaint = citizenComplaint;
        citizenComplaintDataSource = CitizenComplaintDataSource.getInstance(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPreExecute() {
        try {
            Toast.makeText(context, "Submitting your complaint.", Toast.LENGTH_SHORT).show();
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
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(context, "Complaint submitted successfully.", Toast.LENGTH_SHORT).show();
        Intent newIntent = new Intent(context, CitizenComplaintHomeActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }
}
