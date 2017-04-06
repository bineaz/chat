package com.by.communication.util;

import android.util.Log;

import com.by.communication.entity.ChatMessage;

import java.util.ArrayList;

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

    public static void list(String TAG, ArrayList arrayList)
    {
        for (int i = 0; i < arrayList.size(); i++) {
            Log.e(TAG, arrayList.get(i).toString());
        }
    }
}
