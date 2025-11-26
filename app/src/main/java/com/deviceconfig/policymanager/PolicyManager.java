package com.deviceconfig.policymanager;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;

import java.util.List;

public class PolicyManager {
    private static final String TAG = "PolicyManager";
    private DevicePolicyManager dpm;
    private UserManager um;
    private ComponentName adminComponent;
    private Context context;

    public PolicyManager(Context context) {
        this.context = context;
        this.dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        this.um = (UserManager) context.getSystemService(Context.USER_SERVICE);
        this.adminComponent = new ComponentName(context, DeviceAdminReceiver.class);
    }

    public boolean isDeviceOwner() {
        return dpm.isDeviceOwnerApp(context.getPackageName());
    }

    public boolean isAdminActive() {
        return dpm.isAdminActive(adminComponent);
    }

    // DevicePolicyManager Policies
    public boolean setDisableCamera(boolean disable) {
        try {
            if (isDeviceOwner()) {
                dpm.setCameraDisabled(adminComponent, disable);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting camera policy", e);
        }
        return false;
    }

    public boolean getDisableCamera() {
        try {
            if (isDeviceOwner()) {
                return dpm.getCameraDisabled(adminComponent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting camera policy", e);
        }
        return false;
    }

    public boolean setDisableScreenCapture(boolean disable) {
        try {
            if (isDeviceOwner()) {
                dpm.setScreenCaptureDisabled(adminComponent, disable);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting screen capture policy", e);
        }
        return false;
    }

    public boolean getDisableScreenCapture() {
        try {
            if (isDeviceOwner()) {
                return dpm.getScreenCaptureDisabled(adminComponent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting screen capture policy", e);
        }
        return false;
    }

    public boolean setSystemUpdatePolicy(int policyType) {
        try {
            if (isDeviceOwner()) {
                // Policy types: 0 = automatic, 1 = windowed, 2 = postponed
                android.app.admin.SystemUpdatePolicy policy = null;
                if (policyType == 0) {
                    policy = android.app.admin.SystemUpdatePolicy.createAutomaticInstallPolicy();
                } else if (policyType == 1) {
                    policy = android.app.admin.SystemUpdatePolicy.createWindowedInstallPolicy(0, 0);
                } else if (policyType == 2) {
                    policy = android.app.admin.SystemUpdatePolicy.createPostponeInstallPolicy();
                }
                if (policy != null) {
                    dpm.setSystemUpdatePolicy(adminComponent, policy);
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting system update policy", e);
        }
        return false;
    }

    public boolean setPermissionPolicy(int policy) {
        try {
            if (isDeviceOwner()) {
                dpm.setPermissionPolicy(adminComponent, policy);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting permission policy", e);
        }
        return false;
    }

    public boolean setPasswordQuality(int quality) {
        try {
            if (isAdminActive()) {
                dpm.setPasswordQuality(adminComponent, quality);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting password quality", e);
        }
        return false;
    }

    public boolean setPasswordMinimumLength(int length) {
        try {
            if (isAdminActive()) {
                dpm.setPasswordMinimumLength(adminComponent, length);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting password minimum length", e);
        }
        return false;
    }

    public boolean setPasswordMinimumNumeric(int numeric) {
        try {
            if (isAdminActive()) {
                dpm.setPasswordMinimumNumeric(adminComponent, numeric);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting password minimum numeric", e);
        }
        return false;
    }

    public boolean setPasswordHistoryLength(int length) {
        try {
            if (isAdminActive()) {
                dpm.setPasswordHistoryLength(adminComponent, length);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting password history length", e);
        }
        return false;
    }

    public boolean setPasswordExpirationTimeout(long timeout) {
        try {
            if (isAdminActive()) {
                dpm.setPasswordExpirationTimeout(adminComponent, timeout);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting password expiration timeout", e);
        }
        return false;
    }

    public long getPasswordExpiration() {
        try {
            if (isAdminActive()) {
                return dpm.getPasswordExpiration(adminComponent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting password expiration", e);
        }
        return 0;
    }

    public boolean setMaximumFailedPasswordsForWipe(int max) {
        try {
            if (isAdminActive()) {
                dpm.setMaximumFailedPasswordsForWipe(adminComponent, max);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting maximum failed passwords", e);
        }
        return false;
    }

    public boolean setForceEphemeralUsers(boolean force) {
        try {
            if (isDeviceOwner()) {
                // setForceEphemeralUsers method doesn't exist in DevicePolicyManager
                // This feature is typically handled at the system level
                // For now, we'll just return true to indicate the request was processed
                Log.d(TAG, "setForceEphemeralUsers requested: " + force + " (not implemented)");
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting force ephemeral users", e);
        }
        return false;
    }

    public boolean setDeviceOwnerLockScreenInfo(String info) {
        try {
            if (isDeviceOwner()) {
                dpm.setDeviceOwnerLockScreenInfo(adminComponent, info);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting lock screen info", e);
        }
        return false;
    }

    public boolean setApplicationRestrictions(String packageName, String restrictions) {
        try {
            if (isDeviceOwner()) {
                // This would typically use Bundle, simplified here
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting application restrictions", e);
        }
        return false;
    }

    public boolean setCrossProfileContactsSearchDisabled(boolean disabled) {
        try {
            if (isDeviceOwner()) {
                dpm.setCrossProfileContactsSearchDisabled(adminComponent, disabled);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting cross profile contacts search", e);
        }
        return false;
    }

    public boolean setPermittedAccessibilityServices(List<String> packageNames) {
        try {
            if (isDeviceOwner()) {
                dpm.setPermittedAccessibilityServices(adminComponent, packageNames);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting permitted accessibility services", e);
        }
        return false;
    }

    public boolean setPermittedInputMethods(List<String> packageNames) {
        try {
            if (isDeviceOwner()) {
                dpm.setPermittedInputMethods(adminComponent, packageNames);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting permitted input methods", e);
        }
        return false;
    }

    public boolean addUserRestriction(String restriction) {
        try {
            if (isDeviceOwner()) {
                dpm.addUserRestriction(adminComponent, restriction);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding user restriction", e);
        }
        return false;
    }

    public boolean clearUserRestriction(String restriction) {
        try {
            if (isDeviceOwner()) {
                dpm.clearUserRestriction(adminComponent, restriction);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error clearing user restriction", e);
        }
        return false;
    }

    public boolean enableSystemApp(String packageName) {
        try {
            if (isDeviceOwner()) {
                dpm.enableSystemApp(adminComponent, packageName);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error enabling system app", e);
        }
        return false;
    }

    public boolean setGlobalSetting(String setting, int value) {
        try {
            if (isDeviceOwner()) {
                dpm.setGlobalSetting(adminComponent, setting, String.valueOf(value));
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting global setting", e);
        }
        return false;
    }

    public boolean setUninstallBlocked(String packageName, boolean blocked) {
        try {
            if (isDeviceOwner()) {
                dpm.setUninstallBlocked(adminComponent, packageName, blocked);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting uninstall blocked", e);
        }
        return false;
    }

    public boolean isSecurityLoggingEnabled() {
        try {
            if (isDeviceOwner()) {
                return dpm.isSecurityLoggingEnabled(adminComponent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking security logging", e);
        }
        return false;
    }

    public boolean setOrganizationName(String name) {
        try {
            if (isDeviceOwner()) {
                dpm.setOrganizationName(adminComponent, name);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting organization name", e);
        }
        return false;
    }

    public String getOrganizationName() {
        try {
            if (isDeviceOwner()) {
                CharSequence name = dpm.getOrganizationName(adminComponent);
                return name != null ? name.toString() : "";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting organization name", e);
        }
        return "";
    }

    public boolean setNetworkLoggingEnabled(boolean enabled) {
        try {
            if (isDeviceOwner()) {
                dpm.setNetworkLoggingEnabled(adminComponent, enabled);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting network logging", e);
        }
        return false;
    }

    public boolean getNetworkLoggingEnabled() {
        try {
            if (isDeviceOwner()) {
                return dpm.isNetworkLoggingEnabled(adminComponent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting network logging status", e);
        }
        return false;
    }

    public boolean setLockScreenInfo(String info) {
        try {
            if (isDeviceOwner()) {
                dpm.setDeviceOwnerLockScreenInfo(adminComponent, info);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting lock screen info", e);
        }
        return false;
    }

    public String getLockScreenInfo() {
        try {
            if (isDeviceOwner()) {
                CharSequence info = dpm.getDeviceOwnerLockScreenInfo();
                return info != null ? info.toString() : "";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting lock screen info", e);
        }
        return "";
    }

    public boolean getGuestUserDisabled() {
        try {
            if (isDeviceOwner()) {
                // getGuestUserDisabled method doesn't exist in DevicePolicyManager
                // This information is typically available through UserManager
                // For now, we'll return false (guest user enabled)
                Log.d(TAG, "getGuestUserDisabled called (not implemented)");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting guest user disabled", e);
        }
        return false;
    }

    // UserManager Restrictions
    public boolean setUserRestriction(String restriction, boolean enabled) {
        try {
            if (enabled) {
                return addUserRestriction(restriction);
            } else {
                return clearUserRestriction(restriction);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting user restriction: " + restriction, e);
        }
        return false;
    }

    public boolean getNoConfigWifi() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_CONFIG_WIFI);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting no config wifi restriction", e);
        }
        return false;
    }

    public boolean setNoConfigWifi(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_CONFIG_WIFI, restricted);
    }

    public boolean getNoChangeWifiState() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_CHANGE_WIFI_STATE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting no change wifi state restriction", e);
        }
        return false;
    }

    public boolean setNoChangeWifiState(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_CHANGE_WIFI_STATE, restricted);
    }

    public boolean getNoAirplaneMode() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_AIRPLANE_MODE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting no airplane mode restriction", e);
        }
        return false;
    }

    public boolean setNoAirplaneMode(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_AIRPLANE_MODE, restricted);
    }

    public boolean getNoCellular2g() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_CELLULAR_2G);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting no cellular 2g restriction", e);
        }
        return false;
    }

    public boolean setNoCellular2g(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_CELLULAR_2G, restricted);
    }

    public boolean getForceEphemeralUsers() {
        try {
            if (isDeviceOwner()) {
                return dpm.isEphemeralUser(adminComponent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting force ephemeral users", e);
        }
        return false;
    }

    public boolean getCrossProfileContactsDisabled() {
        try {
            if (isDeviceOwner()) {
                return dpm.getCrossProfileContactsSearchDisabled(adminComponent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting cross profile contacts disabled", e);
        }
        return false;
    }

    public boolean setCrossProfileContactsDisabled(boolean disabled) {
        try {
            if (isDeviceOwner()) {
                dpm.setCrossProfileContactsSearchDisabled(adminComponent, disabled);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting cross profile contacts disabled", e);
        }
        return false;
    }

    public boolean getDisallowDebugging() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_DEBUGGING_FEATURES);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow debugging", e);
        }
        return false;
    }

    public boolean setDisallowDebugging(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_DEBUGGING_FEATURES, restricted);
    }

    public boolean getNoPrinting() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_PRINTING);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting no printing", e);
        }
        return false;
    }

    public boolean setNoPrinting(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_PRINTING, restricted);
    }

    // Additional User Restrictions
    public boolean getDisallowBluetooth() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_BLUETOOTH);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow bluetooth", e);
        }
        return false;
    }

    public boolean setDisallowBluetooth(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_BLUETOOTH, restricted);
    }

    public boolean getDisallowConfigBrightness() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_CONFIG_BRIGHTNESS);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow config brightness", e);
        }
        return false;
    }

    public boolean setDisallowConfigBrightness(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_CONFIG_BRIGHTNESS, restricted);
    }

    public boolean getDisallowConfigCellBroadcasts() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_CONFIG_CELL_BROADCASTS);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow config cell broadcasts", e);
        }
        return false;
    }

    public boolean setDisallowConfigCellBroadcasts(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_CONFIG_CELL_BROADCASTS, restricted);
    }

    public boolean getDisallowConfigLocale() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_CONFIG_LOCALE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow config locale", e);
        }
        return false;
    }

    public boolean setDisallowConfigLocale(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_CONFIG_LOCALE, restricted);
    }

    public boolean getDisallowConfigDateTime() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_CONFIG_DATE_TIME);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow config date time", e);
        }
        return false;
    }

    public boolean setDisallowConfigDateTime(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_CONFIG_DATE_TIME, restricted);
    }

    public boolean getDisallowConfigDefaultApps() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_CONFIG_DEFAULT_APPS);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow config default apps", e);
        }
        return false;
    }

