package com.webshrub.citizencomplaint.androidapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
public class CitizenComplaintPostDetailsAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private CitizenComplaint citizenComplaint;
    private ProgressDialog progressDialog;

    public CitizenComplaintPostDetailsAsyncTask(Context context, CitizenComplaint citizenComplaint) {
        this.context = context;
        this.citizenComplaint = citizenComplaint;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading your complaint details..");
        progressDialog.setTitle("Please wait");
        progressDialog.setIndeterminate(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPreExecute() {
        progressDialog.show();
        try {
            new CitizenComplaintGeoLocationAsyncTask(context, citizenComplaint).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            multipartEntity.addPart(CitizenComplaintConstants.REPORTER_ID_PARAMS, new StringBody("" + CitizenComplaintUtility.getDeviceIdentifier(context)));
            multipartEntity.addPart(CitizenComplaintConstants.ADDRESS_PARAMS, new StringBody("" + citizenComplaint.getComplaintAddress()));
            if (citizenComplaint.getSelectedComplaintImageUri() != null && !citizenComplaint.getSelectedComplaintImageUri().equals("")) {
                multipartEntity.addPart(CitizenComplaintConstants.IMAGE_URI_PARAMS, new FileBody(new File(citizenComplaint.getSelectedComplaintImageUri())));
            }
            if (citizenComplaint.getProfileThumbnailImageUri() != null && !citizenComplaint.getProfileThumbnailImageUri().equals("")) {
                multipartEntity.addPart(CitizenComplaintConstants.PROFILE_IMAGE_URI_PARAMS, new FileBody(new File(citizenComplaint.getProfileThumbnailImageUri())));
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
