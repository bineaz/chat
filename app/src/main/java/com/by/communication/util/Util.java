package com.by.communication.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.by.communication.App;

/**
 * Created by Administrator on 2016/5/25.
 */
public class Util {
    private static Toast toast;

    public static Context getContext()
    {
        return App.getInstance().getApplicationContext();
    }

    public static void hideKeyBoard(EditText editText)
    {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public static void hideKeyBoard(IBinder windowToken)
    {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }


    public static void showKeyBoard(EditText editText)
    {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(editText, 0);

    }

    public static void toast(Context context, String message)
    {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }


    public static int dip2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context ctx, float spValue)
    {
        final float scaledDensity = ctx.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scaledDensity + 0.5f);
    }

    public static int getScreenHeight(Context context)
    {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int dip2px(float dpValue)
    {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static String getRunningActivityName()
    {
        ActivityManager activityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
    }


    public static boolean isStorageEnable()
    {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Logger.e("TestFile", "SD card is not available/writable right now.");
            return false;
        }
        return true;
    }

    public static void printByteArray(String pre_text, byte[] in)
    {
        System.out.println(pre_text + new String(in));
    }

}

