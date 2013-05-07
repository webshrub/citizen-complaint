package com.webshrub.citizencomplaint.androidapp;

import android.content.Context;
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
public class CitizenComplaintPostDetailsInBackground extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private CitizenComplaintDataSource citizenComplaintDataSource;

    public CitizenComplaintPostDetailsInBackground(Context context) {
        this.context = context;
        citizenComplaintDataSource = new CitizenComplaintDataSource(context);
        citizenComplaintDataSource.open();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPreExecute() {
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean success = true;
        try {
            for (CitizenComplaint citizenComplaint : citizenComplaintDataSource.getAllCitizenComplaints()) {
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
                citizenComplaintDataSource.deleteCitizenComplaint(citizenComplaint);
            }
            citizenComplaintDataSource.close();
        } catch (Exception e) {
            Log.e("Photo Upload", "Photo Upload exception: " + e.getMessage());
            success = false;
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Toast.makeText(context, "All previously stored complaints uploaded successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Few previous complaints could not be uploaded. Will be uploaded next time.", Toast.LENGTH_SHORT).show();
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
