package com.webshrub.citizencomplaint.androidapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Ahsan.Javed
 * Date: 4/26/13
 * Time: 5:44 PM
 */
public class CitizenComplaintPostDetailsAsyncTask extends AsyncTask<Void, Void, Boolean> implements LocationListener {
    private Context context;
    private CitizenComplaint citizenComplaint;
    private ProgressDialog progressDialog;
    private LocationManager locationManager;

    public CitizenComplaintPostDetailsAsyncTask(Context context, CitizenComplaint citizenComplaint) {
        this.context = context;
        this.citizenComplaint = citizenComplaint;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading your complaint details..");
        progressDialog.setTitle("Please wait");
        progressDialog.setIndeterminate(true);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String bestProvider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(bestProvider, 99, 0, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean success = true;
        try {
            HttpParams httpParams = new BasicHttpParams();
            httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpPost httppost = new HttpPost(CitizenComplaintConstants.CITIZEN_COMPLAINT_HOST);
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntity.addPart(CitizenComplaintConstants.LATTITUDE_PARAMS, new StringBody("" + citizenComplaint.getLatitude()));
            multipartEntity.addPart(CitizenComplaintConstants.LONGITUDE_PARAMS, new StringBody("" + citizenComplaint.getLongitude()));
            multipartEntity.addPart(CitizenComplaintConstants.ISSUE_TYPE_PARAMS, new StringBody("" + citizenComplaint.getComplaintId()));
            multipartEntity.addPart(CitizenComplaintConstants.TEMPLATE_ID_PARAMS, new StringBody("" + citizenComplaint.getSelectedTemplateId()));
            multipartEntity.addPart(CitizenComplaintConstants.TEMPLATE_TEXT_PARAMS, new StringBody("" + citizenComplaint.getSelectedTemplateString()));
            multipartEntity.addPart(CitizenComplaintConstants.REPORTER_ID_PARAMS, new StringBody("" + CitizenComplaintConstants.REPORTER_ID_VALUE));
            if (citizenComplaint.getSelectedComplaintImageUri() != null && !citizenComplaint.getSelectedComplaintImageUri().equals("")) {
                multipartEntity.addPart(CitizenComplaintConstants.IMAGE_URI_PARAMS, new FileBody(new File(getAbsoluteFilePath(citizenComplaint.getSelectedComplaintImageUri()))));
            }
            httppost.setEntity(multipartEntity);
            httpClient.execute(httppost, new PhotoUploadResponseHandler());
        } catch (Exception e) {
            Log.e("Photo Upload", "Photo Upload exception: " + e.getMessage());
            success = false;
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (result) {
            Toast.makeText(context, "Complaint uploaded successfully.", Toast.LENGTH_SHORT).show();
            Intent newIntent = new Intent(context, CitizenComplaintHomeActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        } else {
            Toast.makeText(context, "Complaint could not be uploaded. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressWarnings("deprecation")
    private String getAbsoluteFilePath(String selectedComplaintImageUri) {
        if (selectedComplaintImageUri.startsWith("content://")) {
            Uri uri = Uri.parse(selectedComplaintImageUri);
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = ((Activity) context).managedQuery(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else {
            Uri uri = Uri.parse(selectedComplaintImageUri);
            return uri.getSchemeSpecificPart();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        while (citizenComplaint.getLatitude() == null || citizenComplaint.getLongitude() == null) {
            citizenComplaint.setLatitude(Double.toString(location.getLatitude()));
            citizenComplaint.setLongitude(Double.toString(location.getLongitude()));
        }
        Toast.makeText(context, "Your location is: Latitude = " + citizenComplaint.getLatitude() + ", Longitude = " + citizenComplaint.getLongitude(), Toast.LENGTH_SHORT).show();
        locationManager.removeUpdates(this);
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

    private class PhotoUploadResponseHandler implements ResponseHandler {
        @Override
        public Object handleResponse(HttpResponse response) throws IOException {
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            Log.d("UPLOAD", responseString);
            return null;
        }
    }
}
