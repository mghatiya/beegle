/*
 * Copyright 2020 Google Inc.
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
package com.beegle.app;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


public class LauncherActivity
        extends com.google.androidbrowserhelper.trusted.LauncherActivity {



    protected Uri getURLFromIntent() {
        try {
            Uri data = this.getIntent().getData();
            if (data != null) {
                return data;
            }

            Bundle extras = this.getIntent().getExtras();
            if (extras != null) {
                String path = extras.getString("url");
                if (path != null) {
                    return Uri.parse(path);
                }
            }
            return null;
        } catch (Exception error) {
            return  null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting an orientation crashes the app due to the transparent background on Android 8.0
        // Oreo and below. We only set the orientation on Oreo and above. This only affects the
        // splash screen and Chrome will still respect the orientation.
        // See https://github.com/GoogleChromeLabs/bubblewrap/issues/496 for details.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }


    /*
    @Override
    protected Uri getLaunchingUrl() {
        // Get the original launch Url.
        Uri uri = super.getLaunchingUrl();



        return uri;
    }
*/

    @Override
    protected Uri getLaunchingUrl() {
        Uri defaultUrl = super.getLaunchingUrl();
        Uri redirectUrl = this.getURLFromIntent();

        Uri targetUrl = redirectUrl == null ? defaultUrl : redirectUrl;
        Uri.Builder uponTargetUrl = targetUrl.buildUpon();

        String token = getApplicationContext().getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");

        // Token may be absent at the moment of the first launch of the application.
        // As soon as the application appears, it will be restarted.
        // Restart with token implemented in FcmService
        if (token != "empty") {
            uponTargetUrl.appendQueryParameter("beegleapp_fcm_token", token);
            Log.d("DEBUG", "Token not empty");
            Log.d("DEBUG","Token:"+token);
        } else {
            Log.d("DEBUG", "Token Empty");
        }

        return uponTargetUrl.build();

    }
}
