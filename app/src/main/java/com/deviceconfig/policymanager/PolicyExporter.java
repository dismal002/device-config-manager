package com.deviceconfig.policymanager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.UserManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class PolicyExporter {
    private static final String TAG = "PolicyExporter";
    private static final String EXPORT_FILE = "policy_state.json";
    
    private Context context;
    private PolicyManager policyManager;
    
    public PolicyExporter(Context context) {
        this.context = context;
        this.policyManager = new PolicyManager(context);
    }
    
    public boolean exportPolicyState() {
        try {
            JSONObject root = new JSONObject();
            
            // Admin status
            JSONObject adminStatus = new JSONObject();
            adminStatus.put("isAdminActive", policyManager.isAdminActive());
            adminStatus.put("isDeviceOwner", policyManager.isDeviceOwner());
            root.put("adminStatus", adminStatus);
            
            // Device policies
            JSONObject devicePolicies = new JSONObject();
            devicePolicies.put("cameraDisabled", policyManager.getDisableCamera());
            devicePolicies.put("screenCaptureDisabled", policyManager.getDisableScreenCapture());
            devicePolicies.put("securityLoggingEnabled", policyManager.isSecurityLoggingEnabled());
            devicePolicies.put("networkLoggingEnabled", policyManager.getNetworkLoggingEnabled());
            devicePolicies.put("organizationName", policyManager.getOrganizationName());
            devicePolicies.put("lockScreenInfo", policyManager.getLockScreenInfo());
            devicePolicies.put("crossProfileContactsDisabled", policyManager.getCrossProfileContactsDisabled());
            devicePolicies.put("forceEphemeralUsers", policyManager.getForceEphemeralUsers());
            root.put("devicePolicies", devicePolicies);
            
            // User restrictions
            JSONObject userRestrictions = new JSONObject();
            userRestrictions.put("noConfigWifi", policyManager.getNoConfigWifi());
            userRestrictions.put("noChangeWifiState", policyManager.getNoChangeWifiState());
            userRestrictions.put("noAirplaneMode", policyManager.getNoAirplaneMode());
            userRestrictions.put("noCellular2g", policyManager.getNoCellular2g());
            userRestrictions.put("disallowDebugging", policyManager.getDisallowDebugging());
            userRestrictions.put("noPrinting", policyManager.getNoPrinting());
            userRestrictions.put("disallowBluetooth", policyManager.getDisallowBluetooth());
            userRestrictions.put("disallowConfigBrightness", policyManager.getDisallowConfigBrightness());
            userRestrictions.put("disallowConfigCellBroadcasts", policyManager.getDisallowConfigCellBroadcasts());
            userRestrictions.put("disallowConfigLocale", policyManager.getDisallowConfigLocale());
            userRestrictions.put("disallowConfigDateTime", policyManager.getDisallowConfigDateTime());
            userRestrictions.put("disallowConfigDefaultApps", policyManager.getDisallowConfigDefaultApps());
            userRestrictions.put("disallowConfigCredentials", policyManager.getDisallowConfigCredentials());
            userRestrictions.put("disallowConfigTethering", policyManager.getDisallowConfigTethering());
            userRestrictions.put("disallowConfigMobileNetworks", policyManager.getDisallowConfigMobileNetworks());
            userRestrictions.put("disallowConfigPrivateDns", policyManager.getDisallowConfigPrivateDns());
            userRestrictions.put("disallowInstallApps", policyManager.getDisallowInstallApps());
            userRestrictions.put("disallowOutgoingBeam", policyManager.getDisallowOutgoingBeam());
            userRestrictions.put("disallowNfc", policyManager.getDisallowNfc());
            userRestrictions.put("disallowWifiTethering", policyManager.getDisallowWifiTethering());
            userRestrictions.put("disallowMountPhysicalMedia", policyManager.getDisallowMountPhysicalMedia());
            userRestrictions.put("disallowUsbFileTransfer", policyManager.getDisallowUsbFileTransfer());
            root.put("userRestrictions", userRestrictions);
            
            // Uninstall blocked packages
            JSONArray blockedPackages = new JSONArray();
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packages = pm.getInstalledPackages(0);
            for (PackageInfo pkg : packages) {
                if (policyManager.isUninstallBlocked(pkg.packageName)) {
                    blockedPackages.put(pkg.packageName);
                }
            }
            root.put("uninstallBlockedPackages", blockedPackages);
            
            // Timestamp
            root.put("exportTimestamp", System.currentTimeMillis());
            root.put("exportDate", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            
            // Write to file
            File dataDir = context.getFilesDir();
            File exportFile = new File(dataDir, EXPORT_FILE);
            
            FileWriter writer = new FileWriter(exportFile);
            writer.write(root.toString(2)); // Pretty print with 2-space indent
            writer.close();
            
            Log.i(TAG, "Policy state exported to: " + exportFile.getAbsolutePath());
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error exporting policy state", e);
            return false;
        }
    }
    
    public String getExportFilePath() {
        return new File(context.getFilesDir(), EXPORT_FILE).getAbsolutePath();
    }
}
