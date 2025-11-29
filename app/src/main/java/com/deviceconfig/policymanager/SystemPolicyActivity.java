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
    private boolean isLoadingValues = true;
    
    private AdapterView.OnItemSelectedListener systemUpdateListener;
    private AdapterView.OnItemSelectedListener permissionPolicyListener;
    
    @Override
    protected void buildPreferences(String category) {
        // Create listeners first (but don't attach yet)
        systemUpdateListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isLoadingValues) {
                    android.util.Log.d("SystemPolicy", "System update policy changed by user to position: " + position);
                    boolean success = policyManager.setSystemUpdatePolicy(position);
                    showToast(success ? "System update policy updated" : "Failed to update");
                } else {
                    android.util.Log.d("SystemPolicy", "Ignoring system update policy change during load (position: " + position + ")");
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        
        permissionPolicyListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isLoadingValues) {
                    android.util.Log.d("SystemPolicy", "Permission policy changed by user to position: " + position);
                    boolean success = policyManager.setPermissionPolicy(position);
                    showToast(success ? "Permission policy updated" : "Failed to update");
                } else {
                    android.util.Log.d("SystemPolicy", "Ignoring permission policy change during load (position: " + position + ")");
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        
        // System Update Policy - create without setting initial value or listener
        String[] updatePolicies = {"Automatic", "Windowed", "Postponed"};
        View systemUpdateView = addSpinnerPreference(
            "System Update Policy",
            "Configure how system updates are handled",
            updatePolicies,
            -1,  // Don't set initial selection - will be loaded from actual policy
            null  // Don't set listener yet
        );
        systemUpdatePolicySpinner = systemUpdateView.findViewById(R.id.spinner);
        
        // Permission Policy - create without setting initial value or listener
        String[] permissionPolicies = {"Prompt", "Auto Grant", "Auto Deny"};
        View permissionView = addSpinnerPreference(
            "Permission Policy",
            "How app permissions are handled",
            permissionPolicies,
            -1,  // Don't set initial selection - will be loaded from actual policy
            null  // Don't set listener yet
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
        
        // Load current values after a short delay to ensure UI is ready
        // Load policies first, then attach listeners AFTER loading completes
        systemUpdatePolicySpinner.post(() -> {
            loadCurrentPolicies();
        });
    }
    
    private void loadCurrentPolicies() {
        android.util.Log.d("SystemPolicy", "=== loadCurrentPolicies START ===");
        
        // CRITICAL: Set loading flag FIRST before any UI operations
        isLoadingValues = true;
        
        // Make absolutely sure no listeners are attached
        systemUpdatePolicySpinner.setOnItemSelectedListener(null);
        permissionPolicySpinner.setOnItemSelectedListener(null);
        
        try {
            android.app.admin.DevicePolicyManager dpm = (android.app.admin.DevicePolicyManager) 
                getSystemService(DEVICE_POLICY_SERVICE);
            android.content.ComponentName adminComponent = new android.content.ComponentName(
                this, DeviceAdminReceiver.class);
            
            // Load system update policy
            int systemUpdatePosition = 0; // default to Automatic
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                android.app.admin.SystemUpdatePolicy policy = dpm.getSystemUpdatePolicy();
                if (policy != null) {
                    int policyType = policy.getPolicyType();
                    android.util.Log.d("SystemPolicy", "System update policy type from system: " + policyType);
                    switch (policyType) {
                        case android.app.admin.SystemUpdatePolicy.TYPE_INSTALL_AUTOMATIC:
                            systemUpdatePosition = 0;
                            break;
                        case android.app.admin.SystemUpdatePolicy.TYPE_INSTALL_WINDOWED:
                            systemUpdatePosition = 1;
                            break;
                        case android.app.admin.SystemUpdatePolicy.TYPE_POSTPONE:
                            systemUpdatePosition = 2;
                            break;
                        default:
                            android.util.Log.w("SystemPolicy", "Unknown policy type: " + policyType + ", using default");
                            systemUpdatePosition = 0;
                            break;
                    }
                } else {
                    android.util.Log.d("SystemPolicy", "No system update policy set, using default (Automatic)");
                }
            }
            
            // Load permission policy
            int permissionPolicy = dpm.getPermissionPolicy(adminComponent);
            android.util.Log.d("SystemPolicy", "Permission policy from system: " + permissionPolicy);
            
            // Validate permission policy value
            if (permissionPolicy < 0 || permissionPolicy > 2) {
                android.util.Log.w("SystemPolicy", "Invalid permission policy: " + permissionPolicy + ", using default (Prompt)");
                permissionPolicy = 0;
            }
            
            android.util.Log.d("SystemPolicy", "Will set spinners to - System Update: " + systemUpdatePosition + ", Permission: " + permissionPolicy);
            
            // Set spinner selections WITHOUT triggering callbacks
            // The 'false' parameter means no animation
            systemUpdatePolicySpinner.setSelection(systemUpdatePosition, false);
            permissionPolicySpinner.setSelection(permissionPolicy, false);
            
            android.util.Log.d("SystemPolicy", "Spinner selections applied");
            
            // Load ADB setting
            try {
                int adbEnabled = android.provider.Settings.Global.getInt(
                    getContentResolver(), 
                    android.provider.Settings.Global.ADB_ENABLED, 
                    0
                );
                adbEnabledInput.setText(String.valueOf(adbEnabled));
                android.util.Log.d("SystemPolicy", "ADB setting loaded: " + adbEnabled);
            } catch (Exception e) {
                android.util.Log.e("SystemPolicy", "Error loading ADB setting", e);
            }
            
            // Load USB mass storage setting
            try {
                int usbEnabled = android.provider.Settings.Global.getInt(
                    getContentResolver(), 
                    android.provider.Settings.Global.USB_MASS_STORAGE_ENABLED, 
                    0
                );
                usbMassStorageInput.setText(String.valueOf(usbEnabled));
                android.util.Log.d("SystemPolicy", "USB setting loaded: " + usbEnabled);
            } catch (Exception e) {
                android.util.Log.e("SystemPolicy", "Error loading USB setting", e);
            }
            
        } catch (Exception e) {
            android.util.Log.e("SystemPolicy", "Error in loadCurrentPolicies", e);
        }
        
        // Wait for UI to settle, then attach listeners
        // This delay ensures setSelection() completes before any user interaction
        systemUpdatePolicySpinner.postDelayed(() -> {
            android.util.Log.d("SystemPolicy", "Attaching listeners now");
            systemUpdatePolicySpinner.setOnItemSelectedListener(systemUpdateListener);
            permissionPolicySpinner.setOnItemSelectedListener(permissionPolicyListener);
            isLoadingValues = false;
            android.util.Log.d("SystemPolicy", "=== loadCurrentPolicies COMPLETE - Ready for user input ===");
        }, 500); // Increased delay to 500ms to ensure everything settles
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload policies when returning to this screen
        // Note: loadCurrentPolicies() handles listener attachment, so we don't do it here
        if (systemUpdatePolicySpinner != null && permissionPolicySpinner != null) {
            android.util.Log.d("SystemPolicy", "onResume - reloading policies");
            systemUpdatePolicySpinner.post(() -> {
                loadCurrentPolicies();
            });
        }
    }
}

