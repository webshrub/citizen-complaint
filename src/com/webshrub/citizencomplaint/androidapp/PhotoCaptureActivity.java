package com.webshrub.citizencomplaint.androidapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.actionbarsherlock.app.SherlockActivity;
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

public class PhotoCaptureActivity extends SherlockActivity implements OnClickListener {
    private static final int DIALOG_UPLOAD_DONE = 1;
    private static final int DIALOG_UPLOAD_PROGRESS = 2;
    private static final int DIALOG_UPLOAD_FAILED = 3;

    private static final int IMAGE_CAPTURE_REQUEST = 100;
    private static final int IMAGE_SELECT_REQUEST = 101;
    private static final String IMAGE_URI = "IMAGE_URI";
    private static final StringBuffer photoURL = new StringBuffer();
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        if (savedInstanceState != null && savedInstanceState.containsKey(IMAGE_URI)) {
            uri = Uri.parse(savedInstanceState.getString(IMAGE_URI));
            ((ImageView) findViewById(R.id.imageView1)).setImageURI(uri);
        }
    }

    @Override
    public void onClick(View v) {
        Intent newIntent;
        switch (v.getId()) {
            case R.id.button1: {
                newIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uri = MyUtils.getOutputMediaFileUri(MyUtils.MEDIA_TYPE_IMAGE, photoURL);
                newIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
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
                PostDetailsAsyncTask task = new PostDetailsAsyncTask();
                task.execute(getIntent());
                showDialog(DIALOG_UPLOAD_PROGRESS);
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_CAPTURE_REQUEST:
                if (resultCode == RESULT_OK) {
                    ImageView imageView = ((ImageView) findViewById(R.id.imageView1));
                    imageView.setImageURI(uri);
                }
                break;
            case IMAGE_SELECT_REQUEST:
                if (resultCode == RESULT_OK) {
                    ImageView imageView = ((ImageView) findViewById(R.id.imageView1));
                    uri = data.getData();
                    imageView.setImageURI(data.getData());
                    photoURL.delete(0, photoURL.length());
                }
            default:
                break;
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (uri != null) {
            outState.putString(IMAGE_URI, uri.toString());
        }
    }

    private class PhotoUploadResponseHandler implements ResponseHandler {
        @Override
        public Object handleResponse(HttpResponse response) throws IOException {
            HttpEntity r_entity = response.getEntity();
            String responseString = EntityUtils.toString(r_entity);
            Log.d("UPLOAD", responseString);
            return null;
        }
    }

    public class PostDetailsAsyncTask extends AsyncTask<Intent, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Intent... params) {
            Boolean success = false;
            try {
                HttpParams httpParams = new BasicHttpParams();
                httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                DefaultHttpClient mHttpClient = new DefaultHttpClient(httpParams);
                HttpPost httppost = new HttpPost("http://50.57.224.47/html/dev/micronews/?q=phonegap/post");
                MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                multipartEntity.addPart("lat", new StringBody("" + params[0].getDoubleExtra(IntentExtraConstants.LATITUDE, 0)));
                multipartEntity.addPart("long", new StringBody("" + params[0].getDoubleExtra(IntentExtraConstants.LONGITUDE, 0)));
                multipartEntity.addPart("issue_type", new StringBody(params[0].getStringExtra(IntentExtraConstants.BASE_ITEM_INDEX)));
                if (params[0].getIntExtra(IntentExtraConstants.ACTION_DETAILS_POS, -1) > -1) {
                    String[] templateid = getResources().getStringArray(getResourceIDForTemplateID());
                    multipartEntity.addPart("issue_tmpl_id", new StringBody(templateid[params[0].getIntExtra(IntentExtraConstants.ACTION_DETAILS_POS, 0)]));
                } else {
                    multipartEntity.addPart("issue_tmpl_id", new StringBody("0"));
                }
                multipartEntity.addPart("txt", new StringBody(params[0].getStringExtra(IntentExtraConstants.ACTION_DETAILS_TEXT)));
                multipartEntity.addPart("reporter_id", new StringBody("234"));
                if (photoURL.length() > 0) {
                    multipartEntity.addPart("img", new FileBody(new File(photoURL.toString())));
                } else if (uri != null) {
                    String path = getPath(uri);
                    multipartEntity.addPart("img", new FileBody(new File(path)));
                }
                httppost.setEntity(multipartEntity);
                mHttpClient.execute(httppost, new PhotoUploadResponseHandler());
                success = true;
            } catch (Exception e) {
                Log.e("Photo Upload", "Photo Upload exception: " + e.getMessage());
            }
            return success;
        }

        private int getResourceIDForTemplateText() {
            int basePos = getIntent().getIntExtra(IntentExtraConstants.BASE_ITEM_POS, 0);
            int curListID = 0;
            switch (basePos) {
                case 0:
                    curListID = R.array.water;
                    break;
                case 1:
                    curListID = R.array.lawandorder;
                    break;
                case 2:
                    curListID = R.array.electricity;
                    break;
                case 3:
                    curListID = R.array.transportation;
                    break;
                case 4:
                    curListID = R.array.road;
                    break;
                case 5:
                    curListID = R.array.sewage;
                    break;
            }
            return curListID;
        }

        private int getResourceIDForTemplateID() {
            int basePos = getIntent().getIntExtra(IntentExtraConstants.BASE_ITEM_POS, 0);
            int curListID = 0;
            switch (basePos) {
                case 0:
                    curListID = R.array.water_index;
                    break;
                case 1:
                    curListID = R.array.lawandorder_index;
                    break;
                case 2:
                    curListID = R.array.electricity_index;
                    break;
                case 3:
                    curListID = R.array.transportation_index;
                    break;
                case 4:
                    curListID = R.array.road_index;
                    break;
                case 5:
                    curListID = R.array.sewage_index;
                    break;
            }
            return curListID;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dismissDialog(DIALOG_UPLOAD_PROGRESS);
            if (result) {
                showDialog(DIALOG_UPLOAD_DONE);
            } else {
                showDialog(DIALOG_UPLOAD_FAILED);
            }
        }
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_UPLOAD_DONE: {
                return new AlertDialog.Builder(this)
                        .setTitle("Upload Done")
                        .setMessage("Upload done")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent newIntent = new Intent(PhotoCaptureActivity.this, MainActivity.class);
                                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(newIntent);
                                    }
                                }).create();
            }
            case DIALOG_UPLOAD_PROGRESS: {
                return ProgressDialog.show(this, "Uploading...", "Upload in progress");
            }
            case DIALOG_UPLOAD_FAILED: {
                return new AlertDialog.Builder(this)
                        .setTitle("Upload Failed")
                        .setMessage("Upload Failed")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();
            }
        }
        return null;
    }
}
