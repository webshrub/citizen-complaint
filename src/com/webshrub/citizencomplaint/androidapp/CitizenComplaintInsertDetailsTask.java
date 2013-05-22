package com.webshrub.citizencomplaint.androidapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.webshrub.citizencomplaint.androidapp.CitizenComplaintConstants.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ahsan.Javed
 * Date: 4/26/13
 * Time: 5:44 PM
 */
public class CitizenComplaintInsertDetailsTask extends AsyncTask<Void, Void, CitizenComplaintMLADetails> implements LocationListener {
    private Context context;
    private CitizenComplaint citizenComplaint;
    private CitizenComplaintDataSource citizenComplaintDataSource;
    private LocationManager locationManager;
    private Dialog progressDialog;

    public CitizenComplaintInsertDetailsTask(Context context, CitizenComplaint citizenComplaint) {
        this.context = context;
        this.citizenComplaint = citizenComplaint;
        citizenComplaintDataSource = CitizenComplaintDataSource.getInstance(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String bestProvider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(bestProvider, 99, 0, this);
        progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.citizen_complaint_progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPreExecute() {
        progressDialog.show();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected CitizenComplaintMLADetails doInBackground(Void... params) {
        while (citizenComplaint.getLatitude() == null || citizenComplaint.getLongitude() == null) {
            try {
                Thread.sleep(99);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        locationManager.removeUpdates(this);
        citizenComplaintDataSource.createCitizenComplaint(citizenComplaint);
        JSONObject jsonResponse = CitizenComplaintHttpUtil.getJSONFromUrl(CITIZEN_COMPLAINT_GET_MLA_ID_URL_PARAMS + "?" + LAT_PARAMS + "=" + citizenComplaint.getLatitude() + "&" + LONG_PARAMS + "=" + citizenComplaint.getLongitude());
        try {
            String constituencyId = jsonResponse.getString(CONSTITUENCY_ID_PARAMS);
            jsonResponse = CitizenComplaintHttpUtil.getJSONFromUrl(CITIZEN_COMPLAINT_GET_MLA_INFO_URL_PARAMS + "/" + constituencyId);
            JSONArray nodes = jsonResponse.getJSONArray(NODES_PARAMS);
            JSONObject node = nodes.getJSONObject(0).getJSONObject(NODE_PARAMS);
            String imageUrl = node.getString(IMAGE_PARAMS);
            String mlaName = node.getString(MLA_NAME_PARAMS);
            String mlaConstituency = node.getString(CONSTITUENCY_PARAMS);
            Bitmap mlaImage = CitizenComplaintHttpUtil.getBitmapFromUrl(imageUrl);
            return new CitizenComplaintMLADetails(mlaName, mlaConstituency, mlaImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(CitizenComplaintMLADetails citizenComplaintMLADetails) {
        super.onPostExecute(citizenComplaintMLADetails);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.citizen_complaint_summary_dialog, null);
        TextView mlaNameConstituency = (TextView) view.findViewById(R.id.mlaNameConstituencyTextView);
        mlaNameConstituency.setText(citizenComplaintMLADetails.getMlaName() + ", MLA/" + citizenComplaintMLADetails.getMlaConstituency());
        ImageView mlaImage = (ImageView) view.findViewById(R.id.mlaImageView);
        mlaImage.setImageBitmap(citizenComplaintMLADetails.getMlaImage());
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

    @Override
    public void onLocationChanged(Location location) {
        citizenComplaint.setComplaintAddress("India");
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
                String complaintAddress = String.format("%s, %s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getLocality(), address.getCountryName());
                citizenComplaint.setComplaintAddress(complaintAddress);
            }
        }
        //Important to have these two lines at the last because of thread synchronization.
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
