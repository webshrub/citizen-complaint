package com.webshrub.citizencomplaint.androidapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

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
        StringBuffer buffer = new StringBuffer("");
        buffer.append("Category = ").append(citizenComplaint.getComplaintCategory()).append("\n");
        buffer.append("Complaint = ").append(citizenComplaint.getSelectedTemplateString()).append("\n");
        buffer.append("Latitude = ").append(citizenComplaint.getLatitude()).append("\n");
        buffer.append("Longitude = ").append(citizenComplaint.getLongitude()).append("\n");
        if (citizenComplaint.getComplaintAddress() != null && !citizenComplaint.getComplaintAddress().equals("")) {
            buffer.append("Address = ").append(citizenComplaint.getComplaintAddress()).append("\n");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(buffer)
                .setCancelable(false)
                .setTitle("Complaint Successful")
                .setPositiveButton("Another Complaint", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent newIntent = new Intent(context, CitizenComplaintHomeActivity.class);
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(newIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
