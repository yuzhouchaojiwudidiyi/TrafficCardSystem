package com.wellsun.trafficcardsystem;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * date     : 2022-08-09
 * author   : ZhaoZheng
 * describe :
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public Context mContext;


    @Subscribe
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        if (!EventBus.getDefault().isRegistered(this)) {//是否注册eventbus的判断
            EventBus.getDefault().register(this);
        }
        mContext = this;
        init();
    }

    /**
     * 加载页面布局
     */
    public abstract int getLayoutId();

    /**
     * 初始化方法
     */
    public void init() {
        initView();
        setListener();
        initData();
    }

    /**
     * 初始化布局控件
     */
    public abstract void initView();

    /**
     * 初始化设置监听
     */
    public abstract void setListener();

    /**
     * 初始化数据
     */
    public abstract void initData();

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    //去除底部导航栏 状态栏 全屏
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