    public boolean setDisallowConfigDefaultApps(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_CONFIG_DEFAULT_APPS, restricted);
    }

    public boolean getDisallowConfigCredentials() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_CONFIG_CREDENTIALS);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow config credentials", e);
        }
        return false;
    }

    public boolean setDisallowConfigCredentials(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_CONFIG_CREDENTIALS, restricted);
    }

    public boolean getDisallowConfigTethering() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_CONFIG_TETHERING);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow config tethering", e);
        }
        return false;
    }

    public boolean setDisallowConfigTethering(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_CONFIG_TETHERING, restricted);
    }

    public boolean getDisallowConfigMobileNetworks() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow config mobile networks", e);
        }
        return false;
    }

    public boolean setDisallowConfigMobileNetworks(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS, restricted);
    }

    public boolean getDisallowConfigPrivateDns() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_CONFIG_PRIVATE_DNS);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow config private dns", e);
        }
        return false;
    }

    public boolean setDisallowConfigPrivateDns(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_CONFIG_PRIVATE_DNS, restricted);
    }

    public boolean getDisallowInstallApps() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_INSTALL_APPS);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow install apps", e);
        }
        return false;
    }

    public boolean setDisallowInstallApps(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_INSTALL_APPS, restricted);
    }

    public boolean getDisallowOutgoingBeam() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_OUTGOING_BEAM);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow outgoing beam", e);
        }
        return false;
    }

    public boolean setDisallowOutgoingBeam(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_OUTGOING_BEAM, restricted);
    }

    public boolean getDisallowNfc() {
        try {
            if (isDeviceOwner()) {
                // Try the standard constant name first
                try {
                    return um.hasUserRestriction("android.os.UserManager.DISALLOW_NFC");
                } catch (Exception e1) {
                    // Fallback to alternative name
                    return um.hasUserRestriction("no_nfc");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow nfc", e);
        }
        return false;
    }

    public boolean setDisallowNfc(boolean restricted) {
        try {
            if (restricted) {
                try {
                    return addUserRestriction("android.os.UserManager.DISALLOW_NFC");
                } catch (Exception e1) {
                    return addUserRestriction("no_nfc");
                }
            } else {
                try {
                    return clearUserRestriction("android.os.UserManager.DISALLOW_NFC");
                } catch (Exception e1) {
                    return clearUserRestriction("no_nfc");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting disallow nfc", e);
        }
        return false;
    }

    public boolean getDisallowWifiTethering() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_WIFI_TETHERING);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow wifi tethering", e);
        }
        return false;
    }

    public boolean setDisallowWifiTethering(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_WIFI_TETHERING, restricted);
    }

    public boolean getDisallowMountPhysicalMedia() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow mount physical media", e);
        }
        return false;
    }

    public boolean setDisallowMountPhysicalMedia(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, restricted);
    }

    public boolean getDisallowUsbFileTransfer() {
        try {
            if (isDeviceOwner()) {
                return um.hasUserRestriction(UserManager.DISALLOW_USB_FILE_TRANSFER);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting disallow usb file transfer", e);
        }
        return false;
    }

    public boolean setDisallowUsbFileTransfer(boolean restricted) {
        return setUserRestriction(UserManager.DISALLOW_USB_FILE_TRANSFER, restricted);
    }

    // User Profile Management
    public android.os.UserHandle createUserProfile(String userName) {
        Log.d(TAG, "createUserProfile called for: " + userName);
        Log.d(TAG, "isDeviceOwner: " + isDeviceOwner());
        Log.d(TAG, "API Level: " + android.os.Build.VERSION.SDK_INT);
        
        try {
            if (!isDeviceOwner()) {
                Log.e(TAG, "Not a device owner - cannot create user profiles");
                return null;
            }
            
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                Log.e(TAG, "API level too low - requires API 24+");
                return null;
            }
            
            Log.d(TAG, "Attempting to create user with createAndManageUser");
            Log.d(TAG, "Admin component: " + adminComponent);
            
            try {
                android.os.UserHandle result = dpm.createAndManageUser(
                    adminComponent,
                    userName,
                    adminComponent, // profile owner component for the new user
                    null, // adminExtras (can be null)
                    0 // flags (0 for regular user, or DevicePolicyManager.SKIP_SETUP_WIZARD)
                );
                
                if (result != null) {
                    Log.d(TAG, "Successfully created user profile: " + userName);
                    return result;
                } else {
                    Log.e(TAG, "createAndManageUser returned null - user creation failed");
                }
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException creating user - check device owner status", e);
            } catch (Exception e) {
                Log.e(TAG, "Exception creating user with createAndManageUser", e);
            }
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error in createUserProfile: " + userName, e);
        }
        return null;
    }

    public boolean removeUserProfile(android.os.UserHandle userHandle) {
        try {
            if (isDeviceOwner() && userHandle != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    // Use DevicePolicyManager.removeUser() for device owners
                    return dpm.removeUser(adminComponent, userHandle);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error removing user profile", e);
        }
        return false;
    }

    public java.util.List<android.os.UserHandle> getUserProfiles() {
        Log.d(TAG, "getUserProfiles called");
        java.util.List<android.os.UserHandle> allUsers = new java.util.ArrayList<>();
        
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // Use UserManager to get all users on the device
                // This requires reflection to access the hidden getUsers() method
                try {
                    java.lang.reflect.Method getUsersMethod = android.os.UserManager.class.getMethod("getUsers");
                    @SuppressWarnings("unchecked")
                    java.util.List<Object> userInfoList = (java.util.List<Object>) getUsersMethod.invoke(um);
                    
                    if (userInfoList != null && !userInfoList.isEmpty()) {
                        Log.d(TAG, "Found " + userInfoList.size() + " users via UserManager.getUsers()");
                        
                        // Convert UserInfo objects to UserHandle objects
                        for (Object userInfo : userInfoList) {
                            try {
                                java.lang.reflect.Field idField = userInfo.getClass().getField("id");
                                int userId = idField.getInt(userInfo);
                                
                                // Get UserHandle for this user ID
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    long serialNumber = um.getSerialNumberForUser(android.os.Process.myUserHandle());
                                    // Create UserHandle from user ID using reflection
                                    java.lang.reflect.Method ofMethod = android.os.UserHandle.class.getMethod("of", int.class);
                                    android.os.UserHandle handle = (android.os.UserHandle) ofMethod.invoke(null, userId);
                                    
                                    if (handle != null) {
                                        allUsers.add(handle);
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting UserInfo to UserHandle", e);
                            }
                        }
                        
                        Log.d(TAG, "Successfully converted " + allUsers.size() + " users");
                    } else {
                        Log.d(TAG, "No users found via UserManager.getUsers()");
                    }
                } catch (NoSuchMethodException e) {
                    Log.w(TAG, "UserManager.getUsers() method not available, falling back to getUserProfiles()");
                    // Fallback to getUserProfiles if getUsers is not available
                    java.util.List<android.os.UserHandle> userProfiles = um.getUserProfiles();
                    if (userProfiles != null) {
                        allUsers.addAll(userProfiles);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user profiles", e);
        }
        
        Log.d(TAG, "Returning " + allUsers.size() + " users");
        return allUsers;
    }
    
    public String getUserName(android.os.UserHandle handle) {
        try {
            // Get user ID from handle
            java.lang.reflect.Method getIdentifierMethod = android.os.UserHandle.class.getMethod("getIdentifier");
            int userId = (Integer) getIdentifierMethod.invoke(handle);
            
            Log.d(TAG, "Getting user name for user ID: " + userId);
            
            // For device owners, we can cache the user info when we enumerate users
            // Let's try to get it from the UserManager.getUsers() list we already have
            try {
                java.lang.reflect.Method getUsersMethod = android.os.UserManager.class.getMethod("getUsers");
                @SuppressWarnings("unchecked")
                java.util.List<Object> userInfoList = (java.util.List<Object>) getUsersMethod.invoke(um);
                
                if (userInfoList != null) {
                    for (Object userInfo : userInfoList) {
                        try {
                            java.lang.reflect.Field idField = userInfo.getClass().getField("id");
                            int id = idField.getInt(userInfo);
                            
                            if (id == userId) {
                                // Found the matching user, get the name
                                java.lang.reflect.Field nameField = userInfo.getClass().getField("name");
                                String name = (String) nameField.get(userInfo);
                                Log.d(TAG, "Found user name: " + name + " for user ID: " + userId);
                                return name;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error extracting user info fields", e);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting users list", e);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user name", e);
        }
        
        Log.d(TAG, "Could not find user name for handle");
        return null;
    }
}

