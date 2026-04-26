package com.example.daysmatter.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.daysmatter.logic.room.CountdownDatabase;
import com.example.daysmatter.logic.room.Message;
import com.example.daysmatter.logic.room.MessageDao;
import com.example.daysmatter.ui.MyApplication;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Message>> _msgList = new MutableLiveData<>();
    private final MessageDao dao;
    // 使用线程池处理后台任务
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    public LiveData<List<Message>> getMsgList() {
        return _msgList;
    }
    public HomeViewModel(Application application) {
        super(application);
        this.dao = CountdownDatabase.getDatabase(application).messageDao();
        loadMessages();
    }
    public void loadMessages() {
        executorService.execute(() -> {
            List<Message> messages = dao.getAllByTop();
            // 切回主线程更新 UI
            _msgList.postValue(messages);
        });
    }
    public void loadMessagesByCategory(String category) {
        executorService.execute(() -> {
            List<Message> messages = dao.getMessagesByCategory(category);
            // postValue 是 LiveData 在后台线程更新数据的标准方法
            _msgList.postValue(messages);
        });
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown(); // 防止内存泄漏
    }
}