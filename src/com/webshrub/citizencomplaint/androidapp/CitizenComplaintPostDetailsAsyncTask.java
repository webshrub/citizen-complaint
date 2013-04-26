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
public class CitizenComplaintPostDetailsAsyncTask extends AsyncTask<CitizenComplaint, Void, Boolean> {
    private Context context;
    private ProgressDialog progressDialog;

    public CitizenComplaintPostDetailsAsyncTask(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading your complaint details..");
        progressDialog.setTitle("Please wait");
        progressDialog.setIndeterminate(true);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Boolean doInBackground(CitizenComplaint... params) {
        Boolean success = true;
        try {
            HttpParams httpParams = new BasicHttpParams();
            httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpPost httppost = new HttpPost(CitizenComplaintConstants.CITIZEN_COMPLAINT_HOST);
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntity.addPart(CitizenComplaintConstants.LATTITUDE_PARAMS, new StringBody("" + params[0].getLattitude()));
            multipartEntity.addPart(CitizenComplaintConstants.LONGITUDE_PARAMS, new StringBody("" + params[0].getLongitude()));
            multipartEntity.addPart(CitizenComplaintConstants.ISSUE_TYPE_PARAMS, new StringBody("" + params[0].getComplaintId()));
            multipartEntity.addPart(CitizenComplaintConstants.TEMPLATE_ID_PARAMS, new StringBody("" + params[0].getSelectedTemplateId()));
            multipartEntity.addPart(CitizenComplaintConstants.TEMPLATE_TEXT_PARAMS, new StringBody("" + params[0].getSelectedTemplateString()));
            multipartEntity.addPart(CitizenComplaintConstants.REPORTER_ID_PARAMS, new StringBody("" + CitizenComplaintConstants.REPORTER_ID_VALUE));
            if (params[0].getSelectedComplaintImageUri() != null) {
                multipartEntity.addPart(CitizenComplaintConstants.IMAGE_URI_PARAMS, new FileBody(new File("" + params[0].getSelectedComplaintImageUri())));
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
