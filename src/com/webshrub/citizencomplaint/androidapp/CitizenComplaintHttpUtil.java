package com.webshrub.citizencomplaint.androidapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CitizenComplaintHttpUtil {

    public static JSONObject getJSONFromUrl(String url) {
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
            Log.e("CitizenComplaintHttpUtil", "Error while retrieving json from " + url);
        }
        return null;
    }

    public static Bitmap getBitmapFromUrl(String url) {
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
            Log.e("CitizenComplaintHttpUtil", "Error while retrieving bitmap from " + url);
        }
        return null;
    }
}
