package com.deviceconfig.policymanager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class UserRestrictionsActivity extends PolicyDetailActivity {
    
    @Override
    protected void buildPreferences(String category) {
        // Apps & Installations
        addSwitchPreference("No Modify Accounts", "Prevent account changes", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_MODIFY_ACCOUNTS, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Install Apps", "Block app installation", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_INSTALL_APPS, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Uninstall Apps", "Block app uninstallation", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_UNINSTALL_APPS, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Control Apps", "Block app control", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_APPS_CONTROL, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Install Unknown Sources", "Block unknown sources", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Install Unknown Sources Globally", "Global unknown sources block", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES_GLOBALLY, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        // Network & Connectivity
        addSwitchPreference("No Config WiFi", "Block WiFi configuration", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_CONFIG_WIFI, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Change WiFi State", "Block WiFi state changes", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_CHANGE_WIFI_STATE, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Share Location", "Block location sharing", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_SHARE_LOCATION, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Airplane Mode", "Block airplane mode", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_AIRPLANE_MODE, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No USB File Transfer", "Block USB transfers", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_USB_FILE_TRANSFER, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            addSwitchPreference("No Cellular 2G", "Block 2G cellular", false, v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_CELLULAR_2G, sw.isChecked());
                showToast(success ? "Updated" : "Failed");
            });
        }
        
        // Privacy & Security
        addSwitchPreference("No Factory Reset", "Block factory reset", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_FACTORY_RESET, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Wallpaper", "Block wallpaper changes", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_SET_WALLPAPER, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Fun", "Block easter egg", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_FUN, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Autofill", "Block autofill", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_AUTOFILL, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Printing", "Block printing", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_PRINTING, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Config Screen Timeout", "Block timeout changes", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_CONFIG_SCREEN_TIMEOUT, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        // Developer Options
        addSwitchPreference("Disallow Debugging", "Block debugging features", false, v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_DEBUGGING_FEATURES, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
    }
}

