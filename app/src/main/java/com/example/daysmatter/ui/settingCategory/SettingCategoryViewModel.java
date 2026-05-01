package com.example.daysmatter.ui.settingCategory;


import static androidx.lifecycle.AndroidViewModel_androidKt.getApplication;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.daysmatter.logic.room.Category;
import com.example.daysmatter.logic.room.CategoryDao;
import com.example.daysmatter.logic.room.CountdownDatabase;
import com.example.daysmatter.ui.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SettingCategoryViewModel extends ViewModel {
    private final CategoryDao categoryDao;

    // 1. 建议使用单一来源的数据流
    private final MutableLiveData<List<Category>> _msgList = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Category>> getMsgList() {
        return _msgList;
    }

    // 2. 增加加载状态，方便 UI 显示 ProgressBar
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }

    // 3. 使用更高效的线程池：计算密集型用 CPU 核心数，IO 密集型（数据库）可稍微多一些
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public SettingCategoryViewModel() {
        // 数据库初始化建议放在注入或通过 Application 管理
        this.categoryDao = CountdownDatabase.getDatabase(MyApplication.getContext()).categoryDao();
        // 4. 初始化时自动加载一次，确保 UI 进来就有数据
        loadCategories();
    }

    public void loadCategories() {
        // 如果正在加载中，避免重复触发
        if (Boolean.TRUE.equals(_isLoading.getValue())) return;

        _isLoading.setValue(true);
        executor.execute(() -> {
            try {
                // 模拟网络或复杂查询的耗时
                List<Category> categories = categoryDao.loadAllcategory();

                // 5. 确保返回的不为 null
                if (categories == null) {
                    categories = new ArrayList<>();
                }

                _msgList.postValue(categories);
            } catch (Exception e) {
                Log.e("ViewModel", "加载分类失败", e);
                _msgList.postValue(new ArrayList<>());
            } finally {
                _isLoading.postValue(false);
            }
        });
    }

    public void delete(Category category) {
        if (category == null) return;

        _isLoading.setValue(true);
        executor.execute(() -> {
            try {
                // 开启事务处理（如果 Dao 支持）
                // 先删除该分类下的所有倒数消息，再删除分类
                categoryDao.deleteMessagesByCategory(category.getName());
                categoryDao.deleteCategory(category);

                // 6. 删除成功后立即同步更新本地 LiveData，而不是重新请求整个数据库（优化性能）
                List<Category> currentList = _msgList.getValue();
                if (currentList != null) {
                    List<Category> newList = new ArrayList<>(currentList);
                    newList.remove(category);
                    _msgList.postValue(newList);
                } else {
                    loadCategories(); // 兜底方案
                }
            } catch (Exception e) {
                Log.e("ViewModel", "删除失败", e);
            } finally {
                _isLoading.postValue(false);
            }
        });
    }
    public void ensureCategoryExists(Category category, Runnable onComplete) {
        executor.execute(() -> {
            List<Category> dbList = categoryDao.loadAllcategory();
            boolean exists = false;
            for (Category c : dbList) {
                if (c.getName().equals(category.getName())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                categoryDao.insertCategory(category);
            }
            // 执行完数据库操作后，回到主线程执行回调
            if (onComplete != null) {
                new Handler(Looper.getMainLooper()).post(onComplete);
            }
        });
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        // 7. 优雅关闭线程池
        if (!executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }
}