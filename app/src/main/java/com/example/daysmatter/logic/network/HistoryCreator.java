package com.example.daysmatter.logic.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HistoryCreator {

    private static final String BASE_URL = "https://v.juhe.cn/";

    // 使用静态变量并在声明时初始化（饿汉式单例，简单安全）
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    public static <T> T create(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
}