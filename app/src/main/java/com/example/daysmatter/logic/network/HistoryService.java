package com.example.daysmatter.logic.network;

import com.example.daysmatter.logic.entity.HistoryResponse;
import com.example.daysmatter.ui.MyApplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HistoryService {
    @GET("todayOnhistory/queryEvent.php")
    Call<HistoryResponse> searchHistory(
            @Query("date") String date,
            @Query("key") String key
    );
}