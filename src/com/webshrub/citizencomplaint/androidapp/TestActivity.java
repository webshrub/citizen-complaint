package com.webshrub.citizencomplaint.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class TestActivity extends Activity {
    private static final int CAMERA_PIC_REQUEST = 1313;
    private ImageView imgTakenPhoto;
    private Uri imageUri;
    private static final String LOG_TAG = "TestActivity";
    private long mCaptureTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        Button btnTakePhoto = (Button) findViewById(R.id.button1);
        imgTakenPhoto = (ImageView) findViewById(R.id.imageView1);
        btnTakePhoto.setOnClickListener(new btnTakePhotoClicker());
        if (savedInstanceState != null && savedInstanceState.containsKey("IMAGE_URI_M")) {
            imageUri = Uri.parse(savedInstanceState.getString("IMAGE_URI_M"));
            mCaptureTime = savedInstanceState.getLong("M_CAPTURE_TIME");
            imgTakenPhoto.setImageURI(imageUri);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString("IMAGE_URI_M", imageUri.toString());
        }
        if (mCaptureTime != 0) {
            outState.putLong("M_CAPTURE_TIME", mCaptureTime);
        }
        super.onSaveInstanceState(outState);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
//            if (intent != null) {
//                Bitmap thumbnail = (Bitmap) intent.getExtras().get("data");
////                imgTakenPhoto.setImageBitmap(thumbnail);
//                imageUri = intent.getData();
//                imgTakenPhoto.setImageURI(imageUri);
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.v(LOG_TAG, "Camera intent succeeded");
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(LOG_TAG, "Camera intent aborted");
            } else {
                Log.e(LOG_TAG, "Camera intent failed with result code: " + resultCode);
            }
            // Regardless of the resultCode we're going to check if a new photo has been created on the phone. At least on the Samsung Galaxy S3 the behavior
            // could be observed that although the result code was "0" the camera app created two (!) files on the SD card.
            // Image captured and saved to fileUri specified in the Intent
            Uri targetUri = (data != null) ? data.getData() : null;
            if (targetUri == null) {
                Log.w(LOG_TAG, "Camera intent returned empty result.");
                targetUri = imageUri;
            }
            if (targetUri != null) {
                String targetFilePath = targetUri.getPath();
                File targetFile = new File(targetFilePath);
                if (targetFile.exists()) {
                    Log.i(LOG_TAG, "Image saved to: " + targetUri.toString());
                    // Fix for issue reported here: http://code.google.com/p/android/issues/detail?id=22822
                    // and here: http://code.google.com/p/android/issues/detail?id=19268
                    // We're following the proposed solution from http://stackoverflow.com/questions/8450539/
                    int rotation = -1;
                    long fileSize = targetFile.length();
                    Cursor mediaCursor = getContentResolver().query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            new String[]{MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATA},
                            MediaStore.MediaColumns.DATE_ADDED + ">=?", new String[]{String.valueOf(mCaptureTime / 1000 - 1)},
                            MediaStore.MediaColumns.DATE_ADDED + " desc"
                    );
                    if ((mediaCursor != null) && (mCaptureTime != 0)) {
                        if (mediaCursor.moveToFirst()) {
                            do {
                                long size = mediaCursor.getLong(1);
                                Uri uri = Uri.parse(mediaCursor.getString(2));
                                // Extra check to make sure that we are getting the orientation from the proper file
                                if (size == fileSize && !uri.toString().equals(targetUri.toString())) {
                                    rotation = mediaCursor.getInt(0);
                                    break;
                                }
                            } while (mediaCursor.moveToNext());
                        }
                        mediaCursor.close();
                    }
                    if (rotation == -1) {
                        // It seems that there has been no duplication and no rotation issue so far. This means we can add our newly created file to the media library.
                    } else {
                        // Looks like the picture already exists in the media library. This indicates we got a duplicate.
                        Log.w(LOG_TAG, "Duplicate image found for " + targetUri.toString() + " in media library. Deleting the copy.");
                        if (!targetFile.delete()) {
                            Log.e(LOG_TAG, "Failed to delete duplicate image.");
                        }
                    }
                }
                imgTakenPhoto.setImageURI(targetUri);
            }
        }
    }

    private class btnTakePhotoClicker implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            imageUri = CitizenComplaintUtility.getOutputMediaFileUri(CitizenComplaintConstants.MEDIA_TYPE_IMAGE);
//            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            mCaptureTime = System.currentTimeMillis();
            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        }
    }
}