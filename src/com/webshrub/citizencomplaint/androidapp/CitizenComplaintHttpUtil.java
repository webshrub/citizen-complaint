package com.webshrub.citizencomplaint.androidapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
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
import org.json.JSONObject;

import java.io.*;

public class CitizenComplaintHttpUtil {

    public static JSONObject getJSONFromUrl(Context context, String url) {
        if (CitizenComplaintUtility.isDeviceOnline(context)) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    Log.w("CitizenComplaintHttpUtil", "Error " + statusCode + " while retrieving json from " + url);
                    return null;
                }
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
                    InputStream inputStream = null;
                    try {
                        inputStream = httpEntity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        String json = sb.toString();
                        return new JSONObject(json);
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        httpEntity.consumeContent();
                    }
                }
            } catch (Exception e) {
                httpPost.abort();
                Log.e("CitizenComplaintHttpUtil", "Error while retrieving json from " + url + " message = " + e.getMessage());
            }
        }
        return null;
    }

    public static Bitmap getBitmapFromUrl(Context context, String url) {
        if (CitizenComplaintUtility.isDeviceOnline(context)) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    Log.e("CitizenComplaintHttpUtil", "Error " + statusCode + " while retrieving bitmap from " + url);
                    return null;
                }
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
                    InputStream inputStream = null;
                    try {
                        inputStream = httpEntity.getContent();
                        return BitmapFactory.decodeStream(inputStream);
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        httpEntity.consumeContent();
                    }
                }
            } catch (Exception e) {
                httpGet.abort();
                Log.e("CitizenComplaintHttpUtil", "Error while retrieving bitmap from " + url + " message = " + e.getMessage());
            }
        }
        return null;
    }

    public static void postComplaintDetails(Context context, CitizenComplaint citizenComplaint) {
        try {
            if (CitizenComplaintUtility.isDeviceOnline(context)) {
                HttpParams httpParams = new BasicHttpParams();
                httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
                HttpPost httppost = new HttpPost(CitizenComplaintConstants.CITIZEN_COMPLAINT_POST_URL_PARAMS);
                MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                multipartEntity.addPart(CitizenComplaintConstants.LATTITUDE_PARAMS, new StringBody("" + citizenComplaint.getLatitude()));
                multipartEntity.addPart(CitizenComplaintConstants.LONGITUDE_PARAMS, new StringBody("" + citizenComplaint.getLongitude()));
                multipartEntity.addPart(CitizenComplaintConstants.ISSUE_TYPE_PARAMS, new StringBody("" + citizenComplaint.getComplaintId()));
                multipartEntity.addPart(CitizenComplaintConstants.TEMPLATE_ID_PARAMS, new StringBody("" + citizenComplaint.getSelectedTemplateId()));
                multipartEntity.addPart(CitizenComplaintConstants.TEMPLATE_TEXT_PARAMS, new StringBody("" + citizenComplaint.getSelectedTemplateString()));
                multipartEntity.addPart(CitizenComplaintConstants.REPORTER_ID_PARAMS, new StringBody("" + CitizenComplaintUtility.getDeviceIdentifier(context)));
                multipartEntity.addPart(CitizenComplaintConstants.ADDRESS_PARAMS, new StringBody("" + citizenComplaint.getComplaintAddress()));
                if (citizenComplaint.getSelectedComplaintImageUri() != null && !citizenComplaint.getSelectedComplaintImageUri().equals("")) {
                    File file = new File(citizenComplaint.getSelectedComplaintImageUri());
                    if (file.exists()) {
                        multipartEntity.addPart(CitizenComplaintConstants.IMAGE_URI_PARAMS, new FileBody(file));
                    }
                }
                if (citizenComplaint.getProfileThumbnailImageUri() != null && !citizenComplaint.getProfileThumbnailImageUri().equals("")) {
                    File file = new File(citizenComplaint.getProfileThumbnailImageUri());
                    if (file.exists()) {
                        multipartEntity.addPart(CitizenComplaintConstants.PROFILE_IMAGE_URI_PARAMS, new FileBody(file));
                    }
                }
                httppost.setEntity(multipartEntity);
                httpClient.execute(httppost, new CitizenComplaintUploadResponseHandler());
                CitizenComplaintDataSource.getInstance(context).deleteCitizenComplaint(citizenComplaint);
            }
        } catch (Exception e) {
            Log.e("Complaint Upload", "Complaint Upload exception: " + e.getMessage());
        }
    }

    private static class CitizenComplaintUploadResponseHandler implements ResponseHandler {
        @Override
        public Object handleResponse(HttpResponse response) throws IOException {
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            Log.d("UPLOAD", responseString);
            return null;
        }
    }
}
