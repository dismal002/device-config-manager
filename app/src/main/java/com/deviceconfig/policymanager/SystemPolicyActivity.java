package com.deviceconfig.policymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

public class SystemPolicyActivity extends PolicyDetailActivity {
    
    private Spinner systemUpdatePolicySpinner;
    private Spinner permissionPolicySpinner;
    private EditText adbEnabledInput;
    private EditText usbMassStorageInput;
    
    @Override
    protected void buildPreferences(String category) {
        // System Update Policy
        String[] updatePolicies = {"Automatic", "Windowed", "Postponed"};
        View systemUpdateView = addSpinnerPreference(
            "System Update Policy",
            "Configure how system updates are handled",
            updatePolicies,
            0,
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    boolean success = policyManager.setSystemUpdatePolicy(position);
                    showToast(success ? "System update policy updated" : "Failed to update");
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            }
        );
        systemUpdatePolicySpinner = systemUpdateView.findViewById(R.id.spinner);
        
        // Permission Policy
        String[] permissionPolicies = {"Prompt", "Auto Grant", "Auto Deny"};
        View permissionView = addSpinnerPreference(
            "Permission Policy",
            "How app permissions are handled",
            permissionPolicies,
            0,
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    boolean success = policyManager.setPermissionPolicy(position);
                    showToast(success ? "Permission policy updated" : "Failed to update");
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            }
        );
        permissionPolicySpinner = permissionView.findViewById(R.id.spinner);
        
        // ADB Enabled
        View adbView = addEditTextPreference(
            "ADB Enabled",
            "USB debugging setting (0=disabled, 1=enabled)",
            "0 or 1",
            "0",
            (v, hasFocus) -> {
                if (!hasFocus) {
                    EditText et = (EditText) v;
                    try {
                        int value = Integer.parseInt(et.getText().toString());
                        boolean success = policyManager.setGlobalSetting(android.provider.Settings.Global.ADB_ENABLED, value);
                        showToast(success ? "ADB setting updated" : "Failed to update");
                    } catch (NumberFormatException e) {
                        showToast("Invalid value (use 0 or 1)");
                    }
                }
            }
        );
        adbEnabledInput = adbView.findViewById(R.id.edittext);
        
        // USB Mass Storage
        View usbView = addEditTextPreference(
            "USB Mass Storage Enabled",
            "USB mass storage setting (0=disabled, 1=enabled)",
            "0 or 1",
            "0",
            (v, hasFocus) -> {
                if (!hasFocus) {
                    EditText et = (EditText) v;
                    try {
                        int value = Integer.parseInt(et.getText().toString());
                        boolean success = policyManager.setGlobalSetting(android.provider.Settings.Global.USB_MASS_STORAGE_ENABLED, value);
                        showToast(success ? "USB mass storage updated" : "Failed to update");
                    } catch (NumberFormatException e) {
                        showToast("Invalid value (use 0 or 1)");
                    }
                }
            }
        );
        usbMassStorageInput = usbView.findViewById(R.id.edittext);
    }
}

