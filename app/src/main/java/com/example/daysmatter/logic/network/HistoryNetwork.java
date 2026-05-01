package com.example.daysmatter.logic.network;

import android.util.Log;

import com.example.daysmatter.logic.entity.HistoryResponse;
import com.example.daysmatter.ui.MyApplication;

import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryNetwork {
    private static final String TAG = "HistoryNetwork";

    /**
     * 查询历史上的今天
     * @param date 格式示例: "5/20"
     * @param callback UI层的回调对象
     */
    public static void searchHistory(String date, NetworkCallback<HistoryResponse> callback) {
        // 1. 获取当前时间（仅用于日志输出，增加仪式感）
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d(TAG, "发起网络请求 [当前时间: " + month + "月" + day + "日] -> 查询日期: " + date);

        // 2. 通过 Creator 获取 Service 实例
        HistoryService historyService = HistoryCreator.create(HistoryService.class);

        // 3. 构建 Call 对象 (传入日期和 MyApplication 中定义的全局静态 APPKEY)
        Call<HistoryResponse> call = historyService.searchHistory(date, MyApplication.APPKEY);

        // 4. 执行异步请求
        call.enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HistoryResponse historyResponse = response.body();

                    // 判断业务逻辑错误码 (根据聚合 API 规范，error_code 为 0 是成功)
                    if (historyResponse.getError_code() == 0) {
                        Log.d(TAG, "数据解析成功，事件数量: " + historyResponse.getResult().size());
                        callback.onSuccess(historyResponse);
                    } else {
                        String errorMsg = "API 业务错误，错误码: " + historyResponse.getError_code() + ", 原因: " + historyResponse.getReason();
                        Log.e(TAG, errorMsg);
                        callback.onError(new Exception(errorMsg));
                    }
                } else {
                    Log.e(TAG, "响应失败，HTTP 状态码: " + response.code());
                    callback.onError(new Exception("服务器响应异常，状态码: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {
                // 处理网络连接失败、超时、解析异常等情况
                Log.e(TAG, "网络链路异常: " + t.getMessage());
                callback.onError(t);
            }
        });
    }
}