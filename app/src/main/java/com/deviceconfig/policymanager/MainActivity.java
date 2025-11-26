package com.deviceconfig.policymanager;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.deviceconfig.policymanager.dashboard.DashboardBuilder;
import com.deviceconfig.policymanager.dashboard.DashboardCategory;
import com.deviceconfig.policymanager.dashboard.DashboardTile;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    
    private PolicyManager policyManager;
    private DevicePolicyManager dpm;
    private ComponentName adminComponent;
    
    // UI Components - Device Policy Manager
    private Switch disableCameraSwitch;
    private Switch disableScreenCaptureSwitch;
    private Spinner systemUpdatePolicySpinner;
    private Spinner permissionPolicySpinner;
    private Spinner passwordQualitySpinner;
    private EditText passwordMinLengthInput;
    private EditText passwordMinNumericInput;
    private EditText passwordHistoryLengthInput;
    private EditText passwordExpirationInput;
    private EditText maxFailedPasswordsInput;
    private Switch forceEphemeralUsersSwitch;
    private EditText lockScreenInfoInput;
    private Switch crossProfileContactsSwitch;
    private TextView securityLoggingStatus;
    private EditText organizationNameInput;
    private Switch networkLoggingSwitch;
    private TextView guestUserDisabledStatus;
    private EditText adbEnabledInput;
    private EditText usbMassStorageInput;
    
    // UI Components - User Restrictions
    private Switch noModifyAccountsSwitch;
    private Switch noConfigWifiSwitch;
    private Switch noChangeWifiStateSwitch;
    private Switch noInstallAppsSwitch;
    private Switch noUninstallAppsSwitch;
    private Switch noShareLocationSwitch;
    private Switch noAirplaneModeSwitch;
    private Switch noConfigScreenTimeoutSwitch;
    private Switch noInstallUnknownSourcesSwitch;
    private Switch noInstallUnknownSourcesGloballySwitch;
    private Switch noUsbFileTransferSwitch;
    private Switch noFactoryResetSwitch;
    private Switch noControlAppsSwitch;
    private Switch noFunSwitch;
    private Switch noWallpaperSwitch;
    private Switch noOemUnlockSwitch;
    private Switch noAutofillSwitch;
    private Switch noPrintingSwitch;
    private Switch noCellular2gSwitch;
    private Switch disallowDebuggingSwitch;
    
    private Button enableAdminButton;
    private Button applyAllButton;
    private TextView statusText;
    
    // User Profile Management
    private EditText userProfileNameInput;
    private Button createUserProfileButton;
    private TextView userProfilesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        
        policyManager = new PolicyManager(this);
        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, DeviceAdminReceiver.class);
        
        ViewGroup dashboardContainer = findViewById(R.id.dashboard_container);
        
        // Add status section at top
        addStatusSection(dashboardContainer);
        
        buildDashboard(dashboardContainer);
    }
    
    private void addStatusSection(ViewGroup container) {
        View statusView = LayoutInflater.from(this).inflate(R.layout.preference, container, false);
        TextView titleView = statusView.findViewById(android.R.id.title);
        TextView summaryView = statusView.findViewById(android.R.id.summary);
        
        boolean isAdmin = policyManager.isAdminActive();
        boolean isDeviceOwner = policyManager.isDeviceOwner();
        
        titleView.setText("Device Admin Status");
        summaryView.setText("Admin: " + (isAdmin ? "Active" : "Inactive") + " | Owner: " + (isDeviceOwner ? "Yes" : "No"));
        summaryView.setVisibility(View.VISIBLE);
        
        statusView.setOnClickListener(v -> {
            if (!isAdmin) {
                enableDeviceAdmin();
            }
        });
        
        container.addView(statusView);
    }
    
    private void enableDeviceAdmin() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "This app needs device admin privileges to manage device policies.");
        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            // Refresh dashboard to update status
            ViewGroup dashboardContainer = findViewById(R.id.dashboard_container);
            dashboardContainer.removeAllViews();
            addStatusSection(dashboardContainer);
            buildDashboard(dashboardContainer);
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Device admin enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void buildDashboard(ViewGroup container) {
        java.util.List<DashboardCategory> categories = new java.util.ArrayList<>();
        
        // Wireless Networking Category
        DashboardCategory wirelessCategory = new DashboardCategory();
        wirelessCategory.title = "Wireless Networking";
        addTile(wirelessCategory, "WiFi & Cellular", "Configure wireless network policies", R.drawable.ic_settings_wireless, "WIRELESS_NETWORKING");
        categories.add(wirelessCategory);
        
        // Password Settings Category
        DashboardCategory passwordCategory = new DashboardCategory();
        passwordCategory.title = "Password Settings";
        addTile(passwordCategory, "Password Policies", "Configure password requirements", R.drawable.ic_settings_security, "PASSWORD_SETTINGS");
        categories.add(passwordCategory);
        
        // Organization Settings Category
        DashboardCategory organizationCategory = new DashboardCategory();
        organizationCategory.title = "Organization Settings";
        addTile(organizationCategory, "Organization Info", "Set organization details and logging", R.drawable.ic_settings_home, "ORGANIZATION_SETTINGS");
        categories.add(organizationCategory);
        
        // Users Category
        DashboardCategory usersCategory = new DashboardCategory();
        usersCategory.title = "Users";
        addTile(usersCategory, "User Restrictions", "Manage user restrictions and permissions", R.drawable.ic_settings_multiuser, "USER_RESTRICTIONS");
        addTile(usersCategory, "User Profiles", "Create and manage user profiles", R.drawable.ic_settings_multiuser, "USER_PROFILES");
        categories.add(usersCategory);
        
        // Other Settings Category
        DashboardCategory otherCategory = new DashboardCategory();
        otherCategory.title = "Other Settings";
        addTile(otherCategory, "Security & Device", "Camera, screen capture, and device policies", R.drawable.ic_settings_security, "OTHER_SETTINGS");
        categories.add(otherCategory);
        
        // About Category
        DashboardCategory aboutCategory = new DashboardCategory();
        aboutCategory.title = "About";
        addTile(aboutCategory, "About", "App version, licenses, and contributors", R.drawable.ic_settings_about, "ABOUT");
        categories.add(aboutCategory);
        
        // Build the dashboard
        DashboardBuilder.buildDashboard(this, container, categories);
    }
    
    private void addTile(DashboardCategory category, String title, String summary, int iconRes, String tileId) {
        DashboardTile tile = new DashboardTile();
        tile.title = title;
        tile.summary = summary;
        tile.iconRes = iconRes;
        tile.id = tileId.hashCode();
        tile.extras = new Bundle();
        tile.extras.putString("TILE_ID", tileId);
        category.addTile(tile);
    }
    
    public void onTileClicked(String tileId, DashboardTile tile) {
        Intent intent = null;
        String category = null;
        
        // Map tile IDs to activities
        switch (tileId) {
            case "WIRELESS_NETWORKING":
                intent = new Intent(this, WirelessNetworkingActivity.class);
                category = "Wireless Networking";
                break;
                
            case "PASSWORD_SETTINGS":
                intent = new Intent(this, PasswordSettingsActivity.class);
                category = "Password Settings";
                break;
                
            case "ORGANIZATION_SETTINGS":
                intent = new Intent(this, OrganizationSettingsActivity.class);
                category = "Organization Settings";
                break;
                
            case "USER_RESTRICTIONS":
                intent = new Intent(this, UserRestrictionsActivity.class);
                category = "User Restrictions";
                break;
                
            case "USER_PROFILES":
                intent = new Intent(this, UserProfileActivity.class);
                category = "User Profiles";
                break;
                
            case "OTHER_SETTINGS":
                intent = new Intent(this, OtherSettingsActivity.class);
                category = "Other Settings";
                break;
                
            case "ABOUT":
                intent = new Intent(this, AboutActivity.class);
                category = "About";
                break;
        }
        
        if (intent != null) {
            intent.putExtra("CATEGORY", category);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Clicked: " + tile.title, Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        // Status
        statusText = findViewById(R.id.statusText);
        enableAdminButton = findViewById(R.id.enableAdminButton);
        
        // Device Policy Manager
        disableCameraSwitch = findViewById(R.id.disableCameraSwitch);
        disableScreenCaptureSwitch = findViewById(R.id.disableScreenCaptureSwitch);
        systemUpdatePolicySpinner = findViewById(R.id.systemUpdatePolicySpinner);
        permissionPolicySpinner = findViewById(R.id.permissionPolicySpinner);
        passwordQualitySpinner = findViewById(R.id.passwordQualitySpinner);
        passwordMinLengthInput = findViewById(R.id.passwordMinLengthInput);
        passwordMinNumericInput = findViewById(R.id.passwordMinNumericInput);
        passwordHistoryLengthInput = findViewById(R.id.passwordHistoryLengthInput);
        passwordExpirationInput = findViewById(R.id.passwordExpirationInput);
        maxFailedPasswordsInput = findViewById(R.id.maxFailedPasswordsInput);
        forceEphemeralUsersSwitch = findViewById(R.id.forceEphemeralUsersSwitch);
        lockScreenInfoInput = findViewById(R.id.lockScreenInfoInput);
        crossProfileContactsSwitch = findViewById(R.id.crossProfileContactsSwitch);
        securityLoggingStatus = findViewById(R.id.securityLoggingStatus);
        organizationNameInput = findViewById(R.id.organizationNameInput);
        networkLoggingSwitch = findViewById(R.id.networkLoggingSwitch);
        guestUserDisabledStatus = findViewById(R.id.guestUserDisabledStatus);
        adbEnabledInput = findViewById(R.id.adbEnabledInput);
        usbMassStorageInput = findViewById(R.id.usbMassStorageInput);
        
        // User Restrictions
        noModifyAccountsSwitch = findViewById(R.id.noModifyAccountsSwitch);
        noConfigWifiSwitch = findViewById(R.id.noConfigWifiSwitch);
        noChangeWifiStateSwitch = findViewById(R.id.noChangeWifiStateSwitch);
        noInstallAppsSwitch = findViewById(R.id.noInstallAppsSwitch);
        noUninstallAppsSwitch = findViewById(R.id.noUninstallAppsSwitch);
        noShareLocationSwitch = findViewById(R.id.noShareLocationSwitch);
        noAirplaneModeSwitch = findViewById(R.id.noAirplaneModeSwitch);
        noConfigScreenTimeoutSwitch = findViewById(R.id.noConfigScreenTimeoutSwitch);
        noInstallUnknownSourcesSwitch = findViewById(R.id.noInstallUnknownSourcesSwitch);
        noInstallUnknownSourcesGloballySwitch = findViewById(R.id.noInstallUnknownSourcesGloballySwitch);
        noUsbFileTransferSwitch = findViewById(R.id.noUsbFileTransferSwitch);
        noFactoryResetSwitch = findViewById(R.id.noFactoryResetSwitch);
        noControlAppsSwitch = findViewById(R.id.noControlAppsSwitch);
        noFunSwitch = findViewById(R.id.noFunSwitch);
        noWallpaperSwitch = findViewById(R.id.noWallpaperSwitch);
        noOemUnlockSwitch = findViewById(R.id.noOemUnlockSwitch);
        noAutofillSwitch = findViewById(R.id.noAutofillSwitch);
        noPrintingSwitch = findViewById(R.id.noPrintingSwitch);
        noCellular2gSwitch = findViewById(R.id.noCellular2gSwitch);
        disallowDebuggingSwitch = findViewById(R.id.disallowDebuggingSwitch);
        
        applyAllButton = findViewById(R.id.applyAllButton);
        
        // User Profile Management
        userProfileNameInput = findViewById(R.id.userProfileNameInput);
        createUserProfileButton = findViewById(R.id.createUserProfileButton);
        userProfilesList = findViewById(R.id.userProfilesList);
    }

    private void setupSpinners() {
        // System Update Policy
        ArrayAdapter<CharSequence> systemUpdateAdapter = ArrayAdapter.createFromResource(
            this, R.array.system_update_policies, android.R.layout.simple_spinner_item);
        systemUpdateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        systemUpdatePolicySpinner.setAdapter(systemUpdateAdapter);
        
        // Permission Policy
        ArrayAdapter<CharSequence> permissionAdapter = ArrayAdapter.createFromResource(
            this, R.array.permission_policies, android.R.layout.simple_spinner_item);
        permissionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        permissionPolicySpinner.setAdapter(permissionAdapter);
        
        // Password Quality
        ArrayAdapter<CharSequence> passwordQualityAdapter = ArrayAdapter.createFromResource(
            this, R.array.password_qualities, android.R.layout.simple_spinner_item);
        passwordQualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        passwordQualitySpinner.setAdapter(passwordQualityAdapter);
    }

    private void updateStatus() {
        boolean isAdmin = policyManager.isAdminActive();
        boolean isDeviceOwner = policyManager.isDeviceOwner();
        
        String status = "Admin Active: " + isAdmin + "\nDevice Owner: " + isDeviceOwner;
        statusText.setText(status);
        
        enableAdminButton.setEnabled(!isAdmin);
        
        if (isDeviceOwner) {
            loadCurrentPolicies();
        }
    }

    private void loadCurrentPolicies() {
        try {
            // Load Device Policy Manager values
            disableCameraSwitch.setChecked(policyManager.getDisableCamera());
            disableScreenCaptureSwitch.setChecked(policyManager.getDisableScreenCapture());
            securityLoggingStatus.setText(policyManager.isSecurityLoggingEnabled() ? "Enabled" : "Disabled");
            guestUserDisabledStatus.setText(policyManager.getGuestUserDisabled() ? "Disabled" : "Enabled");
        } catch (Exception e) {
            // Ignore errors when loading
        }
    }

    private void setupListeners() {
        enableAdminButton.setOnClickListener(v -> enableDeviceAdmin());
        applyAllButton.setOnClickListener(v -> applyAllPolicies());
        createUserProfileButton.setOnClickListener(v -> createUserProfile());
    }
    
    private void createUserProfile() {
        String userName = userProfileNameInput.getText().toString().trim();
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user profile name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!policyManager.isDeviceOwner()) {
            Toast.makeText(this, "Device Owner privileges required to create user profiles", Toast.LENGTH_LONG).show();
            return;
        }
        
        android.os.UserHandle userHandle = policyManager.createUserProfile(userName);
        if (userHandle != null) {
            Toast.makeText(this, "User profile created: " + userName, Toast.LENGTH_SHORT).show();
            userProfileNameInput.setText("");
            refreshUserProfilesList();
        } else {
            Toast.makeText(this, "Failed to create user profile", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void refreshUserProfilesList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            java.util.List<android.os.UserHandle> profiles = policyManager.getUserProfiles();
            if (profiles != null && !profiles.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                int index = 1;
                for (android.os.UserHandle handle : profiles) {
                    if (handle != null) {
                        // UserHandle.getIdentifier() is not publicly available
                        // Use reflection to access it, or fallback to toString
                        String userInfo;
                        try {
                            // Try to get user ID using reflection
                            java.lang.reflect.Method method = android.os.UserHandle.class.getMethod("getIdentifier");
                            int userId = (Integer) method.invoke(handle);
                            userInfo = "User ID: " + userId;
                        } catch (Exception e) {
                            // Fallback: use toString representation
                            userInfo = "User Profile #" + index;
                        }
                        sb.append(userInfo).append("\n");
                        index++;
                    }
                }
                userProfilesList.setText(sb.toString().trim());
            } else {
                userProfilesList.setText("No user profiles found");
            }
        } else {
            userProfilesList.setText("User profile management requires API 24+");
        }
    }

    private void applyAllPolicies() {
        if (!policyManager.isAdminActive() && !policyManager.isDeviceOwner()) {
            Toast.makeText(this, "Please enable device admin first", Toast.LENGTH_SHORT).show();
            return;
        }

        int successCount = 0;
        int failCount = 0;

        // Device Policy Manager Policies
        if (policyManager.isDeviceOwner()) {
            if (policyManager.setDisableCamera(disableCameraSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setDisableScreenCapture(disableScreenCaptureSwitch.isChecked())) successCount++;
            else failCount++;
            
            int systemUpdatePolicy = systemUpdatePolicySpinner.getSelectedItemPosition();
            if (policyManager.setSystemUpdatePolicy(systemUpdatePolicy)) successCount++;
            else failCount++;
            
            int permissionPolicyPos = permissionPolicySpinner.getSelectedItemPosition();
            int permissionPolicy = permissionPolicyPos; // 0=PROMPT, 1=GRANT, 2=DENY
            if (policyManager.setPermissionPolicy(permissionPolicy)) successCount++;
            else failCount++;
            
            if (policyManager.setForceEphemeralUsers(forceEphemeralUsersSwitch.isChecked())) successCount++;
            else failCount++;
            
            String lockScreenInfo = lockScreenInfoInput.getText().toString();
            if (!lockScreenInfo.isEmpty()) {
                if (policyManager.setDeviceOwnerLockScreenInfo(lockScreenInfo)) successCount++;
                else failCount++;
            }
            
            if (policyManager.setCrossProfileContactsSearchDisabled(crossProfileContactsSwitch.isChecked())) successCount++;
            else failCount++;
            
            String orgName = organizationNameInput.getText().toString();
            if (!orgName.isEmpty()) {
                if (policyManager.setOrganizationName(orgName)) successCount++;
                else failCount++;
            }
            
            if (policyManager.setNetworkLoggingEnabled(networkLoggingSwitch.isChecked())) successCount++;
            else failCount++;
            
            try {
                String adbValue = adbEnabledInput.getText().toString();
                if (!adbValue.isEmpty()) {
                    if (policyManager.setGlobalSetting(Settings.Global.ADB_ENABLED, Integer.parseInt(adbValue))) successCount++;
                    else failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
            
            try {
                String usbValue = usbMassStorageInput.getText().toString();
                if (!usbValue.isEmpty()) {
                    if (policyManager.setGlobalSetting(Settings.Global.USB_MASS_STORAGE_ENABLED, Integer.parseInt(usbValue))) successCount++;
                    else failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
        }

        // Password Policies (require admin)
        if (policyManager.isAdminActive()) {
            int passwordQualityPos = passwordQualitySpinner.getSelectedItemPosition();
            int passwordQuality = 0;
            switch (passwordQualityPos) {
                case 0: passwordQuality = DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED; break;
                case 1: passwordQuality = DevicePolicyManager.PASSWORD_QUALITY_SOMETHING; break;
                case 2: passwordQuality = DevicePolicyManager.PASSWORD_QUALITY_NUMERIC_COMPLEX; break;
                case 3: passwordQuality = DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC; break;
                case 4: passwordQuality = DevicePolicyManager.PASSWORD_QUALITY_COMPLEX; break;
            }
            if (policyManager.setPasswordQuality(passwordQuality)) successCount++;
            else failCount++;
            
            try {
                String minLength = passwordMinLengthInput.getText().toString();
                if (!minLength.isEmpty()) {
                    if (policyManager.setPasswordMinimumLength(Integer.parseInt(minLength))) successCount++;
                    else failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
            
            try {
                String minNumeric = passwordMinNumericInput.getText().toString();
                if (!minNumeric.isEmpty()) {
                    if (policyManager.setPasswordMinimumNumeric(Integer.parseInt(minNumeric))) successCount++;
                    else failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
            
            try {
                String historyLength = passwordHistoryLengthInput.getText().toString();
                if (!historyLength.isEmpty()) {
                    if (policyManager.setPasswordHistoryLength(Integer.parseInt(historyLength))) successCount++;
                    else failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
            
            try {
                String expiration = passwordExpirationInput.getText().toString();
                if (!expiration.isEmpty()) {
                    if (policyManager.setPasswordExpirationTimeout(Long.parseLong(expiration))) successCount++;
                    else failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
            
            try {
                String maxFailed = maxFailedPasswordsInput.getText().toString();
                if (!maxFailed.isEmpty()) {
                    if (policyManager.setMaximumFailedPasswordsForWipe(Integer.parseInt(maxFailed))) successCount++;
                    else failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
        }

        // User Restrictions (require device owner)
        if (policyManager.isDeviceOwner()) {
            if (policyManager.setUserRestriction(UserManager.DISALLOW_MODIFY_ACCOUNTS, noModifyAccountsSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_CONFIG_WIFI, noConfigWifiSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_CHANGE_WIFI_STATE, noChangeWifiStateSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_INSTALL_APPS, noInstallAppsSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_UNINSTALL_APPS, noUninstallAppsSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_SHARE_LOCATION, noShareLocationSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_AIRPLANE_MODE, noAirplaneModeSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_CONFIG_SCREEN_TIMEOUT, noConfigScreenTimeoutSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES, noInstallUnknownSourcesSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES_GLOBALLY, noInstallUnknownSourcesGloballySwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_USB_FILE_TRANSFER, noUsbFileTransferSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, noFactoryResetSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_APPS_CONTROL, noControlAppsSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_FUN, noFunSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_SET_WALLPAPER, noWallpaperSwitch.isChecked())) successCount++;
            else failCount++;
            
            // DISALLOW_OEM_UNLOCK is not available as a UserManager constant
            // This restriction is typically handled at the system level
            // if (policyManager.setUserRestriction(UserManager.DISALLOW_OEM_UNLOCK, noOemUnlockSwitch.isChecked())) successCount++;
            // else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_AUTOFILL, noAutofillSwitch.isChecked())) successCount++;
            else failCount++;
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_PRINTING, noPrintingSwitch.isChecked())) successCount++;
            else failCount++;
            
            // DISALLOW_CELLULAR_2G requires API 29+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (policyManager.setUserRestriction(UserManager.DISALLOW_CELLULAR_2G, noCellular2gSwitch.isChecked())) successCount++;
                else failCount++;
            }
            
            if (policyManager.setUserRestriction(UserManager.DISALLOW_DEBUGGING_FEATURES, disallowDebuggingSwitch.isChecked())) successCount++;
            else failCount++;
        }

        String message = "Applied: " + successCount + " policies";
        if (failCount > 0) {
            message += ", Failed: " + failCount;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        
        updateStatus();
    }
}

