package com.webshrub.citizencomplaint.androidapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import com.actionbarsherlock.app.SherlockActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import static com.webshrub.citizencomplaint.androidapp.CitizenComplaintConstants.*;

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
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.camera)
            .showImageForEmptyUri(R.drawable.ic_empty)
            .showImageOnFail(R.drawable.ic_error)
            .cacheInMemory()
            .cacheOnDisc()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citizen_complaint_profile_activity);
        findViewById(R.id.button1).setOnClickListener(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String profileImageUriString = preferences.getString(PROFILE_IMAGE_URI, "");
        if (!profileImageUriString.equals("")) {
            profileImageUri = Uri.parse(profileImageUriString);
            ImageView imageView = (ImageView) findViewById(R.id.imageView1);
            ImageLoader.getInstance().displayImage(profileImageUri.toString(), imageView, options);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1: {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setCancelable(true);
                alertDialog.setTitle("Upload Photo");
                alertDialog.setPositiveButton("Take a picture", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent newIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        profileImageUri = CitizenComplaintUtility.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        newIntent.putExtra(MediaStore.EXTRA_OUTPUT, profileImageUri);
                        startActivityForResult(newIntent, IMAGE_CAPTURE_REQUEST);
                    }
                });
                alertDialog.setNegativeButton("Pick existing", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent newIntent = new Intent();
                        newIntent.setType("image/*");
                        newIntent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(newIntent, IMAGE_SELECT_REQUEST);
                    }
                });
                alertDialog.show();
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
                    if (profileImageUri != null) {
                        ImageLoader.getInstance().displayImage(profileImageUri.toString(), imageView, options);
                        String thumbnailPath = CitizenComplaintUtility.getCompressedImagePath(CitizenComplaintUtility.getAbsoluteFilePath(this, profileImageUri.toString()), THUMBNAIL_SIZE, THUMBNAIL_SIZE);
                        SharedPreferences.Editor preferenceEditor = preferences.edit();
                        preferenceEditor.putString(PROFILE_IMAGE_URI, profileImageUri.toString());
                        preferenceEditor.putString(PROFILE_THUMBNAIL_IMAGE_URI, thumbnailPath);
                        preferenceEditor.commit();
                    }
                }
                break;
            case IMAGE_SELECT_REQUEST:
                if (resultCode == RESULT_OK) {
                    profileImageUri = intent.getData();
                    if (profileImageUri != null) {
                        ImageLoader.getInstance().displayImage(profileImageUri.toString(), imageView, options);
                        String thumbnailPath = CitizenComplaintUtility.getCompressedImagePath(CitizenComplaintUtility.getAbsoluteFilePath(this, profileImageUri.toString()), THUMBNAIL_SIZE, THUMBNAIL_SIZE);
                        SharedPreferences.Editor preferenceEditor = preferences.edit();
                        preferenceEditor.putString(PROFILE_IMAGE_URI, profileImageUri.toString());
                        preferenceEditor.putString(PROFILE_THUMBNAIL_IMAGE_URI, thumbnailPath);
                        preferenceEditor.commit();
                    }
                }
            default:
                break;
        }
    }
}
