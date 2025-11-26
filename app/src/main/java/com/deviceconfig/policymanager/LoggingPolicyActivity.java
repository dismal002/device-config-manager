package com.deviceconfig.policymanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

public class LoggingPolicyActivity extends PolicyDetailActivity {
    
    private Switch networkLoggingSwitch;
    private TextView securityLoggingStatus;
    
    @Override
    protected void buildPreferences(String category) {
        // Security Logging Status (read-only)
        View securityView = LayoutInflater.from(this).inflate(R.layout.preference, preferenceContainer, false);
        TextView titleView = securityView.findViewById(android.R.id.title);
        TextView summaryView = securityView.findViewById(android.R.id.summary);
        titleView.setText("Security Logging");
        summaryView.setText("Status: " + (policyManager.isSecurityLoggingEnabled() ? "Enabled" : "Disabled"));
        summaryView.setVisibility(View.VISIBLE);
        preferenceContainer.addView(securityView);
        securityLoggingStatus = summaryView;
        
        // Network Logging
        View networkView = addSwitchPreference(
            "Network Logging",
            "Enable network activity logging",
            false,
            v -> {
                Switch sw = v.findViewById(R.id.switchWidget);
                boolean success = policyManager.setNetworkLoggingEnabled(sw.isChecked());
                showToast(success ? "Network logging updated" : "Failed to update");
            }
        );
        networkLoggingSwitch = networkView.findViewById(R.id.switchWidget);
    }
}

