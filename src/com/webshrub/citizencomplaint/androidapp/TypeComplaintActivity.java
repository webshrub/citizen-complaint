package com.webshrub.citizencomplaint.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockActivity;

public class TypeComplaintActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_complaint);
        ((Button) findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String complaint = ((EditText) findViewById(R.id.editText1)).getText().toString();
                Intent newIntent = new Intent(TypeComplaintActivity.this, GetLocationActivity.class);
                newIntent.putExtras(getIntent());
                newIntent.putExtra(IntentExtraConstants.ACTION_DETAILS_POS, -1);
                newIntent.putExtra(IntentExtraConstants.ACTION_DETAILS_TEXT, complaint);
                startActivity(newIntent);
            }
        });
    }
}
