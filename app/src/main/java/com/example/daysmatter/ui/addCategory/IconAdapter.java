package com.example.daysmatter.ui.addCategory;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daysmatter.R;
import com.example.daysmatter.logic.entity.IconItem;

import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {
    private List<IconItem> icons;
    private OnIconSelectedListener onIconSelectedListener;
    public interface OnIconSelectedListener {
        void onIconSelected(int resId);
    }
    public IconAdapter(List<IconItem> icons, OnIconSelectedListener onIconSelectedListener) {
        this.icons = icons;
        this.onIconSelectedListener = onIconSelectedListener;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        ImageView selectedOverlay;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImage);
            selectedOverlay = itemView.findViewById(R.id.selectedOverlay);
        }
    }
    @NonNull
    @Override
    public IconAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconAdapter.ViewHolder holder, int position) {
        IconItem iconItem=icons.get(position);
        holder.iconImage.setImageResource(iconItem.getResId());
        holder.selectedOverlay.setVisibility(iconItem.isSelected() ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> {
            for (IconItem item : icons) {
                item.setSelected(false);
            }

            iconItem.setSelected(true);

            notifyDataSetChanged();

            if (onIconSelectedListener != null) {
                onIconSelectedListener.onIconSelected(iconItem.getResId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return icons == null ? 0 : icons.size();
    }
}
