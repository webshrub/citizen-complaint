package com.webshrub.citizencomplaint.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class CitizenComplaintTypeComplaintActivity extends CitizenComplaintActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_complaint);
        findViewById(R.id.button1).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        CitizenComplaint citizenComplaint = ((CitizenComplaint) getIntent().getExtras().getParcelable(CitizenComplaintConstants.CITIZEN_COMPLAINT));
        String selectedTemplateComplaintString = ((EditText) findViewById(R.id.editText1)).getText().toString();
        citizenComplaint.setSelectedTemplateString(selectedTemplateComplaintString);
        Intent newIntent = new Intent(CitizenComplaintTypeComplaintActivity.this, CitizenComplaintPhotoCaptureActivity.class);
        newIntent.putExtra(CitizenComplaintConstants.CITIZEN_COMPLAINT, citizenComplaint);
        startActivity(newIntent);
    }
}
