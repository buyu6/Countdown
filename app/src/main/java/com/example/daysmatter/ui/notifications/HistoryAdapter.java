package com.example.daysmatter.ui.notifications;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daysmatter.R;
import com.example.daysmatter.logic.entity.HistoryEvent;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<HistoryEvent> list;

    // 构造函数
    public HistoryAdapter(List<HistoryEvent> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载之前优化过的简约风布局 history_item.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryEvent event = list.get(position);

        // 打印日志，方便调试
        Log.d("HistoryAdapter", "绑定数据: position=" + position + ", date=" + event.getDate() + ", title=" + event.getTitle());

        // 绑定数据到 UI
        holder.dateText.setText(event.getDate());
        holder.titleText.setText(event.getTitle());
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    // 静态内部类 ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView dateText;
        final TextView titleText;

        public ViewHolder(@NonNull View view) {
            super(view);
            dateText = view.findViewById(R.id.date_text);
            titleText = view.findViewById(R.id.title_text);
        }
    }
}
