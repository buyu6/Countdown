package com.example.daysmatter.ui.addCategory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.daysmatter.logic.room.Category;
import com.example.daysmatter.logic.room.CategoryDao;
import com.example.daysmatter.logic.room.CountdownDatabase;
import com.example.daysmatter.ui.MyApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddCategoryViewModel extends ViewModel {

    private final CategoryDao dao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final MutableLiveData<SaveResult> saveResult = new MutableLiveData<>();

    public AddCategoryViewModel() {
        dao = CountdownDatabase
                .getDatabase(MyApplication.getContext())
                .categoryDao();
    }

    public LiveData<SaveResult> getSaveResult() {
        return saveResult;
    }

    public void save(String name, int imageId) {
        String finalName = name == null ? "" : name.trim();

        if (finalName.isEmpty() || imageId == -1) {
            saveResult.setValue(new SaveResult(false, "倒数本名称或图标为空，请重新操作"));
            return;
        }

        executorService.execute(() -> {
            try {
                dao.insertCategory(new Category(finalName, imageId));
                saveResult.postValue(new SaveResult(true, "保存成功"));
            } catch (Exception e) {
                saveResult.postValue(new SaveResult(false, "保存失败：" + e.getMessage()));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }

    public static class SaveResult {
        private final boolean success;
        private final String message;

        public SaveResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}