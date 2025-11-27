package com.deviceconfig.policymanager;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UninstallBlockedActivity extends PolicyDetailActivity {
    
    private List<PackageInfo> installedPackages;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Block Uninstall");
        }
    }
    
    @Override
    protected void buildPreferences(String category) {
        // Check if device owner
        if (!policyManager.isDeviceOwner()) {
            addInfoMessage("Device Owner privileges required to manage uninstall blocking");
            return;
        }
        
        // Add info message
        addInfoMessage("Select apps to prevent uninstallation. Users will not be able to uninstall blocked apps.");
        
        // Get all installed packages
        PackageManager pm = getPackageManager();
        installedPackages = pm.getInstalledPackages(0);
        
        // Sort packages by app name
        Collections.sort(installedPackages, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo p1, PackageInfo p2) {
                String name1 = getAppName(p1);
                String name2 = getAppName(p2);
                return name1.compareToIgnoreCase(name2);
            }
        });
        
        // Add checkbox for each package
        for (PackageInfo packageInfo : installedPackages) {
            addPackageCheckbox(packageInfo);
        }
    }
    
    private void addInfoMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(32, 24, 32, 24);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        preferenceContainer.addView(textView);
    }
    
    private String getAppName(PackageInfo packageInfo) {
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            CharSequence label = pm.getApplicationLabel(appInfo);
            return label != null ? label.toString() : packageInfo.packageName;
        } catch (Exception e) {
            return packageInfo.packageName;
        }
    }
    
    private void addPackageCheckbox(PackageInfo packageInfo) {
        String packageName = packageInfo.packageName;
        String appName = getAppName(packageInfo);
        
        // Check if uninstall is currently blocked
        boolean isBlocked = isUninstallBlocked(packageName);
        
        // Create switch preference
        View view = addSwitchPreference(
            appName,
            packageName,
            isBlocked,
            v -> {
                Switch switchWidget = v.findViewById(R.id.switchWidget);
                boolean blocked = switchWidget.isChecked();
                boolean success = policyManager.setUninstallBlocked(packageName, blocked);
                
                if (success) {
                    showToast((blocked ? "Blocked" : "Unblocked") + " uninstall for " + appName);
                } else {
                    // Revert switch state on failure
                    switchWidget.setChecked(!blocked);
                    showToast("Failed to update uninstall policy for " + appName);
                }
            }
        );
    }
    
    private boolean isUninstallBlocked(String packageName) {
        return policyManager.isUninstallBlocked(packageName);
    }
}
