package com.example.daysmatter.ui.settingCategory;


import static androidx.fragment.app.FragmentKt.setFragmentResult;

import static java.nio.file.Files.delete;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.daysmatter.R;
import com.example.daysmatter.databinding.FragmentSettingCategoryBinding;
import com.example.daysmatter.logic.room.Category;
import com.example.daysmatter.logic.room.CategoryDao;
import com.example.daysmatter.logic.room.CountdownDatabase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SettingCategoryFragment extends Fragment {
    private FragmentSettingCategoryBinding binding;
    private SettingCategoryViewModel viewModel;
    private CategoryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 使用 requireActivity().getApplication() 确保 ViewModel 内部获取数据库时不报错
        viewModel = new ViewModelProvider(this).get(SettingCategoryViewModel.class);
        binding = FragmentSettingCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        initClickListeners();
        observeData();
    }

    private void initAdapter() {
        Bundle b = getArguments();
        int flag = (b != null) ? b.getInt("flag") : 0;
        int iconId = (b != null) ? b.getInt("iconId") : -1;

        binding.settingCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 这里不再需要传入外部定义的 list，Adapter 内部有自己的存储空间
        if (flag == 1) {
            adapter = new CategoryAdapter(
                    requireActivity(),
                    new ArrayList<>(),
                    false, // showDeleteBtn
                    false, // clickItemView
                    true,  // isSelected
                    iconId,
                    category -> {},
                    category -> {
                        // 1. 修正：将数据库操作交给 ViewModel 统一处理，保持 Fragment 纯净
                        viewModel.ensureCategoryExists(category, () -> {
                            // 2. 修正：确保在主线程且 Fragment 仍然有效时回传数据
                            if (isAdded() && getActivity() != null) {
                                Bundle result = new Bundle();
                                result.putInt("icon_id", category.getImageId());
                                result.putString("category_name", category.getName());
                                getParentFragmentManager().setFragmentResult("category_request", result);
                                Navigation.findNavController(requireView()).popBackStack();
                            }
                        });
                    }
            );
        } else if(flag==2) {
            adapter = new CategoryAdapter(
                    requireActivity(),
                    new ArrayList<>(),
                    true,  // showDeleteBtn
                    false, // clickItemView
                    false, // isSelected
                    -1,
                    categoryItem -> new AlertDialog.Builder(requireContext())
                            .setMessage("确认删除吗？该分类下的所有倒数日也将被删除。")
                            .setPositiveButton("删除", (dialog, which) -> viewModel.delete(categoryItem))
                            .setNegativeButton("取消", null)
                            .show(),
                    category -> {}
            );
        }
        binding.settingCategoryRecyclerView.setAdapter(adapter);
    }

    private void initClickListeners() {
        binding.addCategoryBook.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_settingCategoryFragment_to_addCategoryFragment)
        );
    }

    private void observeData() {
        // 观察加载状态（配合我之前给你的 ViewModel 修改版）
        if (viewModel.getIsLoading() != null) {
            viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
                // 如果你有 ProgressBar，可以在这里控制显隐
                binding.addCategoryBook.setEnabled(!isLoading);
            });
        }

        viewModel.getMsgList().observe(getViewLifecycleOwner(), categories -> {
            if (categories == null) return;

            // 逻辑处理：提取默认分类并保证顺序
            // 这部分逻辑依然保留，但做了简化
            List<Category> finalResult = processCategories(categories);
            adapter.submitList(finalResult);
        });
    }

    private List<Category> processCategories(List<Category> source) {
        Map<String, Category> map = new LinkedHashMap<>();

        // 这里的对象现在能正确保存名字和图标了
        map.put("生活", new Category("生活", R.drawable.life));
        map.put("纪念日", new Category("纪念日", R.drawable.miss));
        map.put("工作", new Category("工作", R.drawable.work));

        if (source != null) {
            for (Category c : source) {
                // 只有当 name 不为空时才放入，防止数据库里的脏数据干扰
                if (c.getName() != null && !c.getName().isEmpty()) {
                    map.put(c.getName(), c);
                }
            }
        }
        return new ArrayList<>(map.values());
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadCategories();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 彻底防止内存泄漏
    }
}