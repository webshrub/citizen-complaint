package com.webshrub.citizencomplaint.androidapp;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

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
    public static final String CITIZEN_COMPLAINT_APPLICATION_LOG_TAG = "CitizenComplaintApplication";

    public static void initImageLoader(Context context) {
        int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        MemoryCacheAware<String, Bitmap> memoryCache;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            memoryCache = new LruMemoryCache(memoryCacheSize);
        } else {
            memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
        }
        // This configuration tuning is custom. You can tune every option, you may tune some of them, or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this); method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(memoryCache)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .enableLogging() // Not necessary in common
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onCreate() {
        if (CitizenComplaintConstants.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
        }
        super.onCreate();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(new CitizenComplaintPostDetailsTask(), 1, 2 * 60, TimeUnit.SECONDS);
        initImageLoader(getApplicationContext());
    }

    private class CitizenComplaintPostDetailsTask implements Runnable {
        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (CitizenComplaintUtility.isDeviceOnline(getApplicationContext())) {
                for (CitizenComplaint citizenComplaint : CitizenComplaintDataSource.getInstance(getApplicationContext()).getAllCitizenComplaints()) {
                    CitizenComplaintHttpUtil.postComplaintDetails(getApplicationContext(), citizenComplaint);
                }
            } else {
                Log.w(CITIZEN_COMPLAINT_APPLICATION_LOG_TAG, "Not connected to internet.");
            }
        }
    }
}
