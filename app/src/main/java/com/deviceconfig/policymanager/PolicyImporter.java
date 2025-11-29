package com.deviceconfig.policymanager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.UserManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

public class PolicyImporter {
    private static final String TAG = "PolicyImporter";
    
    private Context context;
    private PolicyManager policyManager;
    
    public PolicyImporter(Context context) {
        this.context = context;
        this.policyManager = new PolicyManager(context);
    }
    
    public boolean importAndApplyPolicies() {
        try {
            String configPath = PolicyExporter.getSharedConfigPath();
            File configFile = new File(configPath);
            
            if (!configFile.exists()) {
                Log.w(TAG, "Config file not found: " + configPath);
                return false;
            }
            
            // Read JSON file
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();
            
            JSONObject root = new JSONObject(jsonBuilder.toString());
            
            // Apply device policies
            if (root.has("devicePolicies")) {
                applyDevicePolicies(root.getJSONObject("devicePolicies"));
            }
            
            // Apply user restrictions
            if (root.has("userRestrictions")) {
                applyUserRestrictions(root.getJSONObject("userRestrictions"));
            }
            
            // Apply uninstall blocks
            if (root.has("uninstallBlockedPackages")) {
                applyUninstallBlocks(root.getJSONArray("uninstallBlockedPackages"));
            }
            
            Log.i(TAG, "Successfully imported and applied policies");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error importing policies", e);
            return false;
        }
    }
    
    private void applyDevicePolicies(JSONObject policies) {
        try {
            if (policies.has("cameraDisabled")) {
                policyManager.setDisableCamera(policies.getBoolean("cameraDisabled"));
            }
            if (policies.has("screenCaptureDisabled")) {
                policyManager.setDisableScreenCapture(policies.getBoolean("screenCaptureDisabled"));
            }
            if (policies.has("networkLoggingEnabled")) {
                policyManager.setNetworkLoggingEnabled(policies.getBoolean("networkLoggingEnabled"));
            }
            if (policies.has("crossProfileContactsDisabled")) {
                policyManager.setCrossProfileContactsDisabled(policies.getBoolean("crossProfileContactsDisabled"));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error applying device policies", e);
        }
    }
    
    private void applyUserRestrictions(JSONObject restrictions) {
        try {
            Iterator<String> keys = restrictions.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                boolean value = restrictions.getBoolean(key);
                
                // Map JSON keys to UserManager constants
                String restriction = mapToUserManagerConstant(key);
                if (restriction != null) {
                    policyManager.setUserRestriction(restriction, value);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error applying user restrictions", e);
        }
    }
    
    private void applyUninstallBlocks(JSONArray blockedPackages) {
        try {
            // First, get all currently blocked packages and unblock them
            PackageManager pm = context.getPackageManager();
            for (PackageInfo pkg : pm.getInstalledPackages(0)) {
                if (policyManager.isUninstallBlocked(pkg.packageName)) {
                    policyManager.setUninstallBlocked(pkg.packageName, false);
                }
            }
            
            // Then apply blocks from config
            for (int i = 0; i < blockedPackages.length(); i++) {
                String packageName = blockedPackages.getString(i);
                policyManager.setUninstallBlocked(packageName, true);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error applying uninstall blocks", e);
        }
    }
    
    private String mapToUserManagerConstant(String jsonKey) {
        switch (jsonKey) {
            case "noConfigWifi": return UserManager.DISALLOW_CONFIG_WIFI;
            case "noChangeWifiState": return UserManager.DISALLOW_CHANGE_WIFI_STATE;
            case "noAirplaneMode": return UserManager.DISALLOW_AIRPLANE_MODE;
            case "noCellular2g": return UserManager.DISALLOW_CELLULAR_2G;
            case "disallowDebugging": return UserManager.DISALLOW_DEBUGGING_FEATURES;
            case "noPrinting": return UserManager.DISALLOW_PRINTING;
            case "disallowBluetooth": return UserManager.DISALLOW_BLUETOOTH;
            case "disallowConfigBrightness": return UserManager.DISALLOW_CONFIG_BRIGHTNESS;
            case "disallowConfigCellBroadcasts": return UserManager.DISALLOW_CONFIG_CELL_BROADCASTS;
            case "disallowConfigLocale": return UserManager.DISALLOW_CONFIG_LOCALE;
            case "disallowConfigDateTime": return UserManager.DISALLOW_CONFIG_DATE_TIME;
            case "disallowConfigDefaultApps": return UserManager.DISALLOW_CONFIG_DEFAULT_APPS;
            case "disallowConfigCredentials": return UserManager.DISALLOW_CONFIG_CREDENTIALS;
            case "disallowConfigTethering": return UserManager.DISALLOW_CONFIG_TETHERING;
            case "disallowConfigMobileNetworks": return UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS;
            case "disallowConfigPrivateDns": return UserManager.DISALLOW_CONFIG_PRIVATE_DNS;
            case "disallowInstallApps": return UserManager.DISALLOW_INSTALL_APPS;
            case "disallowOutgoingBeam": return UserManager.DISALLOW_OUTGOING_BEAM;
            case "disallowNfc": return "no_nfc";
            case "disallowWifiTethering": return UserManager.DISALLOW_WIFI_TETHERING;
            case "disallowMountPhysicalMedia": return UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA;
            case "disallowUsbFileTransfer": return UserManager.DISALLOW_USB_FILE_TRANSFER;
            default: return null;
        }
    }
}
