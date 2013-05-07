package com.webshrub.citizencomplaint.androidapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CitizenComplaintSQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_CITIZEN_COMPLAINT = "citizen_complaint";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COMPLAINT_ID = "complaint_id";
    public static final String COLUMN_COMPLAINT_CATEGORY = "complaint_category";
    public static final String COLUMN_SELECTED_COMPLAINT_IMAGE_URI = "selected_complaint_image_uri";
    public static final String COLUMN_PROFILE_THUMBNAIL_IMAGE_URI = "profile_thumbnail_image_uri";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_COMPLAINT_ADDRESS = "complaint_address";
    public static final String COLUMN_SELECTED_TEMPLATE_ID = "selected_template_id";
    public static final String COLUMN_SELECTED_TEMPLATE_STRING = "selected_template_string";

    public static final String DATABASE_NAME = "citizen_complaint.db";

    public static final String[] ALL_COLUMNS = {
            COLUMN_ID,
            COLUMN_COMPLAINT_ID,
            COLUMN_COMPLAINT_CATEGORY,
            COLUMN_SELECTED_COMPLAINT_IMAGE_URI,
            COLUMN_PROFILE_THUMBNAIL_IMAGE_URI,
            COLUMN_LATITUDE,
            COLUMN_LONGITUDE,
            COLUMN_COMPLAINT_ADDRESS,
            COLUMN_SELECTED_TEMPLATE_ID,
            COLUMN_SELECTED_TEMPLATE_STRING,
    };


    private static final String DATABASE_CREATE = "create table " + TABLE_CITIZEN_COMPLAINT + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_COMPLAINT_ID + " text, "
            + COLUMN_COMPLAINT_CATEGORY + " text,"
            + COLUMN_SELECTED_COMPLAINT_IMAGE_URI + " text,"
            + COLUMN_PROFILE_THUMBNAIL_IMAGE_URI + " text,"
            + COLUMN_LATITUDE + " text,"
            + COLUMN_LONGITUDE + " text,"
            + COLUMN_COMPLAINT_ADDRESS + " text,"
            + COLUMN_SELECTED_TEMPLATE_ID + " text,"
            + COLUMN_SELECTED_TEMPLATE_STRING + " text,"
            + ");";

    public CitizenComplaintSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(CitizenComplaintSQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIZEN_COMPLAINT);
        onCreate(db);
    }
}