package com.example.daysmatter.ui.settingCategory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daysmatter.R;
import com.example.daysmatter.logic.room.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Activity activity;
    // 1. 初始为 ArrayList 而非 null，防止构造函数崩溃
    private List<Category> list = new ArrayList<>();
    private final boolean showDeleteBtn;
    private final boolean clickItemView;
    private final boolean isSelected;
    private int selectedIconId;
    private int selectedPosition = -1;

    private final Consumer<Category> onDeleteClick;
    private final Consumer<Category> isSelectedListener;

    public CategoryAdapter(Activity activity, List<Category> list, boolean showDeleteBtn,
                           boolean clickItemView, boolean isSelected, int selectedIconId,
                           Consumer<Category> onDeleteClick, Consumer<Category> isSelectedListener) {
        this.activity = activity;
        // 2. 构造时安全赋值
        this.list = (list != null) ? new ArrayList<>(list) : new ArrayList<>();
        this.showDeleteBtn = showDeleteBtn;
        this.clickItemView = clickItemView;
        this.isSelected = isSelected;
        this.selectedIconId = selectedIconId;
        this.onDeleteClick = onDeleteClick;
        this.isSelectedListener = isSelectedListener;

        updateSelectedPosition();
    }

    // 寻找当前选中的图标在列表中的位置
    private void updateSelectedPosition() {
        if (list == null || list.isEmpty()) {
            selectedPosition = -1;
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getImageId() == selectedIconId) {
                selectedPosition = i;
                return;
            }
        }
        selectedPosition = -1;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(List<Category> newList) {
        // 3. 强制替换数据源，确保 getItemCount 立即生效
        this.list = (newList != null) ? new ArrayList<>(newList) : new ArrayList<>();
        updateSelectedPosition();

        // 在 Android 中，LiveData 观察者通常已经在主线程执行，但加一层安全校验也是好的
        if (Looper.myLooper() == Looper.getMainLooper()) {
            notifyDataSetChanged();
        } else {
            new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView categoryIcon;
        public final TextView categoryName;
        public final ImageButton deleteBtn;
        public final ImageView selectedIcon;

        public ViewHolder(@NonNull View view) {
            super(view);
            categoryIcon = view.findViewById(R.id.categoryIcon);
            categoryName = view.findViewById(R.id.categoryName);
            deleteBtn = view.findViewById(R.id.deleteCategory);
            selectedIcon = view.findViewById(R.id.CategorySelected);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = list.get(position);

        // 设置图标
        holder.categoryIcon.setImageResource(category.getImageId() != 0 ? category.getImageId() : R.drawable.life);
        holder.categoryName.setText(category.getName());

        // 4. 点击逻辑优化
        holder.itemView.setOnClickListener(v -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;

            // 跳转详情逻辑
            if (clickItemView) {
                Bundle bundle = new Bundle();
                bundle.putString("category", category.getName());
                try {
                    Navigation.findNavController(v).navigate(R.id.action_navigation_dashboard_to_showCategoryFragment2, bundle);
                } catch (Exception e) {
                    Log.e("Adapter", "Navigation 失败: " + e.getMessage());
                }
            }

            // 选中逻辑
            if (isSelected) {
                int oldPos = selectedPosition;
                selectedPosition = currentPos;
                selectedIconId = category.getImageId();

                // 局部刷新，性能更好且不会导致图片闪烁
                notifyItemChanged(oldPos);
                notifyItemChanged(selectedPosition);

                if (isSelectedListener != null) {
                    isSelectedListener.accept(category);
                }
            }
        });

        // 5. 删除按钮逻辑：过滤掉系统默认分类
       boolean isDefault = "生活".equals(category.getName()) || "纪念日".equals(category.getName()) || "工作".equals(category.getName());
        if (showDeleteBtn&&!isDefault ) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.deleteBtn.setOnClickListener(v -> {
               if (onDeleteClick != null) onDeleteClick.accept(category);
            });
        } else {
            holder.deleteBtn.setVisibility(View.GONE);
       }

        // 6. 选中状态显示
        if (isSelected) {
            holder.selectedIcon.setVisibility(selectedPosition == position ? View.VISIBLE : View.GONE);
        } else {
            holder.selectedIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }
}