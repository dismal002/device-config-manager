package com.deviceconfig.policymanager;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class PolicySyncActivity extends PolicyDetailActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Policy Configuration");
        }
    }
    
    @Override
    protected void buildPreferences(String category) {
        boolean isDeviceOwner = policyManager.isDeviceOwner();
        boolean isAdmin = policyManager.isAdminActive();
        
        addInfoMessage("Policy configuration is stored at:\n/data/data/com.deviceconfig.policymanager/policy_state.json");
        
        if (isDeviceOwner) {
            addInfoMessage("\nYou are the Device Owner. Export your policies to share with other profiles.");
        } else if (isAdmin) {
            addInfoMessage("\nYou have Device Admin privileges. Import configuration to sync policies from the primary user.");
        } else {
            addInfoMessage("\nDevice Admin privileges required. Please enable Device Admin first.");
        }
        
        // Show current user info
        addHeaderMessage("Current User");
        try {
            android.os.Process.myUserHandle();
            addInfoMessage("User ID: " + android.os.Process.myUid() / 100000);
        } catch (Exception e) {
            addInfoMessage("Unable to determine user ID");
        }
        
        // Show profiles only for device owner
        if (isDeviceOwner) {
            addHeaderMessage("Managed Profiles");
            List<UserHandle> profiles = policyManager.getUserProfiles();
            if (profiles != null && !profiles.isEmpty()) {
                for (UserHandle handle : profiles) {
                    String userInfo = getUserInfo(handle);
                    addInfoMessage("• " + userInfo);
                }
                addInfoMessage("\nTotal profiles: " + profiles.size());
            } else {
                addInfoMessage("No additional profiles found.");
            }
        }
        
        addHeaderMessage("Configuration Management");
        
        // Export - only for device owner
        if (isDeviceOwner) {
            addClickablePreference(
                "Export Configuration Now",
                "Save current policies to shared config file",
                v -> exportConfiguration()
            );
        }
        
        // Import - available for all admins
        if (isAdmin) {
            addClickablePreference(
                "Import and Apply Configuration",
                "Load and apply policies from shared config",
                v -> importConfiguration()
            );
        }
        
        // View options - available to all
        addClickablePreference(
            "View Export File Path",
            "Show where configuration is saved",
            v -> showExportPath()
        );
        
        addClickablePreference(
            "View Current Configuration",
            "Display exported policy state",
            v -> viewConfiguration()
        );
    }
    
    private void addInfoMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(32, 16, 32, 16);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        preferenceContainer.addView(textView);
    }
    
    private void addHeaderMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(32, 24, 32, 8);
        textView.setTextSize(16);
        textView.setTextAppearance(android.R.style.TextAppearance_Material_Subhead);
        preferenceContainer.addView(textView);
    }
    
    private void addClickablePreference(String title, String summary, View.OnClickListener listener) {
        View view = addSwitchPreference(title, summary, false, null);
        view.setClickable(true);
        view.setOnClickListener(listener);
        // Hide the switch widget
        View switchWidget = view.findViewById(R.id.switchWidget);
        if (switchWidget != null) {
            switchWidget.setVisibility(View.GONE);
        }
    }
    
    private String getUserInfo(UserHandle handle) {
        try {
            java.lang.reflect.Method method = UserHandle.class.getMethod("getIdentifier");
            int userId = (Integer) method.invoke(handle);
            return "User ID: " + userId + (userId == 0 ? " (Primary)" : "");
        } catch (Exception e) {
            return "User Profile";
        }
    }
    
    private void exportConfiguration() {
        PolicyExporter exporter = new PolicyExporter(this);
        boolean success = exporter.exportPolicyState();
        if (success) {
            showToast("Configuration exported to shared location");
        } else {
            showToast("Failed to export configuration");
        }
    }
    
    private void importConfiguration() {
        PolicyImporter importer = new PolicyImporter(this);
        boolean success = importer.importAndApplyPolicies();
        if (success) {
            showToast("Configuration imported and applied successfully");
        } else {
            showToast("Failed to import configuration");
        }
    }
    
    private void showExportPath() {
        PolicyExporter exporter = new PolicyExporter(this);
        String path = exporter.getExportFilePath();
        showToast("Export path: " + path);
    }
    
    private void viewConfiguration() {
        try {
            PolicyExporter exporter = new PolicyExporter(this);
            java.io.File file = new java.io.File(exporter.getExportFilePath());
            
            if (!file.exists()) {
                showToast("Configuration file not found. Export first.");
                return;
            }
            
            // Read file content
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
            StringBuilder content = new StringBuilder();
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null && lineCount < 50) {
                content.append(line).append("\n");
                lineCount++;
            }
            reader.close();
            
            if (lineCount >= 50) {
                content.append("\n... (truncated, see full file at path above)");
            }
            
            // Show in dialog
            new android.app.AlertDialog.Builder(this)
                .setTitle("Policy Configuration")
                .setMessage(content.toString())
                .setPositiveButton("OK", null)
                .show();
                
        } catch (Exception e) {
            showToast("Error reading configuration: " + e.getMessage());
            android.util.Log.e("PolicySyncActivity", "Error reading config", e);
        }
    }
}
