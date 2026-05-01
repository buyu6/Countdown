package com.example.daysmatter.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daysmatter.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationsViewModel viewModel;
    private HistoryAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. 初始化 ViewBinding
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 2. 初始化 RecyclerView
        // 注意：你可以直接使用 binding.recyclerViewId 访问，不需要 findViewById
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 3. 初始化适配器（初始给个空列表）
        adapter = new HistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 4. 初始化 ViewModel
        viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        Log.d("NotificationsFragment", "开始观察数据变化");

        // 5. 观察成功的列表数据
        viewModel.getHistoryList().observe(getViewLifecycleOwner(), historyList -> {
            if (historyList != null) {
                Log.d("NotificationsFragment", "收到数据，大小: " + historyList.size());
                // 更新适配器：建议在 Adapter 中写一个 setList 方法，而不是每次都 new
                adapter = new HistoryAdapter(historyList);
                recyclerView.setAdapter(adapter);
            }
        });

        // 6. 观察错误信息
        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Log.e("NotificationsFragment", "加载失败: " + error);
                // 失败时清空列表
                adapter = new HistoryAdapter(new ArrayList<>());
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 7. 释放 Binding 防止内存泄漏
        binding = null;
    }
}