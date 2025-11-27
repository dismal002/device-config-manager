package com.deviceconfig.policymanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class PasswordSettingsActivity extends PolicyDetailActivity {
    
    private Spinner passwordQualitySpinner;
    private EditText passwordMinLengthInput;
    private EditText passwordMinNumericInput;
    private EditText passwordHistoryLengthInput;
    private EditText passwordExpirationInput;
    private EditText maxFailedPasswordsInput;
    private int selectedPasswordQuality = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Password Settings");
        }
    }
    
    private void addInfoMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(32, 24, 32, 24);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        preferenceContainer.addView(textView);
    }
    
    @Override
    protected void buildPreferences(String category) {
        // Check admin status
        if (!policyManager.isAdminActive()) {
            addInfoMessage("Device Admin privileges required to set password policies. Please enable Device Admin first.");
            return;
        }
        
        addInfoMessage("Configure password requirements for the device. Changes apply when you tap 'Apply Password Policies'.");
        
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
                    selectedPasswordQuality = position;
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
            null
        );
        passwordMinLengthInput = passwordMinLengthView.findViewById(R.id.edittext);
        
        // Password Minimum Numeric
        View passwordMinNumericView = addEditTextPreference(
            "Minimum Numeric Characters",
            "Minimum number of numeric characters",
            "Enter minimum numeric count",
            "0",
            null
        );
        passwordMinNumericInput = passwordMinNumericView.findViewById(R.id.edittext);
        
        // Password History Length
        View passwordHistoryView = addEditTextPreference(
            "Password History Length",
            "Number of previous passwords to remember",
            "Enter history length",
            "0",
            null
        );
        passwordHistoryLengthInput = passwordHistoryView.findViewById(R.id.edittext);
        
        // Password Expiration Timeout
        View passwordExpirationView = addEditTextPreference(
            "Password Expiration Timeout (ms)",
            "Milliseconds until password expires (0 = never)",
            "Enter milliseconds",
            "0",
            null
        );
        passwordExpirationInput = passwordExpirationView.findViewById(R.id.edittext);
        
        // Max Failed Passwords
        View maxFailedView = addEditTextPreference(
            "Maximum Failed Attempts",
            "Number of failed attempts before device wipe",
            "Enter max attempts",
            "0",
            null
        );
        maxFailedPasswordsInput = maxFailedView.findViewById(R.id.edittext);
        
        // Apply Policies Button
        View applyButtonView = LayoutInflater.from(this).inflate(R.layout.preference, preferenceContainer, false);
        TextView applyButtonTitle = applyButtonView.findViewById(android.R.id.title);
        applyButtonTitle.setText("Apply Password Policies");
        applyButtonView.setOnClickListener(v -> applyPasswordPolicies());
        preferenceContainer.addView(applyButtonView);
    }
    
    private void applyPasswordPolicies() {
        boolean allSuccess = true;
        
        // Apply Password Quality using literal values to avoid constant resolution issues
        int quality = 0;
        switch (selectedPasswordQuality) {
            case 0: 
                quality = 0x00000; // PASSWORD_QUALITY_UNSPECIFIED
                break;
            case 1: 
                quality = 0x10000; // PASSWORD_QUALITY_SOMETHING
                break;
            case 2: 
                quality = 0x30000; // PASSWORD_QUALITY_NUMERIC_COMPLEX
                break;
            case 3: 
                quality = 0x50000; // PASSWORD_QUALITY_ALPHANUMERIC
                break;
            case 4: 
                quality = 0x60000; // PASSWORD_QUALITY_COMPLEX
                break;
        }
        if (!policyManager.setPasswordQuality(quality)) {
            allSuccess = false;
        }
        
        // Apply Password Minimum Length
        try {
            int length = Integer.parseInt(passwordMinLengthInput.getText().toString());
            if (!policyManager.setPasswordMinimumLength(length)) {
                allSuccess = false;
            }
        } catch (NumberFormatException e) {
            showToast("Invalid minimum length value");
            allSuccess = false;
        }
        
        // Apply Password Minimum Numeric
        try {
            int numeric = Integer.parseInt(passwordMinNumericInput.getText().toString());
            if (!policyManager.setPasswordMinimumNumeric(numeric)) {
                allSuccess = false;
            }
        } catch (NumberFormatException e) {
            showToast("Invalid minimum numeric value");
            allSuccess = false;
        }
        
        // Apply Password History Length
        try {
            int history = Integer.parseInt(passwordHistoryLengthInput.getText().toString());
            if (!policyManager.setPasswordHistoryLength(history)) {
                allSuccess = false;
            }
        } catch (NumberFormatException e) {
            showToast("Invalid history length value");
            allSuccess = false;
        }
        
        // Apply Password Expiration Timeout
        try {
            long timeout = Long.parseLong(passwordExpirationInput.getText().toString());
            if (!policyManager.setPasswordExpirationTimeout(timeout)) {
                allSuccess = false;
            }
        } catch (NumberFormatException e) {
            showToast("Invalid expiration timeout value");
            allSuccess = false;
        }
        
        // Apply Max Failed Passwords
        try {
            int maxFailed = Integer.parseInt(maxFailedPasswordsInput.getText().toString());
            if (!policyManager.setMaximumFailedPasswordsForWipe(maxFailed)) {
                allSuccess = false;
            }
        } catch (NumberFormatException e) {
            showToast("Invalid max failed attempts value");
            allSuccess = false;
        }
        
        if (allSuccess) {
            showToast("All password policies applied successfully");
        } else {
            showToast("Some policies failed to apply. Check device admin status.");
        }
    }
}
