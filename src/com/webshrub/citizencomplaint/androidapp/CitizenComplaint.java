package com.webshrub.citizencomplaint.androidapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ahsan.Javed
 * Date: 4/25/13
 * Time: 1:38 PM
 */
public class CitizenComplaint implements Parcelable {
    private String complaintId;
    private String complaintCategory;
    private String selectedComplaintImageUri;
    private String latitude;
    private String longitude;
    private String selectedTemplateId;
    private String selectedTemplateString;
    private List<CitizenComplaintTemplate> citizenComplaintTemplates = new ArrayList<CitizenComplaintTemplate>();

    public CitizenComplaint() {
    }

    public CitizenComplaint(Parcel in) {
        readFromParcel(in);
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getComplaintCategory() {
        return complaintCategory;
    }

    public void setComplaintCategory(String complaintCategory) {
        this.complaintCategory = complaintCategory;
    }

    public String getSelectedComplaintImageUri() {
        return selectedComplaintImageUri;
    }

    public void setSelectedComplaintImageUri(String selectedComplaintImageUri) {
        this.selectedComplaintImageUri = selectedComplaintImageUri;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSelectedTemplateId() {
        return selectedTemplateId;
    }

    public void setSelectedTemplateId(String selectedTemplateId) {
        this.selectedTemplateId = selectedTemplateId;
    }

    public String getSelectedTemplateString() {
        return selectedTemplateString;
    }

    public void setSelectedTemplateString(String selectedTemplateString) {
        this.selectedTemplateString = selectedTemplateString;
    }

    public List<CitizenComplaintTemplate> getCitizenComplaintTemplates() {
        return citizenComplaintTemplates;
    }

    public void setCitizenComplaintTemplates(List<CitizenComplaintTemplate> citizenComplaintTemplates) {
        this.citizenComplaintTemplates = citizenComplaintTemplates;
    }


    public CitizenComplaintTemplate getCitizenComplaintTemplateAt(int position) {
        return citizenComplaintTemplates.get(position);
    }

    @Override
    public String toString() {
        return complaintCategory;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(complaintId);
        dest.writeString(complaintCategory);
        dest.writeString(selectedComplaintImageUri);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(selectedTemplateId);
        dest.writeString(selectedTemplateString);
        dest.writeList(citizenComplaintTemplates);
    }

    private void readFromParcel(Parcel in) {
        complaintId = in.readString();
        complaintCategory = in.readString();
        selectedComplaintImageUri = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        selectedTemplateId = in.readString();
        selectedTemplateString = in.readString();
        citizenComplaintTemplates = new ArrayList<CitizenComplaintTemplate>();
        in.readList(citizenComplaintTemplates, CitizenComplaint.class.getClassLoader());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CitizenComplaint createFromParcel(Parcel in) {
            return new CitizenComplaint(in);
        }

        public CitizenComplaint[] newArray(int size) {
            return new CitizenComplaint[size];
        }
    };
}
