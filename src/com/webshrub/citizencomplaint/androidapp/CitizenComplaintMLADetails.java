package com.webshrub.citizencomplaint.androidapp;

import android.graphics.Bitmap;

public class CitizenComplaintMLADetails {
    private String mlaName;
    private String mlaConstituency;
    private Bitmap mlaImage;

    public CitizenComplaintMLADetails(String mlaName, String mlaConstituency, Bitmap mlaImage) {
        this.mlaName = mlaName;
        this.mlaConstituency = mlaConstituency;
        this.mlaImage = mlaImage;
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

    public Bitmap getMlaImage() {
        return mlaImage;
    }

    public void setMlaImage(Bitmap mlaImage) {
        this.mlaImage = mlaImage;
    }
}
