package com.webshrub.citizencomplaint.androidapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by IntelliJ IDEA.
 * User: Ahsan.Javed
 * Date: 4/25/13
 * Time: 1:40 PM
 */
public class CitizenComplaintTemplate implements Parcelable {
    private String templateId;
    private String templateString;

    public CitizenComplaintTemplate() {
    }

    public CitizenComplaintTemplate(Parcel in) {
        readFromParcel(in);
    }


    public CitizenComplaintTemplate(String templateId, String templateString) {
        this.templateId = templateId;
        this.templateString = templateString;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateString() {
        return templateString;
    }

    public void setTemplateString(String templateString) {
        this.templateString = templateString;
    }

    @Override
    public String toString() {
        return templateString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(templateId);
        dest.writeString(templateString);
    }

    private void readFromParcel(Parcel in) {
        templateId = in.readString();
        templateString = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CitizenComplaintTemplate createFromParcel(Parcel in) {
            return new CitizenComplaintTemplate(in);
        }

        public CitizenComplaintTemplate[] newArray(int size) {
            return new CitizenComplaintTemplate[size];
        }
    };
}
