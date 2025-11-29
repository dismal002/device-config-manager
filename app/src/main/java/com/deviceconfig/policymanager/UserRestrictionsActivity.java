package com.deviceconfig.policymanager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class UserRestrictionsActivity extends PolicyDetailActivity {
    
    @Override
    protected void buildPreferences(String category) {
        // Apps & Installations
        addSwitchPreference("No Modify Accounts", "Prevent account changes", 
            getUserRestriction(android.os.UserManager.DISALLOW_MODIFY_ACCOUNTS), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_MODIFY_ACCOUNTS, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Install Apps", "Block app installation", 
            getUserRestriction(android.os.UserManager.DISALLOW_INSTALL_APPS), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_INSTALL_APPS, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Uninstall Apps", "Block app uninstallation", 
            getUserRestriction(android.os.UserManager.DISALLOW_UNINSTALL_APPS), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_UNINSTALL_APPS, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Control Apps", "Block app control", 
            getUserRestriction(android.os.UserManager.DISALLOW_APPS_CONTROL), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_APPS_CONTROL, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Install Unknown Sources", "Block unknown sources", 
            getUserRestriction(android.os.UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Install Unknown Sources Globally", "Global unknown sources block", 
            getUserRestriction(android.os.UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES_GLOBALLY), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES_GLOBALLY, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        // Network & Connectivity
        addSwitchPreference("No Config WiFi", "Block WiFi configuration", 
            policyManager.getNoConfigWifi(), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_CONFIG_WIFI, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Change WiFi State", "Block WiFi state changes", 
            policyManager.getNoChangeWifiState(), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_CHANGE_WIFI_STATE, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Share Location", "Block location sharing", 
            getUserRestriction(android.os.UserManager.DISALLOW_SHARE_LOCATION), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_SHARE_LOCATION, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Airplane Mode", "Block airplane mode", 
            policyManager.getNoAirplaneMode(), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_AIRPLANE_MODE, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No USB File Transfer", "Block USB transfers", 
            policyManager.getDisallowUsbFileTransfer(), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_USB_FILE_TRANSFER, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            addSwitchPreference("No Cellular 2G", "Block 2G cellular", 
                policyManager.getNoCellular2g(), v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_CELLULAR_2G, sw.isChecked());
                showToast(success ? "Updated" : "Failed");
            });
        }
        
        // Privacy & Security
        addSwitchPreference("No Factory Reset", "Block factory reset", 
            getUserRestriction(android.os.UserManager.DISALLOW_FACTORY_RESET), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_FACTORY_RESET, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Wallpaper", "Block wallpaper changes", 
            getUserRestriction(android.os.UserManager.DISALLOW_SET_WALLPAPER), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_SET_WALLPAPER, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Fun", "Block easter egg", 
            getUserRestriction(android.os.UserManager.DISALLOW_FUN), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_FUN, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Autofill", "Block autofill", 
            getUserRestriction(android.os.UserManager.DISALLOW_AUTOFILL), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_AUTOFILL, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Printing", "Block printing", 
            policyManager.getNoPrinting(), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_PRINTING, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        addSwitchPreference("No Config Screen Timeout", "Block timeout changes", 
            getUserRestriction(android.os.UserManager.DISALLOW_CONFIG_SCREEN_TIMEOUT), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_CONFIG_SCREEN_TIMEOUT, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
        
        // Developer Options
        addSwitchPreference("Disallow Debugging", "Block debugging features", 
            policyManager.getDisallowDebugging(), v -> {
            Switch sw = v.findViewById(R.id.switchWidget);
            boolean success = policyManager.setUserRestriction(android.os.UserManager.DISALLOW_DEBUGGING_FEATURES, sw.isChecked());
            showToast(success ? "Updated" : "Failed");
        });
    }
    
    private boolean getUserRestriction(String restriction) {
        try {
            android.os.UserManager um = (android.os.UserManager) getSystemService(android.content.Context.USER_SERVICE);
            return um.hasUserRestriction(restriction);
        } catch (Exception e) {
            return false;
        }
    }
}



