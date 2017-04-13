package com.yixia.camera.demo.log;

import android.util.Log;


public class Logger {
	/**
	 * 程序是否Debug版本
	 */
	public static final boolean IsDebug = false;
	private static final String TAG = "[VCameraDemo]";


	public static void e(Throwable tr) {
		if (IsDebug) {
			Log.e(TAG, "", tr);
		}
	}


	public static void e(String msg) {
		if (IsDebug) {
			Log.e(TAG, msg);
		}
	}
}
