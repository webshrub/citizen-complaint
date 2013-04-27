package com.webshrub.citizencomplaint.androidapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class CitizenComplaintPhotoCaptureActivity extends CitizenComplaintActivity implements OnClickListener {
    private static final int IMAGE_CAPTURE_REQUEST = 100;
    private static final int IMAGE_SELECT_REQUEST = 101;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        if (savedInstanceState != null && savedInstanceState.containsKey(CitizenComplaintConstants.IMAGE_URI)) {
            imageUri = Uri.parse(savedInstanceState.getString(CitizenComplaintConstants.IMAGE_URI));
            ((ImageView) findViewById(R.id.imageView1)).setImageURI(imageUri);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString(CitizenComplaintConstants.IMAGE_URI, imageUri.toString());
        }
        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View v) {
        Intent newIntent;
        switch (v.getId()) {
            case R.id.button1: {
                newIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = CitizenComplaintUtility.getOutputMediaFileUri(CitizenComplaintConstants.MEDIA_TYPE_IMAGE);
                newIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(newIntent, IMAGE_CAPTURE_REQUEST);
            }
            break;
            case R.id.button2: {
                newIntent = new Intent();
                newIntent.setType("image/*");
                newIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(newIntent, IMAGE_SELECT_REQUEST);
            }
            break;
            case R.id.button3: {
                CitizenComplaint citizenComplaint = ((CitizenComplaint) getIntent().getExtras().getParcelable(CitizenComplaintConstants.CITIZEN_COMPLAINT));
                if (imageUri != null) {
                    citizenComplaint.setSelectedComplaintImageUri(imageUri.toString());
                }
                new CitizenComplaintPostDetailsAsyncTask(this, citizenComplaint).execute();
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        ImageView imageView = ((ImageView) findViewById(R.id.imageView1));
        switch (requestCode) {
            case IMAGE_CAPTURE_REQUEST:
                if (resultCode == RESULT_OK) {
                    imageView.setImageURI(imageUri);
                }
                break;
            case IMAGE_SELECT_REQUEST:
                if (resultCode == RESULT_OK) {
                    imageUri = intent.getData();
                    imageView.setImageURI(imageUri);
                }
            default:
                break;
        }
    }
}
