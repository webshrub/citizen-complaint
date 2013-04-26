package com.webshrub.citizencomplaint.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class CitizenComplaintTemplateListActivity extends CitizenComplaintActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        CitizenComplaint citizenComplaint = ((CitizenComplaint) getIntent().getExtras().getParcelable(CitizenComplaintConstants.CITIZEN_COMPLAINT));
        String[] templates = getTemplates(citizenComplaint);
        ((TextView) findViewById(R.id.textView1)).setText("Complaint about : " + citizenComplaint.getComplaintCategory() + "...");
        ListView listView = (ListView) findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.textView1, templates);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new CitizenComplaintTemplateOnItemClickListener(adapter));
    }

    private String[] getTemplates(CitizenComplaint citizenComplaint) {
        String[] data = new String[citizenComplaint.getCitizenComplaintTemplates().size()];
        List<CitizenComplaintTemplate> citizenComplaintTemplates = citizenComplaint.getCitizenComplaintTemplates();
        for (int i = 0, citizenComplaintTemplatesSize = citizenComplaintTemplates.size(); i < citizenComplaintTemplatesSize; i++) {
            CitizenComplaintTemplate citizenComplaintTemplate = citizenComplaintTemplates.get(i);
            data[i] = citizenComplaintTemplate.getTemplateString();
        }
        return data;
    }

    private class CitizenComplaintTemplateOnItemClickListener implements AdapterView.OnItemClickListener {
        private ArrayAdapter<String> adapter;

        public CitizenComplaintTemplateOnItemClickListener(ArrayAdapter<String> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            CitizenComplaint citizenComplaint = ((CitizenComplaint) getIntent().getExtras().getParcelable(CitizenComplaintConstants.CITIZEN_COMPLAINT));
            if (position == adapter.getCount() - 1) {
                citizenComplaint.setSelectedTemplateId(citizenComplaint.getCitizenComplaintTemplateAt(position).getTemplateId());
                Intent newIntent = new Intent(CitizenComplaintTemplateListActivity.this, CitizenComplaintTypeComplaintActivity.class);
                newIntent.putExtra(CitizenComplaintConstants.CITIZEN_COMPLAINT, citizenComplaint);
                startActivity(newIntent);
            } else {
                citizenComplaint.setSelectedTemplateId(citizenComplaint.getCitizenComplaintTemplateAt(position).getTemplateId());
                citizenComplaint.setSelectedTemplateString(citizenComplaint.getCitizenComplaintTemplateAt(position).getTemplateString());
                Intent newIntent = new Intent(CitizenComplaintTemplateListActivity.this, CitizenComplaintPhotoCaptureActivity.class);
                newIntent.putExtra(CitizenComplaintConstants.CITIZEN_COMPLAINT, citizenComplaint);
                startActivity(newIntent);
            }
        }
    }
}
