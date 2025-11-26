# Device Configuration Manager

An Android application for managing device policies locally on Android Google Mobile Services (GMS). This app allows you to simulate having access to a management console by directly changing policy values on the device. This is __NOT__ intended for real world EMM or MDM device management. Rather this was created purely as an educational tool for MDM Policy and cybersecurity education.

## Features

This app provides a comprehensive interface to manage:

### DevicePolicyManager Policies
- Camera disable/enable
- Screen capture disable/enable
- System update policies
- Permission policies
- Password quality and requirements
- Password expiration and history
- Security and network logging
- Organization name and lock screen info
- Global settings (ADB, USB mass storage)
- And more...

### UserManager Restrictions
- Account modification restrictions
- WiFi configuration restrictions
- App installation/uninstallation restrictions
- Location sharing restrictions
- Airplane mode restrictions
- Factory reset restrictions
- And many more user restrictions...

## Requirements

- Android X86 (or compatible Android device)
- Device Admin privileges (the app will prompt you to enable this)
- For full functionality, the app should be set as Device Owner (requires root or ADB setup)
- Root is required for the User Creation function to fully work 

## Setup Instructions

1. **Build the project:**
   ```bash
   ./gradlew assembleDebug
   ```

2. **Install on device:**
   ```adb push DeviceManagement.apk /system/priv-app/DeviceManagement.apk
   ```

3. **Set as Device Owner (for full functionality):**
   ```bash
   adb shell dpm set-device-owner com.deviceconfig.policymanager/.DeviceAdminReceiver
   ```

4. **Launch the app** and enable Device Admin when prompted.

## Usage

1. Open the app
2. Click "Enable Device Admin" if not already enabled
3. Configure policies using the toggles and input fields
4. Click "Apply All Policies" to save your changes

## Notes

- Some policies require Device Owner status (not just Device Admin)
- Password policies require Device Admin privileges
- User restrictions require Device Owner status
- The app will show which policies were successfully applied and which failed
- Some portions of this project were written impart or entirely with the use of AI tools. Special care was taken to ensure security and compatibility. 

## License

This project is provided as-is for educational and testing purposes.

