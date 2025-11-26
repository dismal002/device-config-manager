package com.deviceconfig.policymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class OrganizationSettingsActivity extends PolicyDetailActivity {
    
    private EditText organizationNameInput;
    private Switch networkLoggingSwitch;
    private EditText lockScreenInfoInput;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Organization Settings");
        }
    }
    
    @Override
    protected void buildPreferences(String category) {
        // Organization Name
        View orgNameView = addEditTextPreference(
            "Organization Name",
            "Set the organization name displayed on device",
            "Enter organization name",
            policyManager.getOrganizationName(),
            (v, hasFocus) -> {
                if (!hasFocus) {
                    EditText et = (EditText) v;
                    String name = et.getText().toString();
                    boolean success = policyManager.setOrganizationName(name);
                    showToast(success ? "Organization name updated" : "Failed to update organization name");
                }
            }
        );
        organizationNameInput = orgNameView.findViewById(R.id.edittext);
        
        // Network Logging
        View networkLoggingView = addSwitchPreference(
            "Network Logging",
            "Enable logging of network activity",
            policyManager.getNetworkLoggingEnabled(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setNetworkLoggingEnabled(sw.isChecked());
                showToast(success ? "Network logging policy updated" : "Failed to update network logging");
            }
        );
        networkLoggingSwitch = networkLoggingView.findViewById(R.id.switchWidget);
        
        // Lock Screen Info
        View lockScreenInfoView = addEditTextPreference(
            "Lock Screen Information",
            "Information displayed on lock screen",
            "Enter lock screen info",
            policyManager.getLockScreenInfo(),
            (v, hasFocus) -> {
                if (!hasFocus) {
                    EditText et = (EditText) v;
                    String info = et.getText().toString();
                    boolean success = policyManager.setLockScreenInfo(info);
                    showToast(success ? "Lock screen info updated" : "Failed to update lock screen info");
                }
            }
        );
        lockScreenInfoInput = lockScreenInfoView.findViewById(R.id.edittext);
    }
}
