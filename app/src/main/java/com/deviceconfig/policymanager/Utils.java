/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.deviceconfig.policymanager;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Utility methods for Settings
 */
public class Utils {
    
    /**
     * Start a new instance of the activity, showing only the given fragment.
     * Simplified version - for now just shows a toast or launches intent.
     */
    public static void startWithFragment(Context context, String fragmentName, Bundle args,
            Fragment resultTo, int resultRequestCode, int titleResId,
            CharSequence title) {
        // For this app, we'll handle tile clicks differently
        // If there's a fragment, we could navigate to it, but for now just show title
        if (title != null) {
            Toast.makeText(context, "Selected: " + title, Toast.LENGTH_SHORT).show();
        } else if (titleResId != 0) {
            Toast.makeText(context, context.getString(titleResId), Toast.LENGTH_SHORT).show();
        }
        // TODO: Implement fragment navigation if needed
    }
}

