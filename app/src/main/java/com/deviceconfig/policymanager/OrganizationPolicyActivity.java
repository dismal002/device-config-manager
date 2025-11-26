package com.deviceconfig.policymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class OrganizationPolicyActivity extends PolicyDetailActivity {
    
    private Switch forceEphemeralUsersSwitch;
    private Switch crossProfileContactsSwitch;
    private EditText lockScreenInfoInput;
    private EditText organizationNameInput;
    
    @Override
    protected void buildPreferences(String category) {
        // Lock Screen Info
        View lockScreenView = addEditTextPreference(
            "Lock Screen Info",
            "Custom message to display on lock screen",
            "Enter lock screen message",
            "",
            (v, hasFocus) -> {
                if (!hasFocus) {
                    EditText et = (EditText) v;
                    String info = et.getText().toString();
                    boolean success = policyManager.setDeviceOwnerLockScreenInfo(info);
                    showToast(success ? "Lock screen info updated" : "Failed to update");
                }
            }
        );
        lockScreenInfoInput = lockScreenView.findViewById(R.id.edittext);
        
        // Organization Name
        View orgNameView = addEditTextPreference(
            "Organization Name",
            "Name of the organization managing this device",
            "Enter organization name",
            "",
            (v, hasFocus) -> {
                if (!hasFocus) {
                    EditText et = (EditText) v;
                    String name = et.getText().toString();
                    boolean success = policyManager.setOrganizationName(name);
                    showToast(success ? "Organization name updated" : "Failed to update");
                }
            }
        );
        organizationNameInput = orgNameView.findViewById(R.id.edittext);
        
        // Cross Profile Contacts Search
        View crossProfileView = addSwitchPreference(
            "Disable Cross Profile Contacts Search",
            "Prevent searching contacts across profiles",
            false,
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setCrossProfileContactsSearchDisabled(sw.isChecked());
                showToast(success ? "Cross profile contacts updated" : "Failed to update");
            }
        );
        crossProfileContactsSwitch = crossProfileView.findViewById(R.id.switchWidget);
        
        // Force Ephemeral Users
        View ephemeralView = addSwitchPreference(
            "Force Ephemeral Users",
            "Force users to be temporary",
            false,
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setForceEphemeralUsers(sw.isChecked());
                showToast(success ? "Ephemeral users updated" : "Failed to update");
            }
        );
        forceEphemeralUsersSwitch = ephemeralView.findViewById(R.id.switchWidget);
    }
}

