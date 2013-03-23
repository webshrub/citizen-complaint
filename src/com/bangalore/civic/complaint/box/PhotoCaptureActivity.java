package com.bangalore.civic.complaint.box;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
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

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;

public class PhotoCaptureActivity extends SherlockActivity implements
		OnClickListener {

	private static final int IMAGE_CAPTURE_REQUEST = 100;
	private static final int IMAGE_SELECT_REQUEST = 101;
	private static final String IMAGE_URI = "IMAGE_URI";
	private Uri uri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_capture);
		((Button) findViewById(R.id.button1)).setOnClickListener(this);
		((Button) findViewById(R.id.button2)).setOnClickListener(this);
		((Button) findViewById(R.id.button3)).setOnClickListener(this);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(IMAGE_URI)) {
			uri = Uri.parse(savedInstanceState.getString(IMAGE_URI));
			((ImageView) findViewById(R.id.imageView1)).setImageURI(uri);
		}
	}

	@Override
	public void onClick(View v) {
		Intent newIntent = null;
		switch (v.getId()) {
		case R.id.button1: {
			newIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			uri = MyUtils.getOutputMediaFileUri(MyUtils.MEDIA_TYPE_IMAGE);
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
			}
		default:
			break;
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
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
		public Object handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {

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
				httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
						HttpVersion.HTTP_1_1);
				DefaultHttpClient mHttpClient = new DefaultHttpClient(
						httpParams);

				HttpPost httppost = new HttpPost(
						"http://50.57.224.47/html/dev/micronews/?q=phonegap/post");

				MultipartEntity multipartEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				multipartEntity.addPart(
						"lat",
						new StringBody(""
								+ params[0].getDoubleExtra(
										IntentExtraConstants.LATITUDE, 0)));
				multipartEntity.addPart(
						"long",
						new StringBody(""
								+ params[0].getDoubleExtra(
										IntentExtraConstants.LONGITUDE, 0)));
				String[] baseItems = getResources().getStringArray(
						R.array.basic_items);
				multipartEntity.addPart(
						"issue_type",
						new StringBody(baseItems[params[0].getIntExtra(
								IntentExtraConstants.BASE_ITEM_POS, 0)]));
				multipartEntity
						.addPart(
								"issue_tmpl_id",
								new StringBody(
										params[0]
												.getStringExtra(IntentExtraConstants.ACTION_DETAILS_TEXT)));
				if (uri != null) {
					String path = getPath(uri);
					multipartEntity
							.addPart("img", new FileBody(new File(path)));
				}
				httppost.setEntity(multipartEntity);
				mHttpClient.execute(httppost, new PhotoUploadResponseHandler());
				success = true;
			} catch (Exception e) {
				Log.e("Photo Upload",
						"Photo Upload exception: " + e.getMessage());
			}
			return success;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			showDialog(0);

		}

	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this).setTitle("Upload Done")
				.setMessage("Upload done")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent newIntent = new Intent(
								PhotoCaptureActivity.this, MainActivity.class);
						newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
								| Intent.FLAG_ACTIVITY_NEW_TASK);
					}

				}).create();

	}
}
