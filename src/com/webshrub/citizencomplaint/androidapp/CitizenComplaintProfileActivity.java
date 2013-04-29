package com.webshrub.citizencomplaint.androidapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import com.actionbarsherlock.app.SherlockActivity;

/**
 * Created by IntelliJ IDEA.
 * User: Ahsan.Javed
 * Date: 4/28/13
 * Time: 3:11 PM
 */
public class CitizenComplaintProfileActivity extends SherlockActivity implements View.OnClickListener {
    private static final int IMAGE_CAPTURE_REQUEST = 100;
    private static final int IMAGE_SELECT_REQUEST = 101;
    private Uri profileImageUri;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citizen_complaint_profile_activity);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String profileImageUriString = preferences.getString(CitizenComplaintConstants.PROFILE_IMAGE_URI, "");
        if (!profileImageUriString.equals("")) {
            profileImageUri = Uri.parse(profileImageUriString);
            ((ImageView) findViewById(R.id.imageView1)).setImageURI(profileImageUri);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View v) {
        Intent newIntent;
        switch (v.getId()) {
            case R.id.button1: {
                newIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                profileImageUri = CitizenComplaintUtility.getOutputMediaFileUri(CitizenComplaintConstants.MEDIA_TYPE_IMAGE);
                newIntent.putExtra(MediaStore.EXTRA_OUTPUT, profileImageUri);
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        ImageView imageView = ((ImageView) findViewById(R.id.imageView1));
        switch (requestCode) {
            case IMAGE_CAPTURE_REQUEST:
                if (resultCode == RESULT_OK) {
                    if (profileImageUri != null) {
                        imageView.setImageURI(profileImageUri);
                        SharedPreferences.Editor preferenceEditor = preferences.edit();
                        preferenceEditor.putString(CitizenComplaintConstants.PROFILE_IMAGE_URI, profileImageUri.toString());
                        preferenceEditor.commit();
                    }
                }
                break;
            case IMAGE_SELECT_REQUEST:
                if (resultCode == RESULT_OK) {
                    profileImageUri = intent.getData();
                    if (profileImageUri != null) {
                        imageView.setImageURI(profileImageUri);
                        SharedPreferences.Editor preferenceEditor = preferences.edit();
                        preferenceEditor.putString(CitizenComplaintConstants.PROFILE_IMAGE_URI, profileImageUri.toString());
                        preferenceEditor.commit();
                    }
                }
            default:
                break;
        }
    }
}
