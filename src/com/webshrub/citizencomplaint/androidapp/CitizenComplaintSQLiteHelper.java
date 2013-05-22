package com.webshrub.citizencomplaint.androidapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CitizenComplaintSQLiteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "citizen_complaint.db";
    public static final String TABLE_CITIZEN_COMPLAINT = "citizen_complaint";
    public static final String TABLE_MLA_DETAIL = "mla_detail";
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
    public static final String COLUMN_MLA_NAME = "mla_name";
    public static final String COLUMN_MLA_EMAIL = "mla_email";
    public static final String COLUMN_MLA_CONTACT_NO = "mla_contact_no";
    public static final String COLUMN_MLA_CONSTITUENCY_ID = "mla_constituency_id";
    public static final String COLUMN_MLA_CONSTITUENCY = "mla_constituency";
    public static final String COLUMN_MLA_IMAGE_URI = "mla_image_uri";
    public static final String[] COLUMNS_CITIZEN_COMPLAINT = {
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
    public static final String[] COLUMNS_MLA_DETAIL = {
            COLUMN_ID,
            COLUMN_MLA_NAME,
            COLUMN_MLA_EMAIL,
            COLUMN_MLA_CONTACT_NO,
            COLUMN_MLA_CONSTITUENCY_ID,
            COLUMN_MLA_CONSTITUENCY,
            COLUMN_MLA_IMAGE_URI
    };
    public static final String CREATE_CITIZEN_COMPLAINT = "create table " + TABLE_CITIZEN_COMPLAINT + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_COMPLAINT_ID + " text, "
            + COLUMN_COMPLAINT_CATEGORY + " text,"
            + COLUMN_SELECTED_COMPLAINT_IMAGE_URI + " text,"
            + COLUMN_PROFILE_THUMBNAIL_IMAGE_URI + " text,"
            + COLUMN_LATITUDE + " text,"
            + COLUMN_LONGITUDE + " text,"
            + COLUMN_COMPLAINT_ADDRESS + " text,"
            + COLUMN_SELECTED_TEMPLATE_ID + " text,"
            + COLUMN_SELECTED_TEMPLATE_STRING + " text"
            + ");";
    public static final String CREATE_MLA_DETAIL = "create table " + TABLE_MLA_DETAIL + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_MLA_NAME + " text,"
            + COLUMN_MLA_EMAIL + " text,"
            + COLUMN_MLA_CONTACT_NO + " text,"
            + COLUMN_MLA_CONSTITUENCY_ID + " text,"
            + COLUMN_MLA_CONSTITUENCY + " text,"
            + COLUMN_MLA_IMAGE_URI + " text"
            + ");";

    public CitizenComplaintSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_CITIZEN_COMPLAINT);
        database.execSQL(CREATE_MLA_DETAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(CitizenComplaintSQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIZEN_COMPLAINT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MLA_DETAIL);
        onCreate(db);
    }
}