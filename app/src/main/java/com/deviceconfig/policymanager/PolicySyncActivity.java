package com.deviceconfig.policymanager;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class PolicySyncActivity extends PolicyDetailActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sync Policies Across Profiles");
        }
    }
    
    @Override
    protected void buildPreferences(String category) {
        // Check if device owner
        if (!policyManager.isDeviceOwner()) {
            addInfoMessage("Device Owner privileges required to sync policies across profiles");
            return;
        }
        
        addInfoMessage("This will apply all current policies from the primary user to all other user profiles on the device.");
        
        // Show current profiles
        addHeaderMessage("Available Profiles");
        List<UserHandle> profiles = policyManager.getUserProfiles();
        if (profiles != null && !profiles.isEmpty()) {
            for (UserHandle handle : profiles) {
                String userInfo = getUserInfo(handle);
                addInfoMessage("• " + userInfo);
            }
        } else {
            addInfoMessage("No additional profiles found");
        }
        
        // Sync button
        addClickablePreference(
            "Sync All Policies",
            "Apply current policies to all profiles",
            v -> syncPoliciesAcrossProfiles()
        );
        
        // Individual sync options
        addHeaderMessage("Sync Individual Policy Types");
        
        addClickablePreference(
            "Sync User Restrictions",
            "Apply user restrictions to all profiles",
            v -> syncUserRestrictions()
        );
        
        addClickablePreference(
            "Sync Device Policies",
            "Apply device policies to all profiles",
            v -> syncDevicePolicies()
        );
        
        addClickablePreference(
            "Sync Uninstall Blocks",
            "Apply uninstall blocks to all profiles",
            v -> syncUninstallBlocks()
        );
    }
    
    private void addInfoMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(32, 16, 32, 16);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        preferenceContainer.addView(textView);
    }
    
    private void addHeaderMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(32, 24, 32, 8);
        textView.setTextSize(16);
        textView.setTextAppearance(android.R.style.TextAppearance_Material_Subhead);
        preferenceContainer.addView(textView);
    }
    
    private void addClickablePreference(String title, String summary, View.OnClickListener listener) {
        View view = addSwitchPreference(title, summary, false, null);
        view.setClickable(true);
        view.setOnClickListener(listener);
        // Hide the switch widget
        View switchWidget = view.findViewById(R.id.switchWidget);
        if (switchWidget != null) {
            switchWidget.setVisibility(View.GONE);
        }
    }
    
    private String getUserInfo(UserHandle handle) {
        try {
            java.lang.reflect.Method method = UserHandle.class.getMethod("getIdentifier");
            int userId = (Integer) method.invoke(handle);
            return "User ID: " + userId + (userId == 0 ? " (Primary)" : "");
        } catch (Exception e) {
            return "User Profile";
        }
    }
    
    private void syncPoliciesAcrossProfiles() {
        showToast("Starting full policy sync...");
        
        int successCount = 0;
        int failCount = 0;
        
        // Sync user restrictions
        if (syncUserRestrictionsInternal()) successCount++;
        else failCount++;
        
        // Sync device policies
        if (syncDevicePoliciesInternal()) successCount++;
        else failCount++;
        
        // Sync uninstall blocks
        if (syncUninstallBlocksInternal()) successCount++;
        else failCount++;
        
        String message = "Sync complete: " + successCount + " succeeded";
        if (failCount > 0) {
            message += ", " + failCount + " failed";
        }
        showToast(message);
    }
    
    private void syncUserRestrictions() {
        boolean success = syncUserRestrictionsInternal();
        showToast(success ? "User restrictions synced successfully" : "Failed to sync user restrictions");
    }
    
    private void syncDevicePolicies() {
        boolean success = syncDevicePoliciesInternal();
        showToast(success ? "Device policies synced successfully" : "Failed to sync device policies");
    }
    
    private void syncUninstallBlocks() {
        boolean success = syncUninstallBlocksInternal();
        showToast(success ? "Uninstall blocks synced successfully" : "Failed to sync uninstall blocks");
    }
    
    private boolean syncUserRestrictionsInternal() {
        try {
            // Get current user restrictions
            UserManager um = (UserManager) getSystemService(Context.USER_SERVICE);
            
            // List of restrictions to sync
            String[] restrictions = {
                UserManager.DISALLOW_CONFIG_WIFI,
                UserManager.DISALLOW_CHANGE_WIFI_STATE,
                UserManager.DISALLOW_AIRPLANE_MODE,
                UserManager.DISALLOW_CELLULAR_2G,
                UserManager.DISALLOW_DEBUGGING_FEATURES,
                UserManager.DISALLOW_PRINTING,
                UserManager.DISALLOW_BLUETOOTH,
                UserManager.DISALLOW_CONFIG_BRIGHTNESS,
                UserManager.DISALLOW_CONFIG_CELL_BROADCASTS,
                UserManager.DISALLOW_CONFIG_LOCALE,
                UserManager.DISALLOW_CONFIG_DATE_TIME,
                UserManager.DISALLOW_CONFIG_DEFAULT_APPS,
                UserManager.DISALLOW_CONFIG_CREDENTIALS,
                UserManager.DISALLOW_CONFIG_TETHERING,
                UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS,
                UserManager.DISALLOW_CONFIG_PRIVATE_DNS,
                UserManager.DISALLOW_INSTALL_APPS,
                UserManager.DISALLOW_OUTGOING_BEAM,
                UserManager.DISALLOW_WIFI_TETHERING,
                UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA,
                UserManager.DISALLOW_USB_FILE_TRANSFER
            };
            
            // Apply each restriction based on current state
            for (String restriction : restrictions) {
                boolean isRestricted = um.hasUserRestriction(restriction);
                policyManager.setUserRestriction(restriction, isRestricted);
            }
            
            return true;
        } catch (Exception e) {
            android.util.Log.e("PolicySyncActivity", "Error syncing user restrictions", e);
            return false;
        }
    }
    
    private boolean syncDevicePoliciesInternal() {
        try {
            // Sync camera policy
            boolean cameraDisabled = policyManager.getDisableCamera();
            policyManager.setDisableCamera(cameraDisabled);
            
            // Sync screen capture policy
            boolean screenCaptureDisabled = policyManager.getDisableScreenCapture();
            policyManager.setDisableScreenCapture(screenCaptureDisabled);
            
            // Sync network logging
            boolean networkLogging = policyManager.getNetworkLoggingEnabled();
            policyManager.setNetworkLoggingEnabled(networkLogging);
            
            // Sync cross profile contacts
            boolean crossProfileDisabled = policyManager.getCrossProfileContactsDisabled();
            policyManager.setCrossProfileContactsDisabled(crossProfileDisabled);
            
            return true;
        } catch (Exception e) {
            android.util.Log.e("PolicySyncActivity", "Error syncing device policies", e);
            return false;
        }
    }
    
    private boolean syncUninstallBlocksInternal() {
        try {
            android.content.pm.PackageManager pm = getPackageManager();
            java.util.List<android.content.pm.PackageInfo> packages = pm.getInstalledPackages(0);
            
            int syncCount = 0;
            for (android.content.pm.PackageInfo pkg : packages) {
                boolean isBlocked = policyManager.isUninstallBlocked(pkg.packageName);
                if (isBlocked) {
                    // Re-apply the block to ensure it's synced
                    policyManager.setUninstallBlocked(pkg.packageName, true);
                    syncCount++;
                }
            }
            
            android.util.Log.i("PolicySyncActivity", "Synced " + syncCount + " uninstall blocks");
            return true;
        } catch (Exception e) {
            android.util.Log.e("PolicySyncActivity", "Error syncing uninstall blocks", e);
            return false;
        }
    }
}
