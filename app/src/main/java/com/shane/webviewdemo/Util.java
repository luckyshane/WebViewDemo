package com.shane.webviewdemo;

import android.os.Looper;

/**
 * Created by Shane on 2018/6/22.
 */

public class Util {

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static String checkThread() {
        return isMainThread() ? "MainThread" : "SubThread";
    }


}
