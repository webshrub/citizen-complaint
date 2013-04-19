package com.bangalore.civic.complaint.box;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class ActionActivity extends SherlockActivity {

	private String[] baseItems;
	private String[] baseItemsVal;
	private String curBaseItem;
	private String curBaseItemVal;
	private int curListID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_action);
		int baseItemPos = getIntent().getIntExtra(
				IntentExtraConstants.BASE_ITEM_POS, -1);
		if (baseItemPos == -1) {
			finish();
			return;
		}
		baseItems = getResources().getStringArray(R.array.basic_items);
		baseItemsVal = getResources().getStringArray(R.array.basic_items_vals);
		curBaseItem = baseItems[baseItemPos];
		curBaseItemVal = baseItemsVal[baseItemPos];

		switch (baseItemPos) {
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

		((TextView) findViewById(R.id.textView1)).setText("Complaint about : "
				+ baseItems[baseItemPos] + "...");

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item, R.id.textView1, getResources()
						.getStringArray(curListID));
		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				if (pos == adapter.getCount() - 1) {
					Intent newIntent = new Intent(ActionActivity.this,
							TypeComplaintActivity.class);
					newIntent.putExtras(getIntent());
					startActivity(newIntent);
				} else {
					Intent newIntent = new Intent(ActionActivity.this,
							GetLocationActivity.class);
					newIntent.putExtras(getIntent());
					newIntent.putExtra(IntentExtraConstants.ACTION_DETAILS_POS,
							pos);
					newIntent.putExtra(
							IntentExtraConstants.ACTION_DETAILS_TEXT,
							getResources().getStringArray(curListID)[pos]);
					startActivity(newIntent);
				}

			}
		});
	}

}
