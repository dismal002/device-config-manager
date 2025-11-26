package com.deviceconfig.policymanager;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfileActivity extends PolicyDetailActivity {
    
    private EditText userProfileNameInput;
    private LinearLayout userProfilesContainer;
    private DevicePolicyManager dpm;
    private ComponentName adminComponent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, DeviceAdminReceiver.class);
    }
    
    @Override
    protected void buildPreferences(String category) {
        // Create User Profile
        View createView = LayoutInflater.from(this).inflate(R.layout.preference_edittext, preferenceContainer, false);
        TextView titleView = createView.findViewById(android.R.id.title);
        EditText editText = createView.findViewById(R.id.edittext);
        titleView.setText("User Profile Name");
        editText.setHint("Enter user profile name");
        preferenceContainer.addView(createView);
        userProfileNameInput = editText;
        
        // Create Button
        View buttonView = LayoutInflater.from(this).inflate(R.layout.preference, preferenceContainer, false);
        TextView buttonTitle = buttonView.findViewById(android.R.id.title);
        buttonTitle.setText("Create Profile");
        buttonView.setOnClickListener(v -> createUserProfile());
        preferenceContainer.addView(buttonView);
        
        // User Profiles List Header
        View listHeaderView = LayoutInflater.from(this).inflate(R.layout.preference, preferenceContainer, false);
        TextView listHeaderTitle = listHeaderView.findViewById(android.R.id.title);
        listHeaderTitle.setText("Existing User Profiles");
        preferenceContainer.addView(listHeaderView);
        
        // Container for user profiles
        userProfilesContainer = new LinearLayout(this);
        userProfilesContainer.setOrientation(LinearLayout.VERTICAL);
        preferenceContainer.addView(userProfilesContainer);
        
        refreshUserProfilesList();
    }
    
    private void createUserProfile() {
        android.util.Log.d("UserProfileActivity", "createUserProfile button clicked");
        
        String userName = userProfileNameInput.getText().toString().trim();
        android.util.Log.d("UserProfileActivity", "User name entered: " + userName);
        
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user profile name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        boolean isDeviceOwner = policyManager.isDeviceOwner();
        android.util.Log.d("UserProfileActivity", "Is device owner: " + isDeviceOwner);
        
        if (!isDeviceOwner) {
            Toast.makeText(this, "Device Owner privileges required to create user profiles", Toast.LENGTH_LONG).show();
            return;
        }
        
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            Toast.makeText(this, "User profile creation requires Android 7.0+", Toast.LENGTH_LONG).show();
            return;
        }
        
        android.util.Log.d("UserProfileActivity", "Calling policyManager.createUserProfile");
        
        try {
            android.os.UserHandle userHandle = policyManager.createUserProfile(userName);
            android.util.Log.d("UserProfileActivity", "createUserProfile returned: " + (userHandle != null ? "UserHandle" : "null"));
            
            if (userHandle != null) {
                Toast.makeText(this, "User profile created: " + userName, Toast.LENGTH_SHORT).show();
                userProfileNameInput.setText("");
                refreshUserProfilesList();
            } else {
                Toast.makeText(this, "Failed to create user profile. Check logcat for details.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            android.util.Log.e("UserProfileActivity", "Error creating user profile", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void refreshUserProfilesList() {
        userProfilesContainer.removeAllViews();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            java.util.List<android.os.UserHandle> profiles = policyManager.getUserProfiles();
            if (profiles != null && !profiles.isEmpty()) {
                for (android.os.UserHandle handle : profiles) {
                    if (handle != null) {
                        addProfileItemView(handle);
                    }
                }
            } else {
                TextView emptyView = new TextView(this);
                emptyView.setText("No user profiles found");
                emptyView.setPadding(16, 16, 16, 16);
                userProfilesContainer.addView(emptyView);
            }
        } else {
            TextView apiView = new TextView(this);
            apiView.setText("User profile management requires API 24+");
            apiView.setPadding(16, 16, 16, 16);
            userProfilesContainer.addView(apiView);
        }
    }
    
    private void addProfileItemView(android.os.UserHandle handle) {
        View profileView = LayoutInflater.from(this).inflate(R.layout.preference, userProfilesContainer, false);
        TextView titleView = profileView.findViewById(android.R.id.title);
        TextView summaryView = profileView.findViewById(android.R.id.summary);
        
        final String userInfo = getUserInfo(handle);
        final boolean isOwner = isOwnerUser(handle);
        
        titleView.setText(userInfo);
        
        if (isOwner) {
            summaryView.setText("Owner user (cannot be deleted)");
            summaryView.setVisibility(View.VISIBLE);
            profileView.setClickable(false);
            profileView.setAlpha(0.6f);
        } else {
            summaryView.setText("Tap to delete this profile");
            summaryView.setVisibility(View.VISIBLE);
            final android.os.UserHandle finalHandle = handle;
            profileView.setOnClickListener(v -> deleteUserProfile(finalHandle, userInfo));
        }
        
        userProfilesContainer.addView(profileView);
    }
    
    private void deleteUserProfile(android.os.UserHandle handle, String userInfo) {
        // Prevent deletion of owner user
        if (isOwnerUser(handle)) {
            Toast.makeText(this, "Cannot delete the owner user", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!policyManager.isDeviceOwner()) {
            Toast.makeText(this, "Device Owner privileges required", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show confirmation dialog
        new android.app.AlertDialog.Builder(this)
            .setTitle("Delete User Profile")
            .setMessage("Are you sure you want to delete this user profile?\n\n" + userInfo)
            .setPositiveButton("Delete", (dialog, which) -> {
                boolean success = policyManager.removeUserProfile(handle);
                if (success) {
                    Toast.makeText(this, "User profile deleted: " + userInfo, Toast.LENGTH_SHORT).show();
                    refreshUserProfilesList();
                } else {
                    Toast.makeText(this, "Failed to delete user profile", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private String getUserInfo(android.os.UserHandle handle) {
        try {
            java.lang.reflect.Method method = android.os.UserHandle.class.getMethod("getIdentifier");
            int userId = (Integer) method.invoke(handle);
            
            android.util.Log.d("UserProfileActivity", "getUserInfo called for user ID: " + userId);
            
            if (userId == 0) {
                return "Owner (ID: " + userId + ")";
            }
            
            // Try to get the user name from PolicyManager
            String userName = policyManager.getUserName(handle);
            android.util.Log.d("UserProfileActivity", "getUserName returned: " + userName);
            
            if (userName != null && !userName.isEmpty()) {
                return userName + " (ID: " + userId + ")";
            }
            
            return "User " + userId;
        } catch (Exception e) {
            android.util.Log.e("UserProfileActivity", "Error getting user info", e);
            return "User Profile";
        }
    }
    
    private boolean isOwnerUser(android.os.UserHandle handle) {
        try {
            java.lang.reflect.Method method = android.os.UserHandle.class.getMethod("getIdentifier");
            int userId = (Integer) method.invoke(handle);
            return userId == 0; // User ID 0 is always the owner
        } catch (Exception e) {
            android.util.Log.e("UserProfileActivity", "Error checking if owner user", e);
            return false;
        }
    }
}

