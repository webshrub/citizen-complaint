package com.webshrub.citizencomplaint.androidapp;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ahsan.Javed
 * Date: 4/25/13
 * Time: 1:45 PM
 */
public class CitizenComplaintManager {
    private List<CitizenComplaint> citizenComplaints;

    public CitizenComplaintManager(Context context) {
        citizenComplaints = new ArrayList<CitizenComplaint>();
        String[] complaintCategories = context.getResources().getStringArray(R.array.basic_items);
        String[] complaintIds = context.getResources().getStringArray(R.array.basic_items_index);
        for (int i = 0, complaintCategoriesLength = complaintCategories.length; i < complaintCategoriesLength; i++) {
            int complaintId = Integer.parseInt(complaintIds[i]);
            CitizenComplaint citizenComplaint = new CitizenComplaint();
            citizenComplaint.setComplaintId(complaintIds[i]);
            citizenComplaint.setComplaintCategory(complaintCategories[i]);
            int templateStringResourceForComplaint = getTemplateStringResourceForComplaint(complaintId);
            int templateIdResourceForComplaint = getTemplateIdResourceForComplaint(complaintId);
            String[] templateIds = context.getResources().getStringArray(templateIdResourceForComplaint);
            String[] templateStrings = context.getResources().getStringArray(templateStringResourceForComplaint);
            for (int j = 0, templateStringsLength = templateStrings.length; j < templateStringsLength; j++) {
                String templateIndex = templateIds[j];
                String templateString = templateStrings[j];
                CitizenComplaintTemplate citizenComplaintTemplate = new CitizenComplaintTemplate(templateIndex, templateString);
                citizenComplaint.getCitizenComplaintTemplates().add(citizenComplaintTemplate);
            }
            citizenComplaints.add(citizenComplaint);
        }
    }

    public List<CitizenComplaint> getCitizenComplaints() {
        return citizenComplaints;
    }

    private int getTemplateStringResourceForComplaint(int complaintId) {
        int resourceId = 0;
        switch (complaintId) {
            case 48:
                resourceId = R.array.water;
                break;
            case 53:
                resourceId = R.array.lawandorder;
                break;
            case 49:
                resourceId = R.array.electricity;
                break;
            case 52:
                resourceId = R.array.transportation;
                break;
            case 51:
                resourceId = R.array.road;
                break;
            case 50:
                resourceId = R.array.sewage;
                break;
        }
        return resourceId;
    }

    private int getTemplateIdResourceForComplaint(int complaintId) {
        int resourceId = 0;
        switch (complaintId) {
            case 48:
                resourceId = R.array.water_index;
                break;
            case 53:
                resourceId = R.array.lawandorder_index;
                break;
            case 49:
                resourceId = R.array.electricity_index;
                break;
            case 52:
                resourceId = R.array.transportation_index;
                break;
            case 51:
                resourceId = R.array.road_index;
                break;
            case 50:
                resourceId = R.array.sewage_index;
                break;
        }
        return resourceId;
    }
}
