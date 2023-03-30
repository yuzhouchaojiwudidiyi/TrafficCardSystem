package com.wellsun.trafficcardsystem.util;

import android.util.Log;

/**
 * date     : 2023-03-23
 * author   : ZhaoZheng
 * describe :
 */
public class L {
    static boolean bShow = true;

    public static void v(String strV) {
        if (bShow)
            Log.v("卡操作", strV);
    }

    public static void v(String tip, String strV) {
        if (bShow)
            Log.v(tip, strV);
    }
}
