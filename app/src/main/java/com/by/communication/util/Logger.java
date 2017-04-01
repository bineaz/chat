package com.by.communication.util;

import android.util.Log;

/**
 * Produced a lot of bug on 2017/4/1.
 */

public class Logger {
    private static boolean debug = true;

    public static void e(String TAG, String message)
    {
        if (debug) {
            Log.e(TAG, message);

        }
    }
}
