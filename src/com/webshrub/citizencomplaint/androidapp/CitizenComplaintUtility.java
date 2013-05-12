package com.webshrub.citizencomplaint.androidapp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.webshrub.citizencomplaint.androidapp.CitizenComplaintConstants.MEDIA_TYPE_IMAGE;

public class CitizenComplaintUtility {
    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CitizenComplaint");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CitizenComplaint", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == CitizenComplaintConstants.MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == CitizenComplaintConstants.MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }


    @SuppressWarnings("deprecation")
    public static String getAbsoluteFilePath(Activity activity, String inputUriString) {
        if (inputUriString.startsWith("content://")) {
            Uri uri = Uri.parse(inputUriString);
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else {
            Uri uri = Uri.parse(inputUriString);
            return uri.getSchemeSpecificPart().substring(2);
        }
    }

    public static String getDeviceIdentifier(Context context) {
        String deviceIdentifier = null;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            deviceIdentifier = telephonyManager.getDeviceId();
        }
        if (deviceIdentifier == null || deviceIdentifier.length() == 0) {
            deviceIdentifier = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceIdentifier;
    }

    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnectedOrConnecting();
    }

    public static boolean isGeocoderPresent() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent();
    }

    public static String getCompressedImagePath(String inputImagePath, int width, int height) {
        try {
            String outputImagePath = CitizenComplaintUtility.getOutputMediaFile(MEDIA_TYPE_IMAGE).getAbsolutePath();
            Bitmap outputImage = ThumbnailUtils.extractThumbnail(CitizenComplaintBitmapHelper.decodeFile(inputImagePath, width, height, true), width, height);
            File file = new File(outputImagePath);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            outputImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return outputImagePath;
        } catch (Exception e) {
            Log.e("Photo Upload", "Photo Upload exception: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
