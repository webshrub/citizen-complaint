package com.webshrub.citizencomplaint.androidapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import static com.webshrub.citizencomplaint.androidapp.CitizenComplaintConstants.*;

public class CitizenComplaintPhotoCaptureActivity extends CitizenComplaintActivity implements OnClickListener {
    private static final int IMAGE_CAPTURE_REQUEST = 100;
    private static final int IMAGE_SELECT_REQUEST = 101;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citizen_complaint_photo_capture_activity);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        CitizenComplaint citizenComplaint = getIntent().getExtras().getParcelable(CITIZEN_COMPLAINT);
        TextView category = (TextView) findViewById(R.id.categoryTextView);
        category.setText(citizenComplaint.getComplaintCategory());
        TextView complaint = (TextView) findViewById(R.id.complaintTextView);
        complaint.setText(citizenComplaint.getSelectedTemplateString());
        if (savedInstanceState != null && savedInstanceState.containsKey(COMPLAINT_IMAGE_URI)) {
            imageUri = Uri.parse(savedInstanceState.getString(COMPLAINT_IMAGE_URI));
            ((ImageView) findViewById(R.id.imageView1)).setImageURI(imageUri);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString(COMPLAINT_IMAGE_URI, imageUri.toString());
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
                imageUri = CitizenComplaintUtility.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
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
                CitizenComplaint citizenComplaint = getIntent().getExtras().getParcelable(CITIZEN_COMPLAINT);
                if (imageUri != null) {
                    String compressedImagePath = CitizenComplaintUtility.getCompressedImagePath(CitizenComplaintUtility.getAbsoluteFilePath(this, imageUri.toString()), COMPLAINT_IMAGE_WIDTH, COMPLAINT_IMAGE_HEIGHT);
                    citizenComplaint.setSelectedComplaintImageUri(compressedImagePath);
                }
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String profileThumbnailImageUriString = preferences.getString(PROFILE_THUMBNAIL_IMAGE_URI, "");
                citizenComplaint.setProfileThumbnailImageUri(profileThumbnailImageUriString);
                new CitizenComplaintInsertDetailsTask(this, citizenComplaint).execute();
                break;
            }
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

    @Override
    protected void onStart() {
        super.onStart();
        // Check if the GPS setting is currently enabled on the device. This verification should be done during onStart() because the system calls this method
        // when the user returns to the activity, which ensures the desired location provider is enabled each time the activity resumes from the stopped state.
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            showSettingsDialog();
        }
    }

    public void showSettingsDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(getString(R.string.enableLocationServices));
        alertDialog.setMessage(getString(R.string.locationProvidersDisabled));
        alertDialog.setPositiveButton(getString(R.string.enable), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.show();
    }
}
