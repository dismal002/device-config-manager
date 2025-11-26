# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep DeviceAdminReceiver
-keep class com.deviceconfig.policymanager.DeviceAdminReceiver { *; }

# Keep PolicyManager
-keep class com.deviceconfig.policymanager.PolicyManager { *; }

# Keep MainActivity
-keep class com.deviceconfig.policymanager.MainActivity { *; }

