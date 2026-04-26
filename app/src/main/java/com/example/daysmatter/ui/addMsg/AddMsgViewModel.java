package com.example.daysmatter.ui.addMsg;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.daysmatter.R;
import com.example.daysmatter.logic.room.CountdownDatabase;
import com.example.daysmatter.logic.room.Message;
import com.example.daysmatter.logic.room.MessageDao;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;

public class AddMsgViewModel extends ViewModel {
    // 使用 LiveData 存储状态，View 会监听这个变量
    private final MutableLiveData<String> dateDisplay = new MutableLiveData<>();
    private final MutableLiveData<Integer> iconId = new MutableLiveData<>(R.drawable.life);

    public LiveData<String> getDateDisplay() {
        return dateDisplay;
    }
    public LiveData<Integer> getIconId() {
        return iconId;
    }
    //更新分类信息方法
    public void setSelectedCategory(int newIconId) {
        iconId.setValue(newIconId);
    }
    // 接收 View 传来的日期数据进行处理
    public void updateDate(int year, int month, int dayOfMonth) {
        @SuppressLint({"NewApi", "LocalSuppress"}) LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
        // 可以在这里进行复杂的业务校验或格式化
        dateDisplay.setValue(selectedDate.toString());
    }
    public void saveMessage(MessageDao dao,String title, boolean isTop, LocalDate selectedDate, int iconId, String categoryName) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (isTop) {
                dao.cancelAllTop();
            }
            @SuppressLint({"NewApi", "LocalSuppress"}) long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), selectedDate);
            Message message = new Message(title, (int) daysBetween, selectedDate.toString(),
                    isTop, iconId, categoryName);
            dao.upSertMessage(message);
        });
    }
    public void updateMessage(String id,MessageDao dao,String title, boolean isTop, LocalDate selectedDate, int iconId, String categoryName) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (isTop) {
                dao.cancelAllTop();
            }
            @SuppressLint({"NewApi", "LocalSuppress"}) long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), selectedDate);
            Message message = new Message(id,title, (int) daysBetween, selectedDate.toString(),
                    isTop, iconId, categoryName);
            dao.upSertMessage(message);
        });
    }
}