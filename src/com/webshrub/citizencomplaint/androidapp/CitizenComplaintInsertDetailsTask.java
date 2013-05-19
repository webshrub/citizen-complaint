package com.webshrub.citizencomplaint.androidapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

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
    private Dialog progressDialog;

    public CitizenComplaintInsertDetailsTask(Context context, CitizenComplaint citizenComplaint) {
        this.context = context;
        this.citizenComplaint = citizenComplaint;
        citizenComplaintDataSource = CitizenComplaintDataSource.getInstance(context);
        progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.citizen_complaint_progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPreExecute() {
        try {
            progressDialog.show();
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
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.citizen_complaint_summary_dialog, null);
        TextView complaintCategory = (TextView) view.findViewById(R.id.complaintCategoryTextView);
        complaintCategory.setText(citizenComplaint.getComplaintCategory());
        TextView issueLevel = (TextView) view.findViewById(R.id.issueLevelTextView);
        Integer templateId = Integer.parseInt(citizenComplaint.getSelectedTemplateId());
        if (templateId >= 0 && templateId <= 9) {
            issueLevel.setText(context.getResources().getString(R.string.lackOfInfrastructure));
        } else if (templateId >= 10 && templateId <= 19) {
            issueLevel.setText(context.getResources().getString(R.string.lackOfMaintenance));
        } else if (templateId >= 20 && templateId <= 29) {
            issueLevel.setText(context.getResources().getString(R.string.lackOfQualityStaff));
        } else if (templateId >= 30 && templateId <= 39) {
            issueLevel.setText(context.getResources().getString(R.string.exorbitantPricing));
        } else if (templateId >= 40 && templateId <= 49) {
            issueLevel.setText(context.getResources().getString(R.string.lackOfAwareness));
        }
        TextView templateString = (TextView) view.findViewById(R.id.templateStringTextView);
        templateString.setText(citizenComplaint.getSelectedTemplateString());
        TextView latLong = (TextView) view.findViewById(R.id.latLongTextView);
        latLong.setText(citizenComplaint.getLatitude() + "/" + citizenComplaint.getLongitude());
        TextView complaintAddress = (TextView) view.findViewById(R.id.complaintAddressTextView);
        complaintAddress.setText(citizenComplaint.getComplaintAddress());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view)
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
