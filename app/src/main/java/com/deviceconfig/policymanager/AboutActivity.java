package com.deviceconfig.policymanager;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends PolicyDetailActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void buildPreferences(String category) {
        // App Version
        addPreference("Application Version", getAppVersion(), null);
        
        // Open Source Licenses
        addClickablePreference("Open Source Licenses", "View third-party licenses", v -> showLicenses());
        
        // Contributors Header
        addHeaderPreference("Contributors");
        
        // Add your GitHub link here
        addClickablePreference("Dismal", "Original Creator", v -> openUrl("https://github.com/dismal002"));
        
        // Add contributor links (you can add more as needed)
        addClickablePreference("InsaaneUnicorn", "Tester", v -> openUrl("https://github.com/insaaneunicorn"));
        
        // Project Repository
        addHeaderPreference("Project");
        addClickablePreference("GitHub Repository", "View source", v -> openUrl("https://github.com/dismal002/device-config-manager"));
        
        // Policy Export
        addHeaderPreference("Scoring");
        addInfoPreference("Policy state is automatically exported every 15 minutes for scoring engines");
        addClickablePreference("Sync Now", "Manually export current policy state", v -> exportPolicyState());
        addClickablePreference("View Export Location", "Show file path", v -> showExportLocation());
    }
    
    private void exportPolicyState() {
        PolicyExporter exporter = new PolicyExporter(this);
        boolean success = exporter.exportPolicyState();
        if (success) {
            showToast("Policy state synced successfully");
        } else {
            showToast("Failed to sync policy state");
        }
    }
    
    private void showExportLocation() {
        PolicyExporter exporter = new PolicyExporter(this);
        String path = exporter.getExportFilePath();
        showToast("Export location: " + path);
    }
    
    private void addInfoPreference(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(32, 16, 32, 16);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        preferenceContainer.addView(textView);
    }
    
    private void addPreference(String title, String summary, View.OnClickListener listener) {
        View view = LayoutInflater.from(this).inflate(R.layout.preference, preferenceContainer, false);
        TextView titleView = view.findViewById(android.R.id.title);
        TextView summaryView = view.findViewById(android.R.id.summary);
        
        titleView.setText(title);
        if (summary != null && !summary.isEmpty()) {
            summaryView.setText(summary);
            summaryView.setVisibility(View.VISIBLE);
        }
        
        if (listener != null) {
            view.setOnClickListener(listener);
        } else {
            view.setClickable(false);
        }
        
        preferenceContainer.addView(view);
    }
    
    private void addClickablePreference(String title, String summary, View.OnClickListener listener) {
        addPreference(title, summary, listener);
    }
    
    private void addHeaderPreference(String title) {
        View view = LayoutInflater.from(this).inflate(R.layout.preference, preferenceContainer, false);
        TextView titleView = view.findViewById(android.R.id.title);
        
        titleView.setText(title);
        titleView.setTextAppearance(android.R.style.TextAppearance_Material_Subhead);
        view.setClickable(false);
        view.setPadding(view.getPaddingLeft(), 32, view.getPaddingRight(), 8);
        
        preferenceContainer.addView(view);
    }
    
    private String getAppVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName + " (" + pInfo.versionCode + ")";
        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    private void showLicenses() {
        Intent intent = new Intent(this, LicensesActivity.class);
        startActivity(intent);
    }
    
    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            showToast("Unable to open link");
        }
    }
}
