package com.example.daysmatter.ui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context context;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate() {
        context = getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }

}
