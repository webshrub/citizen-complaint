package com.webshrub.citizencomplaint.androidapp;

import android.net.Uri;

public class CitizenComplaintMLADetails {
    private String mlaName;
    private String mlaConstituency;
    private Uri mlaImageUri;

    public CitizenComplaintMLADetails(String mlaName, String mlaConstituency, Uri mlaImageUri) {
        this.mlaName = mlaName;
        this.mlaConstituency = mlaConstituency;
        this.mlaImageUri = mlaImageUri;
    }

    public String getMlaName() {
        return mlaName;
    }

    public void setMlaName(String mlaName) {
        this.mlaName = mlaName;
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
