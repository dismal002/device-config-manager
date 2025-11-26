package com.deviceconfig.policymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

public class SecurityPolicyActivity extends PolicyDetailActivity {
    
    private Switch disableCameraSwitch;
    private Switch disableScreenCaptureSwitch;
    private Spinner passwordQualitySpinner;
    private EditText passwordMinLengthInput;
    private EditText passwordMinNumericInput;
    private EditText passwordHistoryLengthInput;
    private EditText passwordExpirationInput;
    private EditText maxFailedPasswordsInput;
    
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
        
        // Password Quality
        String[] passwordQualities = {"Unspecified", "Something", "Numeric Complex", "Alphanumeric", "Complex"};
        View passwordQualityView = addSpinnerPreference(
            "Password Quality",
            "Set the required password quality",
            passwordQualities,
            0,
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int quality = 0;
                    switch (position) {
                        case 0: quality = android.app.admin.DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED; break;
                        case 1: quality = android.app.admin.DevicePolicyManager.PASSWORD_QUALITY_SOMETHING; break;
                        case 2: quality = android.app.admin.DevicePolicyManager.PASSWORD_QUALITY_NUMERIC_COMPLEX; break;
                        case 3: quality = android.app.admin.DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC; break;
                        case 4: quality = android.app.admin.DevicePolicyManager.PASSWORD_QUALITY_COMPLEX; break;
                    }
                    boolean success = policyManager.setPasswordQuality(quality);
                    showToast(success ? "Password quality updated" : "Failed to update password quality");
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            }
        );
        passwordQualitySpinner = passwordQualityView.findViewById(R.id.spinner);
        
        // Password Minimum Length
        View passwordMinLengthView = addEditTextPreference(
            "Password Minimum Length",
            "Minimum number of characters required",
            "Enter minimum length",
            "0",
            (v, hasFocus) -> {
                if (!hasFocus) {
                    EditText et = (EditText) v;
                    try {
                        int length = Integer.parseInt(et.getText().toString());
                        boolean success = policyManager.setPasswordMinimumLength(length);
                        showToast(success ? "Password min length updated" : "Failed to update");
                    } catch (NumberFormatException e) {
                        showToast("Invalid number");
                    }
                }
            }
        );
        passwordMinLengthInput = passwordMinLengthView.findViewById(R.id.edittext);
        
        // Password Minimum Numeric
        View passwordMinNumericView = addEditTextPreference(
            "Password Minimum Numeric",
            "Minimum number of numeric characters required",
            "Enter minimum numeric",
            "0",
            (v, hasFocus) -> {
                if (!hasFocus) {
                    EditText et = (EditText) v;
                    try {
                        int numeric = Integer.parseInt(et.getText().toString());
                        boolean success = policyManager.setPasswordMinimumNumeric(numeric);
                        showToast(success ? "Password min numeric updated" : "Failed to update");
                    } catch (NumberFormatException e) {
                        showToast("Invalid number");
                    }
                }
            }
        );
        passwordMinNumericInput = passwordMinNumericView.findViewById(R.id.edittext);
        
        // Password History Length
        View passwordHistoryView = addEditTextPreference(
            "Password History Length",
            "Number of previous passwords to remember",
            "Enter history length",
            "0",
            (v, hasFocus) -> {
                if (!hasFocus) {
                    EditText et = (EditText) v;
                    try {
                        int length = Integer.parseInt(et.getText().toString());
                        boolean success = policyManager.setPasswordHistoryLength(length);
                        showToast(success ? "Password history updated" : "Failed to update");
                    } catch (NumberFormatException e) {
                        showToast("Invalid number");
                    }
                }
            }
        );
        passwordHistoryLengthInput = passwordHistoryView.findViewById(R.id.edittext);
        
        // Password Expiration Timeout
        View passwordExpirationView = addEditTextPreference(
            "Password Expiration Timeout",
            "Time in milliseconds before password expires",
            "Enter timeout (ms)",
            "0",
            (v, hasFocus) -> {
                if (!hasFocus) {
                    EditText et = (EditText) v;
                    try {
                        long timeout = Long.parseLong(et.getText().toString());
                        boolean success = policyManager.setPasswordExpirationTimeout(timeout);
                        showToast(success ? "Password expiration updated" : "Failed to update");
                    } catch (NumberFormatException e) {
                        showToast("Invalid number");
                    }
                }
            }
        );
        passwordExpirationInput = passwordExpirationView.findViewById(R.id.edittext);
        
        // Max Failed Passwords for Wipe
        View maxFailedView = addEditTextPreference(
            "Max Failed Passwords for Wipe",
            "Number of failed password attempts before device wipe",
            "Enter max failed attempts",
            "0",
            (v, hasFocus) -> {
                if (!hasFocus) {
                    EditText et = (EditText) v;
                    try {
                        int max = Integer.parseInt(et.getText().toString());
                        boolean success = policyManager.setMaximumFailedPasswordsForWipe(max);
                        showToast(success ? "Max failed passwords updated" : "Failed to update");
                    } catch (NumberFormatException e) {
                        showToast("Invalid number");
                    }
                }
            }
        );
        maxFailedPasswordsInput = maxFailedView.findViewById(R.id.edittext);
    }
}

