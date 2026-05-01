package com.example.daysmatter;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.daysmatter.databinding.ActivityMainBinding;
import com.example.daysmatter.logic.room.Category;
import com.example.daysmatter.logic.room.CategoryDao;
import com.example.daysmatter.logic.room.CountdownDatabase;
import com.example.daysmatter.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private CategoryDao dao;
    private ActivityMainBinding binding;
    public static String currentCategory = "全部";
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dao = CountdownDatabase.getDatabase(this).categoryDao();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // 1. 底部导航栏绑定
        NavigationUI.setupWithNavController(binding.navView, navController);

        // ★ 核心修复：显式处理底部导航栏点击，防止侧滑监听冲突导致底部栏失效 ★
        binding.navView.setOnItemSelectedListener(item -> {
            // 这行代码执行原生的 Fragment 切换逻辑
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        // 2. UI 动态显示/隐藏控制
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            boolean isTopLevel = (id == R.id.navigation_home || id == R.id.navigation_notifications || id == R.id.navigation_dashboard);
            binding.navView.setVisibility(isTopLevel ? View.VISIBLE : View.GONE);
            binding.appBar.setVisibility(id == R.id.launchFragemnt ? View.GONE : View.VISIBLE);
        });

        // 3. Toolbar 与侧滑菜单配置
        setSupportActionBar(binding.toolbar);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
        ).setOpenableLayout(binding.drawerLayout).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // 4. ★ 侧滑菜单监听逻辑 ★
        binding.navDrawer.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            String category = item.getTitle().toString();

            // 跳转到指定页面
            if (itemId == R.id.categoryEvent) {
                navController.navigate(R.id.navigation_dashboard);
            } else if (itemId == R.id.historyEvent) {
                navController.navigate(R.id.navigation_notifications);
            } else {
                // 点击的是分类项（动态分类或“全部”）
                if (itemId == R.id.allEvent) category = "全部";
                MainActivity.currentCategory = category;

                // 强制切回首页：清理栈顶，防止重复创建
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.navigation_home, false) // 设为 false，确保首页实例不被销毁
                        .setLaunchSingleTop(true)
                        .build();

                navController.navigate(R.id.navigation_home, null, navOptions);

                // 同步更新底部导航栏的高亮状态
                binding.navView.getMenu().findItem(R.id.navigation_home).setChecked(true);

                // 延迟通知刷新
                final String finalCategory = category;
                binding.getRoot().postDelayed(() -> notifyHomeFragmentRefresh(finalCategory), 60);
            }

            binding.drawerLayout.closeDrawers();
            return true;
        });
    }

    /**
     * 寻找 HomeFragment 并刷新数据
     */
    private void notifyHomeFragmentRefresh(String category) {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment != null) {
            Fragment currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();

            // 兜底查找逻辑：如果 Primary 没拿到，从列表里找
            if (!(currentFragment instanceof HomeFragment)) {
                for (Fragment f : navHostFragment.getChildFragmentManager().getFragments()) {
                    if (f instanceof HomeFragment) {
                        currentFragment = f;
                        break;
                    }
                }
            }

            if (currentFragment instanceof HomeFragment) {
                HomeFragment homeFragment = (HomeFragment) currentFragment;
                if ("全部".equals(category)) {
                    homeFragment.loadAllMessages();
                } else {
                    homeFragment.onCategorySelected(category);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCategoryMenu();
    }

    private static class MenuInfo {
        int id;
        String title;
        Drawable icon;

        MenuInfo(int id, String title, Drawable icon) {
            this.id = id;
            this.title = title;
            this.icon = icon;
        }
    }

    /**
     * 刷新侧滑菜单（防重复添加）
     */
    private void refreshCategoryMenu() {
        Menu menu = binding.navDrawer.getMenu();
        // 静态 ID 列表（XML 中定义的）
        Set<Integer> staticIds = new HashSet<>(Arrays.asList(
                R.id.allEvent, R.id.lifeEvent, R.id.workEvent,
                R.id.missEvent, R.id.categoryEvent, R.id.historyEvent
        ));

        // 清理动态项
        for (int i = menu.size() - 1; i >= 0; i--) {
            MenuItem item = menu.getItem(i);
            if (!staticIds.contains(item.getItemId())) menu.removeItem(item.getItemId());
        }

        // 插入点定位
        int missIndex = -1, categoryIndex = -1;
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).getItemId() == R.id.missEvent) missIndex = i;
            if (menu.getItem(i).getItemId() == R.id.categoryEvent) categoryIndex = i;
        }
        if (missIndex == -1 || categoryIndex == -1) return;

        // 暂存尾部静态项
        List<MenuInfo> staticAfterCategory = new ArrayList<>();
        for (int i = menu.size() - 1; i >= categoryIndex; i--) {
            MenuItem item = menu.getItem(i);
            staticAfterCategory.add(0, new MenuInfo(item.getItemId(), item.getTitle().toString(), item.getIcon()));
            menu.removeItem(item.getItemId());
        }

        executorService.execute(() -> {
            List<Category> categories = dao.loadAllcategory();
            Set<String> filter = new HashSet<>(Arrays.asList("全部", "工作", "生活", "纪念日"));
            runOnUiThread(() -> {
                for (Category cat : categories) {
                    if (!filter.contains(cat.getName())) {
                        MenuItem item = menu.add(Menu.NONE, View.generateViewId(), Menu.NONE, cat.getName());
                        item.setIcon(cat.getImageId());
                        item.setCheckable(true);
                        filter.add(cat.getName());
                    }
                }
                for (MenuInfo info : staticAfterCategory) {
                    MenuItem item = menu.add(Menu.NONE, info.id, Menu.NONE, info.title);
                    item.setIcon(info.icon);
                }
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}