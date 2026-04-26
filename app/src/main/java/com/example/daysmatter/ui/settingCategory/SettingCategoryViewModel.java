package com.example.daysmatter.ui.settingCategory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.daysmatter.R;
import com.example.daysmatter.logic.room.Category;
import com.example.daysmatter.logic.room.CategoryDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingCategoryViewModel extends ViewModel {
    private final MutableLiveData<List<Category>> categoryList = new MutableLiveData<>();
    private final CategoryDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SettingCategoryViewModel(CategoryDao dao) {
        this.dao = dao;
    }

    public LiveData<List<Category>> getCategoryList() {
        return categoryList;
    }

    public void loadCategories() {
        executor.execute(() -> {
            List<Category> dbList = dao.loadAllcategory();
            List<Category> result = new ArrayList<>();

            // 定义默认分类
            String[] names = {"生活", "纪念日", "工作"};
            int[] icons = {R.drawable.life, R.drawable.miss, R.drawable.work};

            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                Category item = null;
                for (Category c : dbList) {
                    if (c.getName().equals(name)) {
                        item = c;
                        break;
                    }
                }
                if (item == null) item = new Category(name, icons[i]);
                result.add(item);
            }

            // 添加其他自定义分类
            for (Category c : dbList) {
                if (!c.getName().equals("生活") && !c.getName().equals("纪念日") && !c.getName().equals("工作")) {
                    result.add(c);
                }
            }
            categoryList.postValue(result);
        });
    }

    public void deleteCategory(Category category) {
        executor.execute(() -> {
            dao.deleteMessagesByCategory(category.getName());
            dao.deleteCategory(category);
            loadCategories(); // 刷新数据
        });
    }
}