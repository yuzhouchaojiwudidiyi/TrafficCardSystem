package com.wellsun.trafficcardsystem;

import android.app.Application;

import com.cczhr.TTS;
import com.cczhr.TTSConstants;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

/**
 * date     : 2023-03-10
 * author   : ZhaoZheng
 * describe :
 */
public class App extends Application {
    public static TTS tts;
    public static App appContext;


    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        initAndroidAutoSize();
        initTts();
    }

    private void initTts() {
        tts = TTS.getInstance();//获取单例对象
        tts.init(this, TTSConstants.TTS_XIAOYAN);//初始化
    }

    /**
     * 可以在 pt、in、mm 这三个冷门单位中，选择一个作为副单位，副单位是用于规避修改 DisplayMetrics#density 所造成的对于其他使用 dp 布局的系统控件或三方库控件的不良影响，使用副单位后可直接填写设计图上的像素尺寸，不需要再将像素转化为 dp
     */
    private void initAndroidAutoSize() {
        AutoSizeConfig.getInstance().getUnitsManager()
                .setSupportDP(true)
                .setSupportSP(true)
                .setSupportSubunits(Subunits.MM);
    }


}
