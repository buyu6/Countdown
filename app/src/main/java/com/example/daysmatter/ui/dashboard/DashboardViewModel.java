package com.example.daysmatter.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.daysmatter.logic.room.Category;
import com.example.daysmatter.logic.room.CategoryDao;
import com.example.daysmatter.logic.room.CountdownDatabase;
import com.example.daysmatter.ui.MyApplication;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<List<Category>> msgList = new MutableLiveData<>();

    public LiveData<List<Category>> getMsgList() {
        return msgList;
    }

    private final CategoryDao dao =
            CountdownDatabase.getDatabase(MyApplication.getContext()).categoryDao();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public DashboardViewModel() {
        loadCategories();
    }

    public void loadCategories() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Category> categories = dao.loadAllcategory();
                msgList.postValue(categories);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}