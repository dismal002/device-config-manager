package com.deviceconfig.policymanager;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class PolicyExportService extends Service {
    private static final String TAG = "PolicyExportService";
    private static final long EXPORT_INTERVAL = 15 * 60 * 1000; // 15 minutes in milliseconds
    
    private Handler handler;
    private Runnable exportRunnable;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "PolicyExportService created");
        
        handler = new Handler();
        exportRunnable = new Runnable() {
            @Override
            public void run() {
                exportPolicyState();
                // Schedule next export
                handler.postDelayed(this, EXPORT_INTERVAL);
            }
        };
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "PolicyExportService started");
        
        // Do initial export
        exportPolicyState();
        
        // Schedule periodic exports
        handler.postDelayed(exportRunnable, EXPORT_INTERVAL);
        
        // Restart service if killed
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "PolicyExportService destroyed");
        
        // Cancel scheduled exports
        if (handler != null && exportRunnable != null) {
            handler.removeCallbacks(exportRunnable);
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private void exportPolicyState() {
        try {
            PolicyExporter exporter = new PolicyExporter(this);
            boolean success = exporter.exportPolicyState();
            Log.i(TAG, "Automatic policy export " + (success ? "succeeded" : "failed"));
        } catch (Exception e) {
            Log.e(TAG, "Error during automatic policy export", e);
        }
    }
}
