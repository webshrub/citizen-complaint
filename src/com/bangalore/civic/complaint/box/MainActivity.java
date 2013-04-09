package com.bangalore.civic.complaint.box;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends SherlockActivity {

	private String[] items;
	private String[] itemsVal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		items = getResources().getStringArray(R.array.basic_items);
		itemsVal = getResources().getStringArray(R.array.basic_items_vals);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.list_item, R.id.textView1,
				items);
		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Intent newIntent = new Intent(MainActivity.this,
						ActionActivity.class);
				newIntent.putExtra(IntentExtraConstants.BASE_ITEM_POS, pos);
				String[] itemsIndex = getResources().getStringArray(
						R.array.basic_items_index);
				newIntent.putExtra(IntentExtraConstants.BASE_ITEM_INDEX,
						itemsIndex[pos]);
				newIntent.putExtra(IntentExtraConstants.LEVEL_COMPLETE, 1);
				startActivity(newIntent);
			}
		});
	}

}
