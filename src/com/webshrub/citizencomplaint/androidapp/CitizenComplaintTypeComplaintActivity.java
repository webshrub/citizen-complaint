package com.webshrub.citizencomplaint.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CitizenComplaintTypeComplaintActivity extends CitizenComplaintActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citizen_complaint_type_complaint_activity);
        findViewById(R.id.button1).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        CitizenComplaint citizenComplaint = getIntent().getExtras().getParcelable(CitizenComplaintConstants.CITIZEN_COMPLAINT);
        Spinner complaintType = (Spinner) findViewById(R.id.typeOfComplaintSpinner);
        int selectedItemPosition = complaintType.getSelectedItemPosition();
        switch (selectedItemPosition) {
            case 0:
                citizenComplaint.setSelectedTemplateId("" + 0);
                break;
            case 1:
                citizenComplaint.setSelectedTemplateId("" + 10);
                break;
            case 2:
                citizenComplaint.setSelectedTemplateId("" + 20);
                break;
            case 3:
                citizenComplaint.setSelectedTemplateId("" + 30);
                break;
            case 4:
                citizenComplaint.setSelectedTemplateId("" + 40);
                break;
            default:
                citizenComplaint.setSelectedTemplateId("" + 0);
                break;
        }
        String selectedTemplateString = ((EditText) findViewById(R.id.editText1)).getText().toString();
        if (selectedTemplateString != null && !selectedTemplateString.equals("")) {
            citizenComplaint.setSelectedTemplateString(selectedTemplateString);
            Intent newIntent = new Intent(CitizenComplaintTypeComplaintActivity.this, CitizenComplaintPhotoCaptureActivity.class);
            newIntent.putExtra(CitizenComplaintConstants.CITIZEN_COMPLAINT, citizenComplaint);
            startActivity(newIntent);
        } else {
            Toast.makeText(this, "Please write something in complaint box.", Toast.LENGTH_SHORT).show();
        }
    }
}
