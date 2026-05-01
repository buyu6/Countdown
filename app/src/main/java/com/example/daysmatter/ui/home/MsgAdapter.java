package com.example.daysmatter.ui.home;

import static androidx.core.content.ContentProviderCompat.requireContext;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daysmatter.R;
import com.example.daysmatter.logic.room.CountdownDatabase;
import com.example.daysmatter.logic.room.Message;
import com.example.daysmatter.logic.room.MessageDao;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private Activity activity;
    private LocalDate today;
    private List<Message> data = new ArrayList<>();
    private final MessageDao dao; // 使用 final 确保只读引用
    public MsgAdapter(Activity activity){
        this.activity=activity;
        dao=CountdownDatabase.getDatabase(activity).messageDao();
    }
    public void submitList(List<Message> newList) {
        this.data = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.firsttitle);
            time = itemView.findViewById(R.id.firsttime);

            // 设置点击监听 (跳转)
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Message msg = data.get(position);
                    int flag=1;
                    Bundle bundle = new Bundle();
                    bundle.putInt("flag",flag);
                    bundle.putSerializable("msg_data", msg);
                    bundle.putInt("result",0);
                    Navigation.findNavController(itemView).navigate(R.id.action_global_showMsgFragment,bundle);
                }
            });

            //设置长按监听 (删除/编辑)
            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Message msg = data.get(position);
                    new AlertDialog.Builder(activity)
                            .setCancelable(true)
                            .setPositiveButton("删除", (dialog, which) -> {
                                // 开启后台线程执行数据库操作
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    dao.deletemessage(msg);
                                    // 必须切回主线程更新 UI
                                    activity.runOnUiThread(() -> {
                                        data.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, data.size() - position);
                                        Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show();
                                    });
                                });
                            })
                            .setNegativeButton("编辑", (dialog, which) -> {
                                Bundle result = new Bundle();
                                result.putInt("result",1);
                                result.putSerializable("msg_data", msg);
                              Navigation.findNavController(itemView).navigate(R.id.action_navigation_home_to_addMsgFragment,result);
                            })
                            .show();
                }
                return true; // 返回 true 表示事件已处理
            });
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=activity.getLayoutInflater().inflate(R.layout.msg_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message= data.get(position);
        int daysBetween=message.getTime();
        holder.time.setText(String.valueOf(Math.abs(daysBetween)));
        String text;
        if (daysBetween == 0) {
            text = message.getTitle() + "就是今天";
        } else if (daysBetween > 0) {
            text = message.getTitle() + "还有";
        } else {
            text = message.getTitle() + "已经";
        }
        holder.title.setText(text);
        }

    @Override
    public int getItemCount() {
        return data.size();
    }



}
