package com.webshrub.citizencomplaint.androidapp;

import android.app.Application;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Ahsan.Javed
 * Date: 5/7/13
 * Time: 9:38 PM
 */
public class CitizenComplaintApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(new CitizenComplaintPostDetailsTask(), 1, 2 * 60, TimeUnit.SECONDS);
    }

    private class CitizenComplaintPostDetailsTask implements Runnable {
        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            for (CitizenComplaint citizenComplaint : CitizenComplaintDataSource.getInstance(getApplicationContext()).getAllCitizenComplaints()) {
                CitizenComplaintHttpUtil.postComplaintDetails(getApplicationContext(), citizenComplaint);
            }
        }
    }
}
