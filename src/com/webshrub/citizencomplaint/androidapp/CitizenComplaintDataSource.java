package com.webshrub.citizencomplaint.androidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.webshrub.citizencomplaint.androidapp.CitizenComplaintSQLiteHelper.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ahsan.Javed
 * Date: 5/6/13
 * Time: 10:16 PM
 */
public class CitizenComplaintDataSource {
    private static CitizenComplaintDataSource instance;

    private SQLiteDatabase database;

    private CitizenComplaintDataSource(Context context) {
        CitizenComplaintSQLiteHelper helper = new CitizenComplaintSQLiteHelper(context);
        database = helper.getWritableDatabase();
    }

    //no synchronized???
    public static CitizenComplaintDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new CitizenComplaintDataSource(context);
        }
        return instance;
    }

    public long createCitizenComplaint(CitizenComplaint citizenComplaint) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLAINT_ID, citizenComplaint.getComplaintId());
        values.put(COLUMN_COMPLAINT_CATEGORY, citizenComplaint.getComplaintCategory());
        values.put(COLUMN_SELECTED_COMPLAINT_IMAGE_URI, citizenComplaint.getSelectedComplaintImageUri());
        values.put(COLUMN_PROFILE_THUMBNAIL_IMAGE_URI, citizenComplaint.getProfileThumbnailImageUri());
        values.put(COLUMN_LATITUDE, citizenComplaint.getLatitude());
        values.put(COLUMN_LONGITUDE, citizenComplaint.getLongitude());
        values.put(COLUMN_COMPLAINT_ADDRESS, citizenComplaint.getComplaintAddress());
        values.put(COLUMN_SELECTED_TEMPLATE_ID, citizenComplaint.getSelectedTemplateId());
        values.put(COLUMN_SELECTED_TEMPLATE_STRING, citizenComplaint.getSelectedTemplateString());
        return database.insert(TABLE_CITIZEN_COMPLAINT, null, values);
    }

    public void deleteCitizenComplaint(CitizenComplaint citizenComplaint) {
        long id = citizenComplaint.getId();
        database.delete(TABLE_CITIZEN_COMPLAINT, COLUMN_ID + " = " + id, null);
    }

    public List<CitizenComplaint> getAllCitizenComplaints() {
        List<CitizenComplaint> citizenComplaints = new ArrayList<CitizenComplaint>();
        Cursor cursor = database.query(TABLE_CITIZEN_COMPLAINT, ALL_COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CitizenComplaint citizenComplaint = cursorToCitizenComplaint(cursor);
            citizenComplaints.add(citizenComplaint);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return citizenComplaints;
    }

    private CitizenComplaint cursorToCitizenComplaint(Cursor cursor) {
        CitizenComplaint citizenComplaint = new CitizenComplaint();
        citizenComplaint.setId(cursor.getLong(0));
        citizenComplaint.setComplaintId(cursor.getString(1));
        citizenComplaint.setComplaintCategory(cursor.getString(2));
        citizenComplaint.setSelectedComplaintImageUri(cursor.getString(3));
        citizenComplaint.setProfileThumbnailImageUri(cursor.getString(4));
        citizenComplaint.setLatitude(cursor.getString(5));
        citizenComplaint.setLongitude(cursor.getString(6));
        citizenComplaint.setComplaintAddress(cursor.getString(7));
        citizenComplaint.setSelectedTemplateId(cursor.getString(8));
        citizenComplaint.setSelectedTemplateString(cursor.getString(9));
        return citizenComplaint;
    }
}
