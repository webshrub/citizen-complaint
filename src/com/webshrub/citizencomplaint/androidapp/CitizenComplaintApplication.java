package com.webshrub.citizencomplaint.androidapp;

import android.app.Application;
import android.util.Log;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Ahsan.Javed
 * Date: 5/7/13
 * Time: 9:38 PM
 */
public class CitizenComplaintApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        CitizenComplaintDataSource dataSource = CitizenComplaintDataSource.getInstance(getApplicationContext());
        executorService.scheduleWithFixedDelay(new CitizenComplaintPostDetailsTask(dataSource), 1, 2 * 60, TimeUnit.SECONDS);
    }

    private class CitizenComplaintPostDetailsTask implements Runnable {
        private CitizenComplaintDataSource dataSource;

        public CitizenComplaintPostDetailsTask(CitizenComplaintDataSource dataSource) {
            this.dataSource = dataSource;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            for (CitizenComplaint citizenComplaint : dataSource.getAllCitizenComplaints()) {
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
                    multipartEntity.addPart(CitizenComplaintConstants.REPORTER_ID_PARAMS, new StringBody("" + CitizenComplaintUtility.getDeviceIdentifier(getApplicationContext())));
                    multipartEntity.addPart(CitizenComplaintConstants.ADDRESS_PARAMS, new StringBody("" + citizenComplaint.getComplaintAddress()));
                    if (citizenComplaint.getSelectedComplaintImageUri() != null && !citizenComplaint.getSelectedComplaintImageUri().equals("")) {
                        multipartEntity.addPart(CitizenComplaintConstants.IMAGE_URI_PARAMS, new FileBody(new File(citizenComplaint.getSelectedComplaintImageUri())));
                    }
                    if (citizenComplaint.getProfileThumbnailImageUri() != null && !citizenComplaint.getProfileThumbnailImageUri().equals("")) {
                        multipartEntity.addPart(CitizenComplaintConstants.PROFILE_IMAGE_URI_PARAMS, new FileBody(new File(citizenComplaint.getProfileThumbnailImageUri())));
                    }
                    httppost.setEntity(multipartEntity);
                    httpClient.execute(httppost, new CitizenComplaintUploadResponseHandler());
                    dataSource.deleteCitizenComplaint(citizenComplaint);
                } catch (Exception e) {
                    Log.e("Complaint Upload", "Complaint Upload exception: " + e.getMessage());
                }
            }
        }
    }

    private class CitizenComplaintUploadResponseHandler implements ResponseHandler {
        @Override
        public Object handleResponse(HttpResponse response) throws IOException {
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            Log.d("UPLOAD", responseString);
            return null;
        }
    }
}
