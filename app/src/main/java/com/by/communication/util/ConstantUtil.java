package com.by.communication.util;

import android.os.Environment;

/**
 * Produced a lot of bug on 2017/3/30.
 */

public class ConstantUtil {
    public static final String SOCKET_URI      = "http://tangcheng.me:8282";
    public static final String BASE_URL        = "http://chat.tangcheng.me/index.php/Chat/";
    public static final String IMAGE_BASE_URL  = "http://chat.tangcheng.me/uploads/image/";
    public static final String AUDIO_BASE_PATH = Environment.getExternalStorageDirectory().getPath() + "/Chat/audio/";
    public static final String FILE_BASE_PATH  = Environment.getExternalStorageDirectory().getPath() + "/Chat/file/";
    public static final String AUDIO_BASE_URL  = "http://chat.tangcheng.me/uploads/audio/";
    public static final String FILE_BASE_URL   = "http://chat.tangcheng.me/uploads/file/";
}
