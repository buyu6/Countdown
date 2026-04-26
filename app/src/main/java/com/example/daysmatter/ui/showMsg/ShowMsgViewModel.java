package com.example.daysmatter.ui.showMsg;

import android.annotation.SuppressLint;

import androidx.lifecycle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.daysmatter.logic.room.Message;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeParseException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class ShowMsgViewModel extends ViewModel {
    // 观察者对象
    private final MutableLiveData<String> titleText = new MutableLiveData<>();
    private final MutableLiveData<String> timeText = new MutableLiveData<>();
    private final MutableLiveData<String> aimTimeText = new MutableLiveData<>();
    private final MutableLiveData<Message> currentMsg = new MutableLiveData<>();

    public void setMsg(Message msg) {
        if (msg == null) return;
        currentMsg.setValue(msg);
        calculateDate(msg);
    }
    @SuppressLint({"NewApi", "LocalSuppress"})
    private void calculateDate(Message msg) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate aimDate = LocalDate.parse(msg.getAimdate());
            long days = ChronoUnit.DAYS.between(today, aimDate);

            if (days == 0) {
                titleText.setValue(msg.getTitle() + " 就是今天");
            } else if (days > 0) {
                titleText.setValue(msg.getTitle() + " 还有");
            } else {
                titleText.setValue(msg.getTitle() + " 已经");
            }
            timeText.setValue(String.valueOf(Math.abs(days)));
            aimTimeText.setValue("目标日：" + msg.getAimdate());
        } catch (DateTimeParseException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public LiveData<String> getTitleText() { return titleText; }
    public LiveData<String> getTimeText() { return timeText; }
    public LiveData<String> getAimTimeText() { return aimTimeText; }
    public Message getMsg() { return currentMsg.getValue(); }
}