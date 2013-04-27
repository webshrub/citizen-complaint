package com.webshrub.citizencomplaint.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragment;

import java.util.List;

public class CitizenComplaintListFragment extends SherlockFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.citizen_complaint_list_fragment, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listView1);
        List<CitizenComplaint> citizenComplaints = new CitizenComplaintManager(getSherlockActivity()).getCitizenComplaints();
        CitizenComplaint[] data = citizenComplaints.toArray(new CitizenComplaint[citizenComplaints.size()]);
        listView.setAdapter(new CitizenComplaintAdapter<CitizenComplaint>(getSherlockActivity(), R.layout.citizen_complaint_list_item, data));
        listView.setOnItemClickListener(new CitizenComplaintOnItemClickListener(listView));
        return view;
    }

    private class CitizenComplaintOnItemClickListener implements AdapterView.OnItemClickListener {
        private final ListView listView;

        public CitizenComplaintOnItemClickListener(ListView listView) {
            this.listView = listView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent newIntent = new Intent(getSherlockActivity(), CitizenComplaintTemplateListActivity.class);
            newIntent.putExtra(CitizenComplaintConstants.CITIZEN_COMPLAINT, (CitizenComplaint) listView.getAdapter().getItem(position));
            startActivity(newIntent);
        }
    }
}