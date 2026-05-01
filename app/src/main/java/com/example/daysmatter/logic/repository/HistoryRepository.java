package com.example.daysmatter.logic.repository;


import androidx.lifecycle.MutableLiveData;

import com.example.daysmatter.logic.entity.HistoryEvent;
import com.example.daysmatter.logic.entity.HistoryResponse;
import com.example.daysmatter.logic.network.HistoryNetwork;
import com.example.daysmatter.logic.network.NetworkCallback;

import java.util.List;

public class HistoryRepository {

    public void searchHistory(String date, MutableLiveData<List<HistoryEvent>> successData, MutableLiveData<String> errorMsg) {
        HistoryNetwork.searchHistory(date, new NetworkCallback<HistoryResponse>() {
            @Override
            public void onSuccess(HistoryResponse response) {
                if (response.getError_code() == 0) {
                    successData.setValue(response.getResult());
                } else {
                    errorMsg.setValue(response.getReason());
                }
            }

            @Override
            public void onError(Throwable t) {
                errorMsg.setValue(t.getMessage());
            }
        });
    }
}