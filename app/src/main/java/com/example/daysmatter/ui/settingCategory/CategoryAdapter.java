package com.example.daysmatter.ui.settingCategory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;
import java.util.function.Consumer;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Activity activity;
    private List<Category> list;
    private final boolean showDeleteBtn;
    private final boolean clickItemView;
    private final boolean isSelected;
    private int selectedIconId;
    private int selectedPosition = -1;

    // 回调接口
    private final Consumer<Category> onDeleteClick;
    private final Consumer<Category> isSelectedListener;

    public CategoryAdapter(Activity activity, List<Category> list, boolean showDeleteBtn,
                           boolean clickItemView, boolean isSelected, int selectedIconId,
                           Consumer<Category> onDeleteClick, Consumer<Category> isSelectedListener) {
        this.activity = activity;
        this.list = list;
        this.showDeleteBtn = showDeleteBtn;
        this.clickItemView = clickItemView;
        this.isSelected = isSelected;
        this.selectedIconId = selectedIconId;
        this.onDeleteClick = onDeleteClick;
        this.isSelectedListener = isSelectedListener;

        updateSelectedPosition();
    }

    private void updateSelectedPosition() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getImageId() == selectedIconId) {
                selectedPosition = i;
                break;
            }
        }
    }

    public void submitList(List<Category> newList) {
        this.list = newList;
        updateSelectedPosition();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView categoryIcon;
        public final TextView categoryName;
        public final ImageButton deleteBtn;
        public final ImageView selectedIcon;

        @SuppressLint("WrongViewCast")
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

        try {
            holder.categoryIcon.setImageResource(category.getImageId());
        } catch (Exception e) {
            Log.e("CategoryAdapter", "设置图标失败: " + category.getName(), e);
            holder.categoryIcon.setImageResource(R.drawable.life);
        }

        holder.categoryName.setText(category.getName());

        // 点击事件逻辑
        holder.itemView.setOnClickListener(v -> {
            if (clickItemView) {
                Bundle bundle=new Bundle();
                bundle.putString("category", category.getName());
                Navigation.findNavController(v).navigate(R.id.action_settingCategoryFragment_to_showCategoryFragment2,bundle);

            }

            if (isSelected) {
                selectedPosition = holder.getBindingAdapterPosition();
                notifyDataSetChanged();
                isSelectedListener.accept(category);
            }
        });

        // 删除按钮逻辑
        if (showDeleteBtn && !"生活".equals(category.getName()) && !"纪念日".equals(category.getName()) && !"工作".equals(category.getName())) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.deleteBtn.setOnClickListener(v -> onDeleteClick.accept(category));
        } else {
            holder.deleteBtn.setVisibility(View.GONE);
        }

        // 选中状态逻辑
        if (isSelected) {
            holder.selectedIcon.setVisibility(selectedPosition == position ? View.VISIBLE : View.GONE);
        } else {
            holder.selectedIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}