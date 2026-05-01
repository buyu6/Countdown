package com.example.daysmatter.ui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context context;
    public static final String APPKEY = "b35989dd6db40fb536d8be42f3202b27";

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate() {
        context = getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }

}
