package com.example.daysmatter.ui.settingCategory;



import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.daysmatter.R;
import com.example.daysmatter.databinding.FragmentSettingCategoryBinding;

import com.example.daysmatter.logic.room.CategoryDao;
import com.example.daysmatter.logic.room.CountdownDatabase;


import java.util.ArrayList;

public class SettingCategoryFragment extends Fragment {

    private SettingCategoryViewModel mViewModel;
    private FragmentSettingCategoryBinding binding;
    private CategoryAdapter adapter;

    public static SettingCategoryFragment newInstance() {
        return new SettingCategoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 初始化 ViewBinding
        binding = FragmentSettingCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. 初始化 DAO 和 ViewModel (使用 Factory 传入 DAO)
        CategoryDao dao = CountdownDatabase.getDatabase(requireContext()).categoryDao();
        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new SettingCategoryViewModel(dao);
            }
        };
        mViewModel = new ViewModelProvider(this, factory).get(SettingCategoryViewModel.class);

        // 2. 配置 RecyclerView
        binding.settingCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 3. 初始化 Adapter
        // 参数：Context, 列表, 是否显示删除按钮, 是否允许点击item, 是否显示选中框, 选中ID, 删除回调, 选中回调
        adapter = new CategoryAdapter(requireActivity(), new ArrayList<>(), true, false, false, -1,
                categoryItem -> {
                    // 删除操作
                    new AlertDialog.Builder(requireContext())
                            .setMessage("确认删除吗")
                            .setPositiveButton("删除", (d, w) -> mViewModel.deleteCategory(categoryItem))
                            .setNegativeButton("取消", null)
                            .show();
                },
                category -> {
                    // 选中操作 (如果需要回传给上一个 Fragment)
                    Bundle bundle = new Bundle();
                    bundle.putInt("icon_id", category.getImageId());
                    bundle.putString("category_name", category.getName());
                    getParentFragmentManager().setFragmentResult("category_request", bundle);
                    Navigation.findNavController(requireView()).popBackStack();
                }
        );
        binding.settingCategoryRecyclerView.setAdapter(adapter);

        // 4. 设置添加按钮监听
        binding.addCategoryBook.setOnClickListener(v -> {
            // 这里跳转到添加分类的 Activity 或 Fragment
            Navigation.findNavController(view).navigate(R.id.action_settingCategoryFragment_to_addCategoryFragment);
        });

        // 5. 观察数据变化
        mViewModel.getCategoryList().observe(getViewLifecycleOwner(), list -> {
            // 注意：这里需要确保 adapter 支持 submitList 或者自行实现 setList 方法
            adapter.submitList(list);
            adapter.notifyDataSetChanged();
        });

        // 6. 加载数据
        mViewModel.loadCategories();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.loadCategories(); // 确保回到页面时刷新
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 防止内存泄漏
    }
}