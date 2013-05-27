package com.webshrub.citizencomplaint.androidapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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
                showUploadPhotoDialog();
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

    private void showUploadPhotoDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.citizen_complaint_upload_photo_dialog);
        ListView listView = (ListView) dialog.findViewById(R.id.listview1);
        listView.setOnItemClickListener(new CitizenComplaintUploadPhotoOnItemClickListener(dialog));
        dialog.setCancelable(true);
        dialog.setTitle("Upload Photo");
        dialog.show();
    }

    private class CitizenComplaintUploadPhotoOnItemClickListener implements AdapterView.OnItemClickListener {
        private Dialog dialog;

        public CitizenComplaintUploadPhotoOnItemClickListener(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent newIntent;
            dialog.dismiss();
            switch (position) {
                case 0:
                    newIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    profileImageUri = CitizenComplaintUtility.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    newIntent.putExtra(MediaStore.EXTRA_OUTPUT, profileImageUri);
                    startActivityForResult(newIntent, IMAGE_CAPTURE_REQUEST);
                    break;
                case 1:
                    newIntent = new Intent();
                    newIntent.setType("image/*");
                    newIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(newIntent, IMAGE_SELECT_REQUEST);
                    break;
            }
        }
    }
}
