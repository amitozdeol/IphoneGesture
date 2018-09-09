package com.iphonegesture;

/**
 * Created by amitozdeol on 10/2/17.
 */

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class Utils {
    public static String LogTag = "Floattest";
    public static String EXTRA_MSG = "extra_msg";


    public static boolean canDrawOverlays(Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }else{
            return Settings.canDrawOverlays(context);
        }

    }


}