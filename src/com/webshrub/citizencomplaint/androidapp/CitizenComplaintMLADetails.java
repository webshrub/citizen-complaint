package com.webshrub.citizencomplaint.androidapp;

import android.net.Uri;

public class CitizenComplaintMLADetails {
    private long id;
    private String mlaName;
    private String mlaEmail;
    private String mlaContactNo;
    private String mlaConstituencyId;
    private String mlaConstituency;
    private Uri mlaImageUri;

    public CitizenComplaintMLADetails() {

    }

    public CitizenComplaintMLADetails(String mlaName, String mlaEmail, String mlaContactNo, String mlaConstituencyId, String mlaConstituency, Uri mlaImageUri) {
        this.mlaName = mlaName;
        this.mlaEmail = mlaEmail;
        this.mlaContactNo = mlaContactNo;
        this.mlaConstituencyId = mlaConstituencyId;
        this.mlaConstituency = mlaConstituency;
        this.mlaImageUri = mlaImageUri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMlaName() {
        return mlaName;
    }

    public void setMlaName(String mlaName) {
        this.mlaName = mlaName;
    }

    public String getMlaEmail() {
        return mlaEmail;
    }

    public void setMlaEmail(String mlaEmail) {
        this.mlaEmail = mlaEmail;
    }

    public String getMlaContactNo() {
        return mlaContactNo;
    }

    public void setMlaContactNo(String mlaContactNo) {
        this.mlaContactNo = mlaContactNo;
    }

    public String getMlaConstituencyId() {
        return mlaConstituencyId;
    }

    public void setMlaConstituencyId(String mlaConstituencyId) {
        this.mlaConstituencyId = mlaConstituencyId;
    }

    public String getMlaConstituency() {
        return mlaConstituency;
    }

    public void setMlaConstituency(String mlaConstituency) {
        this.mlaConstituency = mlaConstituency;
    }

    public Uri getMlaImageUri() {
        return mlaImageUri;
    }

    public void setMlaImageUri(Uri mlaImageUri) {
        this.mlaImageUri = mlaImageUri;
    }
}
