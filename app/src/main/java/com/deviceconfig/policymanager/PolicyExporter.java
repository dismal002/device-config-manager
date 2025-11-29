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
    private static final String SHARED_DIR = "/data/data/com.deviceconfig.policymanager";
    
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
            devicePolicies.put("cameraDisabled", safeGetBoolean(() -> policyManager.getDisableCamera()));
            devicePolicies.put("screenCaptureDisabled", safeGetBoolean(() -> policyManager.getDisableScreenCapture()));
            devicePolicies.put("securityLoggingEnabled", safeGetBoolean(() -> policyManager.isSecurityLoggingEnabled()));
            devicePolicies.put("networkLoggingEnabled", safeGetBoolean(() -> policyManager.getNetworkLoggingEnabled()));
            devicePolicies.put("organizationName", safeGetString(() -> policyManager.getOrganizationName()));
            devicePolicies.put("lockScreenInfo", safeGetString(() -> policyManager.getLockScreenInfo()));
            devicePolicies.put("crossProfileContactsDisabled", safeGetBoolean(() -> policyManager.getCrossProfileContactsDisabled()));
            devicePolicies.put("forceEphemeralUsers", safeGetBoolean(() -> policyManager.getForceEphemeralUsers()));
            devicePolicies.put("guestUserDisabled", safeGetBoolean(() -> policyManager.getGuestUserDisabled()));
            root.put("devicePolicies", devicePolicies);
            
            // System Update Policy
            JSONObject systemUpdatePolicy = new JSONObject();
            try {
                android.app.admin.DevicePolicyManager dpm = (android.app.admin.DevicePolicyManager) 
                    context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                android.content.ComponentName adminComponent = new android.content.ComponentName(
                    context, DeviceAdminReceiver.class);
                
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    android.app.admin.SystemUpdatePolicy policy = dpm.getSystemUpdatePolicy();
                    if (policy != null) {
                        int policyType = policy.getPolicyType();
                        systemUpdatePolicy.put("policyType", policyType);
                        
                        String policyTypeName = "UNKNOWN";
                        switch (policyType) {
                            case android.app.admin.SystemUpdatePolicy.TYPE_INSTALL_AUTOMATIC:
                                policyTypeName = "AUTOMATIC";
                                break;
                            case android.app.admin.SystemUpdatePolicy.TYPE_INSTALL_WINDOWED:
                                policyTypeName = "WINDOWED";
                                break;
                            case android.app.admin.SystemUpdatePolicy.TYPE_POSTPONE:
                                policyTypeName = "POSTPONE";
                                break;
                        }
                        systemUpdatePolicy.put("policyTypeName", policyTypeName);
                        systemUpdatePolicy.put("isConfigured", true);
                    } else {
                        systemUpdatePolicy.put("policyType", -1);
                        systemUpdatePolicy.put("policyTypeName", "NOT_SET");
                        systemUpdatePolicy.put("isConfigured", false);
                    }
                } else {
                    systemUpdatePolicy.put("policyType", -1);
                    systemUpdatePolicy.put("policyTypeName", "NOT_SUPPORTED");
                    systemUpdatePolicy.put("isConfigured", false);
                }
            } catch (Exception e) {
                android.util.Log.e(TAG, "Error getting system update policy", e);
                systemUpdatePolicy.put("policyType", -1);
                systemUpdatePolicy.put("policyTypeName", "ERROR");
                systemUpdatePolicy.put("isConfigured", false);
            }
            root.put("systemUpdatePolicy", systemUpdatePolicy);
            
            // Password policies (comprehensive)
            JSONObject passwordPolicies = new JSONObject();
            android.app.admin.DevicePolicyManager dpm = (android.app.admin.DevicePolicyManager) 
                context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            android.content.ComponentName adminComponent = new android.content.ComponentName(
                context, DeviceAdminReceiver.class);
            
            // Password expiration
            try {
                passwordPolicies.put("passwordExpiration", policyManager.getPasswordExpiration());
                passwordPolicies.put("passwordExpirationTimeout", dpm.getPasswordExpirationTimeout(adminComponent));
            } catch (Exception e) {
                passwordPolicies.put("passwordExpiration", 0);
                passwordPolicies.put("passwordExpirationTimeout", 0);
            }
            
            // Password quality
            try {
                int quality = dpm.getPasswordQuality(adminComponent);
                passwordPolicies.put("passwordQuality", quality);
                String qualityName = "UNSPECIFIED";
                switch (quality) {
                    case android.app.admin.DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED:
                        qualityName = "UNSPECIFIED";
                        break;
                    case android.app.admin.DevicePolicyManager.PASSWORD_QUALITY_SOMETHING:
                        qualityName = "SOMETHING";
                        break;
                    case android.app.admin.DevicePolicyManager.PASSWORD_QUALITY_NUMERIC:
                        qualityName = "NUMERIC";
                        break;
                    case android.app.admin.DevicePolicyManager.PASSWORD_QUALITY_NUMERIC_COMPLEX:
                        qualityName = "NUMERIC_COMPLEX";
                        break;
                    case android.app.admin.DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC:
                        qualityName = "ALPHABETIC";
                        break;
                    case android.app.admin.DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC:
                        qualityName = "ALPHANUMERIC";
                        break;
                    case android.app.admin.DevicePolicyManager.PASSWORD_QUALITY_COMPLEX:
                        qualityName = "COMPLEX";
                        break;
                }
                passwordPolicies.put("passwordQualityName", qualityName);
            } catch (Exception e) {
                passwordPolicies.put("passwordQuality", 0);
                passwordPolicies.put("passwordQualityName", "UNSPECIFIED");
            }
            
            // Password minimum length
            try {
                passwordPolicies.put("passwordMinimumLength", dpm.getPasswordMinimumLength(adminComponent));
            } catch (Exception e) {
                passwordPolicies.put("passwordMinimumLength", 0);
            }
            
            // Password minimum letters
            try {
                passwordPolicies.put("passwordMinimumLetters", dpm.getPasswordMinimumLetters(adminComponent));
            } catch (Exception e) {
                passwordPolicies.put("passwordMinimumLetters", 0);
            }
            
            // Password minimum lowercase
            try {
                passwordPolicies.put("passwordMinimumLowerCase", dpm.getPasswordMinimumLowerCase(adminComponent));
            } catch (Exception e) {
                passwordPolicies.put("passwordMinimumLowerCase", 0);
            }
            
            // Password minimum uppercase
            try {
                passwordPolicies.put("passwordMinimumUpperCase", dpm.getPasswordMinimumUpperCase(adminComponent));
            } catch (Exception e) {
                passwordPolicies.put("passwordMinimumUpperCase", 0);
            }
            
            // Password minimum numeric
            try {
                passwordPolicies.put("passwordMinimumNumeric", dpm.getPasswordMinimumNumeric(adminComponent));
            } catch (Exception e) {
                passwordPolicies.put("passwordMinimumNumeric", 0);
            }
            
            // Password minimum symbols
            try {
                passwordPolicies.put("passwordMinimumSymbols", dpm.getPasswordMinimumSymbols(adminComponent));
            } catch (Exception e) {
                passwordPolicies.put("passwordMinimumSymbols", 0);
            }
            
            // Password minimum non-letter
            try {
                passwordPolicies.put("passwordMinimumNonLetter", dpm.getPasswordMinimumNonLetter(adminComponent));
            } catch (Exception e) {
                passwordPolicies.put("passwordMinimumNonLetter", 0);
            }
            
            // Password history length
            try {
                passwordPolicies.put("passwordHistoryLength", dpm.getPasswordHistoryLength(adminComponent));
            } catch (Exception e) {
                passwordPolicies.put("passwordHistoryLength", 0);
            }
            
            // Maximum failed password attempts before wipe
            try {
                passwordPolicies.put("maximumFailedPasswordsForWipe", dpm.getMaximumFailedPasswordsForWipe(adminComponent));
            } catch (Exception e) {
                passwordPolicies.put("maximumFailedPasswordsForWipe", 0);
            }
            
            // Maximum time to lock
            try {
                passwordPolicies.put("maximumTimeToLock", dpm.getMaximumTimeToLock(adminComponent));
            } catch (Exception e) {
                passwordPolicies.put("maximumTimeToLock", 0);
            }
            
            // Password sufficient
            try {
                passwordPolicies.put("isActivePasswordSufficient", dpm.isActivePasswordSufficient());
            } catch (Exception e) {
                passwordPolicies.put("isActivePasswordSufficient", false);
            }
            
            // Current failed password attempts
            try {
                passwordPolicies.put("currentFailedPasswordAttempts", dpm.getCurrentFailedPasswordAttempts());
            } catch (Exception e) {
                passwordPolicies.put("currentFailedPasswordAttempts", 0);
            }
            
            root.put("passwordPolicies", passwordPolicies);
            
            // Additional User Restrictions (comprehensive)
            JSONObject additionalRestrictions = new JSONObject();
            // Factory Reset & System
            additionalRestrictions.put("disallowFactoryReset", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_FACTORY_RESET);
            }));
            additionalRestrictions.put("disallowAddUser", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_ADD_USER);
            }));
            additionalRestrictions.put("disallowRemoveUser", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_REMOVE_USER);
            }));
            additionalRestrictions.put("disallowModifyAccounts", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_MODIFY_ACCOUNTS);
            }));
            additionalRestrictions.put("disallowUninstallApps", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_UNINSTALL_APPS);
            }));
            additionalRestrictions.put("disallowAppsControl", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_APPS_CONTROL);
            }));
            additionalRestrictions.put("disallowShareLocation", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_SHARE_LOCATION);
            }));
            additionalRestrictions.put("disallowConfigScreenTimeout", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_CONFIG_SCREEN_TIMEOUT);
            }));
            additionalRestrictions.put("disallowInstallUnknownSources", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES);
            }));
            additionalRestrictions.put("disallowInstallUnknownSourcesGlobally", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES_GLOBALLY);
            }));
            additionalRestrictions.put("disallowSetWallpaper", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_SET_WALLPAPER);
            }));
            additionalRestrictions.put("disallowSetUserIcon", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_SET_USER_ICON);
            }));
            additionalRestrictions.put("disallowAdjustVolume", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME);
            }));
            additionalRestrictions.put("disallowUnmuteMicrophone", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_UNMUTE_MICROPHONE);
            }));
            additionalRestrictions.put("disallowOutgoingCalls", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_OUTGOING_CALLS);
            }));
            additionalRestrictions.put("disallowSms", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_SMS);
            }));
            additionalRestrictions.put("disallowFun", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_FUN);
            }));
            additionalRestrictions.put("disallowCreateWindows", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_CREATE_WINDOWS);
            }));
            additionalRestrictions.put("disallowCrossProfileCopyPaste", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_CROSS_PROFILE_COPY_PASTE);
            }));
            additionalRestrictions.put("disallowDataRoaming", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_DATA_ROAMING);
            }));
            additionalRestrictions.put("disallowSafeBootMode", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_SAFE_BOOT);
            }));
            additionalRestrictions.put("disallowNetworkReset", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_NETWORK_RESET);
            }));
            additionalRestrictions.put("disallowConfigVpn", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_CONFIG_VPN);
            }));
            additionalRestrictions.put("disallowAutofill", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_AUTOFILL);
            }));
            additionalRestrictions.put("disallowContentCapture", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_CONTENT_CAPTURE);
            }));
            additionalRestrictions.put("disallowContentSuggestions", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_CONTENT_SUGGESTIONS);
            }));
            additionalRestrictions.put("disallowUserSwitching", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_USER_SWITCH);
            }));
            additionalRestrictions.put("disallowAmbientDisplay", safeGetBoolean(() -> {
                UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                return um.hasUserRestriction(UserManager.DISALLOW_AMBIENT_DISPLAY);
            }));
            root.put("additionalRestrictions", additionalRestrictions);
            
            // User restrictions - Comprehensive list
            JSONObject userRestrictions = new JSONObject();
            // Wireless & Network
            userRestrictions.put("noConfigWifi", safeGetBoolean(() -> policyManager.getNoConfigWifi()));
            userRestrictions.put("noChangeWifiState", safeGetBoolean(() -> policyManager.getNoChangeWifiState()));
            userRestrictions.put("noAirplaneMode", safeGetBoolean(() -> policyManager.getNoAirplaneMode()));
            userRestrictions.put("noCellular2g", safeGetBoolean(() -> policyManager.getNoCellular2g()));
            userRestrictions.put("disallowWifiTethering", safeGetBoolean(() -> policyManager.getDisallowWifiTethering()));
            userRestrictions.put("disallowConfigMobileNetworks", safeGetBoolean(() -> policyManager.getDisallowConfigMobileNetworks()));
            userRestrictions.put("disallowConfigPrivateDns", safeGetBoolean(() -> policyManager.getDisallowConfigPrivateDns()));
            userRestrictions.put("disallowConfigTethering", safeGetBoolean(() -> policyManager.getDisallowConfigTethering()));
            
            // Apps & Installation
            userRestrictions.put("disallowInstallApps", safeGetBoolean(() -> policyManager.getDisallowInstallApps()));
            userRestrictions.put("disallowConfigDefaultApps", safeGetBoolean(() -> policyManager.getDisallowConfigDefaultApps()));
            
            // Device Features
            userRestrictions.put("disallowDebugging", safeGetBoolean(() -> policyManager.getDisallowDebugging()));
            userRestrictions.put("noPrinting", safeGetBoolean(() -> policyManager.getNoPrinting()));
            userRestrictions.put("disallowBluetooth", safeGetBoolean(() -> policyManager.getDisallowBluetooth()));
            userRestrictions.put("disallowNfc", safeGetBoolean(() -> policyManager.getDisallowNfc()));
            userRestrictions.put("disallowOutgoingBeam", safeGetBoolean(() -> policyManager.getDisallowOutgoingBeam()));
            
            // Configuration
            userRestrictions.put("disallowConfigBrightness", safeGetBoolean(() -> policyManager.getDisallowConfigBrightness()));
            userRestrictions.put("disallowConfigCellBroadcasts", safeGetBoolean(() -> policyManager.getDisallowConfigCellBroadcasts()));
            userRestrictions.put("disallowConfigLocale", safeGetBoolean(() -> policyManager.getDisallowConfigLocale()));
            userRestrictions.put("disallowConfigDateTime", safeGetBoolean(() -> policyManager.getDisallowConfigDateTime()));
            userRestrictions.put("disallowConfigCredentials", safeGetBoolean(() -> policyManager.getDisallowConfigCredentials()));
            
            // Storage & Media
            userRestrictions.put("disallowUsbFileTransfer", safeGetBoolean(() -> policyManager.getDisallowUsbFileTransfer()));
            userRestrictions.put("disallowMountPhysicalMedia", safeGetBoolean(() -> policyManager.getDisallowMountPhysicalMedia()));
            
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
            
            // User profiles
            JSONArray userProfiles = new JSONArray();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                try {
                    List<android.os.UserHandle> profiles = policyManager.getUserProfiles();
                    if (profiles != null) {
                        for (android.os.UserHandle handle : profiles) {
                            try {
                                JSONObject userObj = new JSONObject();
                                
                                // Get user ID
                                java.lang.reflect.Method getIdentifierMethod = android.os.UserHandle.class.getMethod("getIdentifier");
                                int userId = (Integer) getIdentifierMethod.invoke(handle);
                                userObj.put("userId", userId);
                                
                                // Get user name
                                String userName = policyManager.getUserName(handle);
                                if (userName != null && !userName.isEmpty()) {
                                    userObj.put("userName", userName);
                                } else {
                                    userObj.put("userName", "User " + userId);
                                }
                                
                                // Mark if it's the owner
                                userObj.put("isOwner", userId == 0);
                                
                                userProfiles.put(userObj);
                            } catch (Exception e) {
                                Log.w(TAG, "Error getting user profile info", e);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Error getting user profiles", e);
                }
            }
            root.put("userProfiles", userProfiles);
            
            // Timestamp
            root.put("exportTimestamp", System.currentTimeMillis());
            root.put("exportDate", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            
            // Write to shared directory accessible by all users
            File sharedDir = new File(SHARED_DIR);
            if (!sharedDir.exists()) {
                sharedDir.mkdirs();
            }
            
            File exportFile = new File(sharedDir, EXPORT_FILE);
            
            FileWriter writer = new FileWriter(exportFile);
            writer.write(root.toString(2)); // Pretty print with 2-space indent
            writer.close();
            
            // Make file readable by all users (system app privilege)
            exportFile.setReadable(true, false);
            exportFile.setWritable(true, false);
            
            Log.i(TAG, "Policy state exported to: " + exportFile.getAbsolutePath());
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error exporting policy state", e);
            return false;
        }
    }
    
    public String getExportFilePath() {
        return new File(SHARED_DIR, EXPORT_FILE).getAbsolutePath();
    }
    
    public static String getSharedConfigPath() {
        return new File(SHARED_DIR, EXPORT_FILE).getAbsolutePath();
    }
    
    // Safe getter methods to handle SecurityExceptions
    private boolean safeGetBoolean(BooleanSupplier supplier) {
        try {
            return supplier.get();
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException getting boolean value: " + e.getMessage());
            return false;
        } catch (Exception e) {
            Log.w(TAG, "Error getting boolean value", e);
            return false;
        }
    }
    
    private String safeGetString(StringSupplier supplier) {
        try {
            String value = supplier.get();
            return value != null ? value : "";
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException getting string value: " + e.getMessage());
            return "";
        } catch (Exception e) {
            Log.w(TAG, "Error getting string value", e);
            return "";
        }
    }
    
    // Functional interfaces for safe getters
    private interface BooleanSupplier {
        boolean get();
    }
    
    private interface StringSupplier {
        String get();
    }
}
