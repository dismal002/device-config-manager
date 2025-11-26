package com.deviceconfig.policymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

public class OtherSettingsActivity extends PolicyDetailActivity {
    
    private Switch disableCameraSwitch;
    private Switch disableScreenCaptureSwitch;
    private Switch forceEphemeralUsersSwitch;
    private Switch crossProfileContactsSwitch;
    private Switch disallowDebuggingSwitch;
    private Switch noPrintingSwitch;
    private Spinner systemUpdatePolicySpinner;
    private Spinner permissionPolicySpinner;
    private EditText adbEnabledInput;
    private EditText usbMassStorageInput;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Other Settings");
        }
    }
    
    @Override
    protected void buildPreferences(String category) {
        // Disable Camera
        View cameraView = addSwitchPreference(
            "Disable Camera",
            "Prevent camera access on the device",
            policyManager.getDisableCamera(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisableCamera(sw.isChecked());
                showToast(success ? "Camera policy updated" : "Failed to update camera policy");
            }
        );
        disableCameraSwitch = cameraView.findViewById(R.id.switchWidget);
        
        // Disable Screen Capture
        View screenCaptureView = addSwitchPreference(
            "Disable Screen Capture",
            "Prevent screenshots and screen recording",
            policyManager.getDisableScreenCapture(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisableScreenCapture(sw.isChecked());
                showToast(success ? "Screen capture policy updated" : "Failed to update screen capture policy");
            }
        );
        disableScreenCaptureSwitch = screenCaptureView.findViewById(R.id.switchWidget);
        
        // System Update Policy
        String[] updatePolicies = {"Automatic", "Postponed", "Windowed"};
        View updatePolicyView = addSpinnerPreference(
            "System Update Policy",
            "Control how system updates are handled",
            updatePolicies,
            0,
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int policy = 0;
                    switch (position) {
                        case 0: policy = 0; break; // SYSTEM_UPDATE_POLICY_TYPE_INSTALL_AUTOMATIC
                        case 1: policy = 2; break; // SYSTEM_UPDATE_POLICY_TYPE_POSTPONE
                        case 2: policy = 1; break; // SYSTEM_UPDATE_POLICY_TYPE_WINDOWED
                    }
                    boolean success = policyManager.setSystemUpdatePolicy(policy);
                    showToast(success ? "System update policy updated" : "Failed to update system update policy");
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            }
        );
        systemUpdatePolicySpinner = updatePolicyView.findViewById(R.id.spinner);
        
        // Permission Policy
        String[] permissionPolicies = {"Prompt", "Auto Grant", "Auto Deny"};
        View permissionPolicyView = addSpinnerPreference(
            "Permission Policy",
            "Default action for permission requests",
            permissionPolicies,
            0,
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int policy = 0;
                    switch (position) {
                        case 0: policy = 0; break; // PERMISSION_POLICY_PROMPT
                        case 1: policy = 1; break; // PERMISSION_POLICY_AUTO_GRANT
                        case 2: policy = 2; break; // PERMISSION_POLICY_AUTO_DENY
                    }
                    boolean success = policyManager.setPermissionPolicy(policy);
                    showToast(success ? "Permission policy updated" : "Failed to update permission policy");
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            }
        );
        permissionPolicySpinner = permissionPolicyView.findViewById(R.id.spinner);
        
        // Force Ephemeral Users
        View ephemeralUsersView = addSwitchPreference(
            "Force Ephemeral Users",
            "Users are removed when they log out",
            policyManager.getForceEphemeralUsers(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setForceEphemeralUsers(sw.isChecked());
                showToast(success ? "Ephemeral users policy updated" : "Failed to update ephemeral users policy");
            }
        );
        forceEphemeralUsersSwitch = ephemeralUsersView.findViewById(R.id.switchWidget);
        
        // Cross Profile Contacts
        View crossProfileView = addSwitchPreference(
            "Cross Profile Contacts",
            "Allow access to contacts across profiles",
            policyManager.getCrossProfileContactsDisabled(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setCrossProfileContactsDisabled(sw.isChecked());
                showToast(success ? "Cross profile contacts policy updated" : "Failed to update cross profile contacts policy");
            }
        );
        crossProfileContactsSwitch = crossProfileView.findViewById(R.id.switchWidget);
        
        // Disallow Debugging
        View debuggingView = addSwitchPreference(
            "Disallow Debugging",
            "Prevent USB debugging and development features",
            policyManager.getDisallowDebugging(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowDebugging(sw.isChecked());
                showToast(success ? "Debugging policy updated" : "Failed to update debugging policy");
            }
        );
        disallowDebuggingSwitch = debuggingView.findViewById(R.id.switchWidget);
        
        // Disable Printing
        View printingView = addSwitchPreference(
            "Disable Printing",
            "Prevent printing from applications",
            policyManager.getNoPrinting(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setNoPrinting(sw.isChecked());
                showToast(success ? "Printing policy updated" : "Failed to update printing policy");
            }
        );
        noPrintingSwitch = printingView.findViewById(R.id.switchWidget);
        
        // Disable Brightness Configuration
        View brightnessView = addSwitchPreference(
            "Disable Brightness Configuration",
            "Prevent users from adjusting brightness",
            policyManager.getDisallowConfigBrightness(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowConfigBrightness(sw.isChecked());
                showToast(success ? "Brightness policy updated" : "Failed to update brightness policy");
            }
        );
        
        // Disable Locale Configuration
        View localeView = addSwitchPreference(
            "Disable Locale Configuration",
            "Prevent users from changing language/locale",
            policyManager.getDisallowConfigLocale(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowConfigLocale(sw.isChecked());
                showToast(success ? "Locale policy updated" : "Failed to update locale policy");
            }
        );
        
        // Disable Date/Time Configuration
        View dateTimeView = addSwitchPreference(
            "Disable Date/Time Configuration",
            "Prevent users from changing date and time",
            policyManager.getDisallowConfigDateTime(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowConfigDateTime(sw.isChecked());
                showToast(success ? "Date/Time policy updated" : "Failed to update date/time policy");
            }
        );
        
        // Disable Default Apps Configuration
        View defaultAppsView = addSwitchPreference(
            "Disable Default Apps Configuration",
            "Prevent users from changing default apps",
            policyManager.getDisallowConfigDefaultApps(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowConfigDefaultApps(sw.isChecked());
                showToast(success ? "Default apps policy updated" : "Failed to update default apps policy");
            }
        );
        
        // Disable Credentials Configuration
        View credentialsView = addSwitchPreference(
            "Disable Credentials Configuration",
            "Prevent users from configuring credentials",
            policyManager.getDisallowConfigCredentials(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowConfigCredentials(sw.isChecked());
                showToast(success ? "Credentials policy updated" : "Failed to update credentials policy");
            }
        );
        
        // Disable Cell Broadcasts Configuration
        View cellBroadcastsView = addSwitchPreference(
            "Disable Cell Broadcasts Configuration",
            "Prevent users from configuring cell broadcasts",
            policyManager.getDisallowConfigCellBroadcasts(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowConfigCellBroadcasts(sw.isChecked());
                showToast(success ? "Cell broadcasts policy updated" : "Failed to update cell broadcasts policy");
            }
        );
        
        // Disable Install Apps
        View installAppsView = addSwitchPreference(
            "Disable Install Apps",
            "Prevent users from installing applications",
            policyManager.getDisallowInstallApps(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowInstallApps(sw.isChecked());
                showToast(success ? "Install apps policy updated" : "Failed to update install apps policy");
            }
        );
        
        // Disable Outgoing Beam
        View outgoingBeamView = addSwitchPreference(
            "Disable Outgoing Beam",
            "Prevent beaming data to other devices",
            policyManager.getDisallowOutgoingBeam(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowOutgoingBeam(sw.isChecked());
                showToast(success ? "Outgoing beam policy updated" : "Failed to update outgoing beam policy");
            }
        );
        
        // Disable NFC
        View nfcView = addSwitchPreference(
            "Disable NFC",
            "Prevent Near Field Communication",
            policyManager.getDisallowNfc(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowNfc(sw.isChecked());
                showToast(success ? "NFC policy updated" : "Failed to update NFC policy");
            }
        );
        
        // Disable USB File Transfer
        View usbFileTransferView = addSwitchPreference(
            "Disable USB File Transfer",
            "Prevent file transfer via USB",
            policyManager.getDisallowUsbFileTransfer(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowUsbFileTransfer(sw.isChecked());
                showToast(success ? "USB file transfer policy updated" : "Failed to update USB file transfer policy");
            }
        );
        
        // Disable Mount Physical Media
        View mountMediaView = addSwitchPreference(
            "Disable Mount Physical Media",
            "Prevent mounting of physical media",
            policyManager.getDisallowMountPhysicalMedia(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowMountPhysicalMedia(sw.isChecked());
                showToast(success ? "Mount physical media policy updated" : "Failed to update mount physical media policy");
            }
        );
    }
}
