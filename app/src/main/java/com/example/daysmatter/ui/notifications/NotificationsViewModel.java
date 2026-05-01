package com.example.daysmatter.ui.notifications;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.daysmatter.logic.entity.HistoryEvent;
import com.example.daysmatter.logic.repository.HistoryRepository;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import kotlin.Result;

public class NotificationsViewModel extends ViewModel {

    // 1. 定义存储成功数据的 LiveData
    private final MutableLiveData<List<HistoryEvent>> historyList = new MutableLiveData<>();

    // 2. 定义存储错误信息的 LiveData
    private final MutableLiveData<String> errorMsg = new MutableLiveData<>();

    private final HistoryRepository repository = new HistoryRepository();

    public NotificationsViewModel() {
        // 3. 获取当前日期并初始化请求
        refreshHistory();
    }

    public void refreshHistory() {
        // 使用 Calendar 获取当前的 月/日
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，需要+1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String dateString = month + "/" + day;

        // 4. 调用仓库层，传入我们定义的两个 MutableLiveData
        repository.searchHistory(dateString, historyList, errorMsg);
    }

    // 暴露给 Fragment 观察的 Getter
    public MutableLiveData<List<HistoryEvent>> getHistoryList() {
        return historyList;
    }

    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }
}