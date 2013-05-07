package com.webshrub.citizencomplaint.androidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
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
    private SQLiteDatabase database;
    private CitizenComplaintSQLiteHelper dbHelper;

    public CitizenComplaintDataSource(Context context) {
        dbHelper = new CitizenComplaintSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public CitizenComplaint createCitizenComplaint(String complaintId, String complaintCategory, String selectedComplaintImageUri, String profileThumbnailImageUri, String latitude, String longitude, String complaintAddress, String selectedTemplateId, String selectedTemplateString) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLAINT_ID, complaintId);
        values.put(COLUMN_COMPLAINT_CATEGORY, complaintCategory);
        values.put(COLUMN_SELECTED_COMPLAINT_IMAGE_URI, selectedComplaintImageUri);
        values.put(COLUMN_PROFILE_THUMBNAIL_IMAGE_URI, profileThumbnailImageUri);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_COMPLAINT_ADDRESS, complaintAddress);
        values.put(COLUMN_SELECTED_TEMPLATE_ID, selectedTemplateId);
        values.put(COLUMN_SELECTED_TEMPLATE_STRING, selectedTemplateString);
        long insertId = database.insert(TABLE_CITIZEN_COMPLAINT, null, values);
        Cursor cursor = database.query(TABLE_CITIZEN_COMPLAINT, ALL_COLUMNS, COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        CitizenComplaint citizenComplaint = cursorToCitizenComplaint(cursor);
        cursor.close();
        return citizenComplaint;
    }

    public void deleteCitizenComplaint(CitizenComplaint citizenComplaint) {
        long id = citizenComplaint.getId();
        System.out.println("CitizenComplaint deleted with id: " + id);
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
