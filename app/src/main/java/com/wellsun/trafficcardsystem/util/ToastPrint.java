package com.wellsun.trafficcardsystem.util;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wellsun.trafficcardsystem.App;
import com.wellsun.trafficcardsystem.R;


/**
 * date     : 2021/7/8
 * author   : ZhaoZheng
 * describe :
 */
public class ToastPrint {
    private static Toast toastText;
    private static Toast toastView;
    public static void showText(String mgs) {
        if (toastText == null) {
            toastText = Toast.makeText(App.appContext, mgs, Toast.LENGTH_LONG);
        } else {
            toastText.cancel();
            toastText = Toast.makeText(App.appContext, mgs, Toast.LENGTH_LONG);
        }
        toastText.setText(mgs);
        toastText.setGravity(Gravity.CENTER, 0, 0);
        toastText.show();
    }


}
