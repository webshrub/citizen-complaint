package com.webshrub.citizencomplaint.androidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by IntelliJ IDEA.
 * User: Ahsan.Javed
 * Date: 4/25/13
 * Time: 1:58 PM
 */
public class CitizenComplaintAdapter<CitizenComplaint> extends ArrayAdapter {
    @SuppressWarnings("unchecked")
    public CitizenComplaintAdapter(Context context, int layoutResourceId, CitizenComplaint[] data) {
        super(context, layoutResourceId, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.citizen_complaint_list_item, null);
        }
        TextView textView = (TextView) view.findViewById(R.id.textView1);
        textView.setText(getItem(position).toString());
        return view;
    }
}
