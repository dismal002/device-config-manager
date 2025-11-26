package com.deviceconfig.policymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Switch;

public class WirelessNetworkingActivity extends PolicyDetailActivity {
    
    private Switch noConfigWifiSwitch;
    private Switch noChangeWifiStateSwitch;
    private Switch noAirplaneModeSwitch;
    private Switch noCellular2gSwitch;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Wireless Networking");
        }
    }
    
    @Override
    protected void buildPreferences(String category) {
        // Disable WiFi Configuration
        View wifiConfigView = addSwitchPreference(
            "Disable WiFi Configuration",
            "Prevent users from configuring WiFi networks",
            policyManager.getNoConfigWifi(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setNoConfigWifi(sw.isChecked());
                showToast(success ? "WiFi configuration policy updated" : "Failed to update WiFi policy");
            }
        );
        noConfigWifiSwitch = wifiConfigView.findViewById(R.id.switchWidget);
        
        // Disable WiFi State Change
        View wifiStateView = addSwitchPreference(
            "Disable WiFi State Change",
            "Prevent users from toggling WiFi on/off",
            policyManager.getNoChangeWifiState(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setNoChangeWifiState(sw.isChecked());
                showToast(success ? "WiFi state policy updated" : "Failed to update WiFi state policy");
            }
        );
        noChangeWifiStateSwitch = wifiStateView.findViewById(R.id.switchWidget);
        
        // Disable Airplane Mode
        View airplaneModeView = addSwitchPreference(
            "Disable Airplane Mode",
            "Prevent users from toggling airplane mode",
            policyManager.getNoAirplaneMode(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setNoAirplaneMode(sw.isChecked());
                showToast(success ? "Airplane mode policy updated" : "Failed to update airplane mode policy");
            }
        );
        noAirplaneModeSwitch = airplaneModeView.findViewById(R.id.switchWidget);
        
        // Disable 2G Cellular
        View cellular2gView = addSwitchPreference(
            "Disable 2G Cellular",
            "Prevent use of 2G cellular networks",
            policyManager.getNoCellular2g(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setNoCellular2g(sw.isChecked());
                showToast(success ? "2G cellular policy updated" : "Failed to update 2G cellular policy");
            }
        );
        noCellular2gSwitch = cellular2gView.findViewById(R.id.switchWidget);
        
        // Disable Bluetooth
        View bluetoothView = addSwitchPreference(
            "Disable Bluetooth",
            "Prevent Bluetooth connectivity",
            policyManager.getDisallowBluetooth(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowBluetooth(sw.isChecked());
                showToast(success ? "Bluetooth policy updated" : "Failed to update Bluetooth policy");
            }
        );
        
        // Disable Tethering Configuration
        View tetheringView = addSwitchPreference(
            "Disable Tethering Configuration",
            "Prevent users from configuring tethering",
            policyManager.getDisallowConfigTethering(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowConfigTethering(sw.isChecked());
                showToast(success ? "Tethering configuration policy updated" : "Failed to update tethering policy");
            }
        );
        
        // Disable WiFi Tethering
        View wifiTetheringView = addSwitchPreference(
            "Disable WiFi Tethering",
            "Prevent WiFi hotspot sharing",
            policyManager.getDisallowWifiTethering(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowWifiTethering(sw.isChecked());
                showToast(success ? "WiFi tethering policy updated" : "Failed to update WiFi tethering policy");
            }
        );
        
        // Disable Mobile Networks Configuration
        View mobileNetworksView = addSwitchPreference(
            "Disable Mobile Networks Configuration",
            "Prevent users from configuring mobile networks",
            policyManager.getDisallowConfigMobileNetworks(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowConfigMobileNetworks(sw.isChecked());
                showToast(success ? "Mobile networks policy updated" : "Failed to update mobile networks policy");
            }
        );
        
        // Disable Private DNS Configuration
        View privateDnsView = addSwitchPreference(
            "Disable Private DNS Configuration",
            "Prevent users from configuring private DNS",
            policyManager.getDisallowConfigPrivateDns(),
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setDisallowConfigPrivateDns(sw.isChecked());
                showToast(success ? "Private DNS policy updated" : "Failed to update private DNS policy");
            }
        );
    }
}
