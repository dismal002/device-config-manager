package com.deviceconfig.policymanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PolicyDetailActivity extends AppCompatActivity {
    protected PolicyManager policyManager;
    protected LinearLayout preferenceContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_detail);
        
        try {
            policyManager = new PolicyManager(this);
        } catch (Exception e) {
            android.util.Log.e("PolicyDetailActivity", "Error initializing PolicyManager", e);
            Toast.makeText(this, "Error initializing policy manager", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        preferenceContainer = findViewById(R.id.preference_container);
        
        String category = getIntent().getStringExtra("CATEGORY");
        if (category != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(category);
            }
            try {
                buildPreferences(category);
            } catch (Exception e) {
                android.util.Log.e("PolicyDetailActivity", "Error building preferences for category: " + category, e);
                Toast.makeText(this, "Error loading preferences: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    protected void buildPreferences(String category) {
        // Override in subclasses
    }
    
    protected View addSwitchPreference(String title, String summary, boolean checked, View.OnClickListener listener) {
        try {
            View view = LayoutInflater.from(this).inflate(R.layout.preference_switch, preferenceContainer, false);
            TextView titleView = view.findViewById(android.R.id.title);
            TextView summaryView = view.findViewById(android.R.id.summary);
            Switch switchWidget = view.findViewById(R.id.switchWidget);
            
            if (titleView == null || summaryView == null || switchWidget == null) {
                android.util.Log.e("PolicyDetailActivity", "Missing views in preference_switch layout");
                return view;
            }
            
            titleView.setText(title);
            if (summary != null && !summary.isEmpty()) {
                summaryView.setText(summary);
                summaryView.setVisibility(View.VISIBLE);
            }
            switchWidget.setChecked(checked);
            if (listener != null) {
                switchWidget.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (listener != null) {
                        listener.onClick(view);
                    }
                });
            }
            
            preferenceContainer.addView(view);
            return view;
        } catch (Exception e) {
            android.util.Log.e("PolicyDetailActivity", "Error adding switch preference: " + title, e);
            return null;
        }
    }
    
    protected View addEditTextPreference(String title, String summary, String hint, String value, View.OnFocusChangeListener listener) {
        try {
            View view = LayoutInflater.from(this).inflate(R.layout.preference_edittext, preferenceContainer, false);
            TextView titleView = view.findViewById(android.R.id.title);
            TextView summaryView = view.findViewById(android.R.id.summary);
            EditText editText = view.findViewById(R.id.edittext);
            
            if (titleView == null || summaryView == null || editText == null) {
                android.util.Log.e("PolicyDetailActivity", "Missing views in preference_edittext layout");
                return view;
            }
            
            titleView.setText(title);
            if (summary != null && !summary.isEmpty()) {
                summaryView.setText(summary);
                summaryView.setVisibility(View.VISIBLE);
            }
            if (hint != null) {
                editText.setHint(hint);
            }
            if (value != null) {
                editText.setText(value);
            }
            if (listener != null) {
                editText.setOnFocusChangeListener(listener);
            }
            
            preferenceContainer.addView(view);
            return view;
        } catch (Exception e) {
            android.util.Log.e("PolicyDetailActivity", "Error adding edittext preference: " + title, e);
            return null;
        }
    }
    
    protected View addSpinnerPreference(String title, String summary, String[] items, int selectedIndex, AdapterView.OnItemSelectedListener listener) {
        try {
            View view = LayoutInflater.from(this).inflate(R.layout.preference_spinner, preferenceContainer, false);
            TextView titleView = view.findViewById(android.R.id.title);
            TextView summaryView = view.findViewById(android.R.id.summary);
            Spinner spinner = view.findViewById(R.id.spinner);
            
            if (titleView == null || summaryView == null || spinner == null) {
                android.util.Log.e("PolicyDetailActivity", "Missing views in preference_spinner layout");
                return view;
            }
            
            titleView.setText(title);
            if (summary != null && !summary.isEmpty()) {
                summaryView.setText(summary);
                summaryView.setVisibility(View.VISIBLE);
            }
            
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            if (selectedIndex >= 0 && selectedIndex < items.length) {
                spinner.setSelection(selectedIndex);
            }
            if (listener != null) {
                spinner.setOnItemSelectedListener(listener);
            }
            
            preferenceContainer.addView(view);
            return view;
        } catch (Exception e) {
            android.util.Log.e("PolicyDetailActivity", "Error adding spinner preference: " + title, e);
            return null;
        }
    }
    
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

