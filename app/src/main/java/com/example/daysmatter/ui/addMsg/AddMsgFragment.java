package com.example.daysmatter.ui.addMsg;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.daysmatter.R;
import com.example.daysmatter.databinding.FragmentAddMsgBinding;
import com.example.daysmatter.logic.room.Category;
import com.example.daysmatter.logic.room.CountdownDatabase;
import com.example.daysmatter.logic.room.Message;
import com.example.daysmatter.logic.room.MessageDao;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AddMsgFragment extends Fragment {
    private LocalDate today;
    private String id;
    private LocalDate selectedDate;
    private MessageDao dao;
    private AddMsgViewModel mViewModel;
    private FragmentAddMsgBinding binding;

    // 建议在初始化时给一个默认值，防止第一次跳转传 0
    private int currentIconId = R.drawable.life;
    private int result;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddMsgBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. 初始化核心组件
        dao = CountdownDatabase.getDatabase(requireContext()).messageDao();
        mViewModel = new ViewModelProvider(this).get(AddMsgViewModel.class);
        today = LocalDate.now();
        getParentFragmentManager().setFragmentResultListener("category_request", getViewLifecycleOwner(), (requestKey, bundle) -> {
            int iconId = bundle.getInt("icon_id", -1);
            String name = bundle.getString("category_name");

            if (iconId != -1 && name != null) {
                // 关键：同步类成员变量，防止保存时变回默认
                this.currentIconId = iconId;

                // 更新 UI
                binding.showIconCategoryBook.setImageResource(iconId);
                binding.showNameCategoryBook.setText(name);

                // 建议：移除 ImageView 的 Tint 效果，显示图标本色
                binding.showIconCategoryBook.setImageTintList(null);
            }
        });
        // 2. 处理参数与数据初始化
        Bundle b = getArguments();
        if (b != null) {
            result = b.getInt("result");
            if (result == 1) { // 编辑模式
                Message msg = (Message) b.getSerializable("msg_data");
                if (msg != null) {
                    id = msg.getId();
                    selectedDate = LocalDate.parse(msg.getAimdate());
                    currentIconId = msg.getCategoryIcon(); // 同步图标变量

                    binding.firstaddmsg.setText(msg.getTitle());
                    binding.showDate.setText(msg.getAimdate());
                    binding.switchPin.setChecked(msg.isTop());
                    binding.showIconCategoryBook.setImageResource(currentIconId);
                    binding.showNameCategoryBook.setText(msg.getCategoryName());
                }
            } else { // 新增模式
                initDefault();
            }
        }

        setUpObservers();
        setOnClicked();

        // 3. 监听分类选择页面的回传
        getParentFragmentManager().setFragmentResultListener("category_request", getViewLifecycleOwner(), (requestKey, bundle) -> {
            int iconId = bundle.getInt("icon_id", -1);
            String name = bundle.getString("category_name");

            if (iconId != -1) {
                this.currentIconId = iconId; // 重要：同步更新变量，确保保存时 ID 正确
                binding.showIconCategoryBook.setImageResource(iconId);
                binding.showNameCategoryBook.setText(name);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initDefault() {
        selectedDate = LocalDate.now();
        binding.showDate.setText(today.toString());
        binding.showIconCategoryBook.setImageResource(R.drawable.life);
        binding.showNameCategoryBook.setText("生活");
        currentIconId = R.drawable.life;
    }

    private void setUpObservers() {
        mViewModel.getDateDisplay().observe(getViewLifecycleOwner(), dateText -> {
            binding.showDate.setText(dateText);
        });

        // 如果 ViewModel 中维护了图标，则通过观察者同步
        mViewModel.getIconId().observe(getViewLifecycleOwner(), iconId -> {
            if (iconId != null && iconId != 0) {
                binding.showIconCategoryBook.setImageResource(iconId);
                currentIconId = iconId;
            }
        });
    }

    private void setOnClicked() {
        binding.datePicker.setOnClickListener(v -> showDataPicker());

        binding.insertMsg.setOnClickListener(v -> saveMsg());

        binding.startChooseBook.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("flag", 1);
            // 确保传过去的是当前页面显示的图标 ID，用于高亮选中
            bundle.putInt("iconId", currentIconId);
            Navigation.findNavController(v).navigate(R.id.action_addMsgFragment_to_settingCategoryFragment, bundle);
        });
    }

    @SuppressLint({"NewApi", "LocalSuppress"})
    private void saveMsg() {
        String title = binding.firstaddmsg.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "请输入标题", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isTop = binding.switchPin.isChecked();
        String categoryName = binding.showNameCategoryBook.getText().toString();

        // 计算日期差
        int daysBetween = (int) ChronoUnit.DAYS.between(today, selectedDate);

        if (result == 1) {
            mViewModel.updateMessage(id, dao, title, isTop, selectedDate, currentIconId, categoryName);
        } else {
            mViewModel.saveMessage(dao, title, isTop, selectedDate, currentIconId, categoryName);
        }

        // 4. 数据回传给上一个页面并关闭
        if (isAdded() && getView() != null) {
            Bundle bundle = new Bundle();
            Message msg = new Message(id, title, daysBetween, selectedDate.toString(), isTop, currentIconId, categoryName);
            bundle.putSerializable("msg_data", msg);
            bundle.putInt("result", 1);

            getParentFragmentManager().setFragmentResult("edit_msg_result", bundle);
            Navigation.findNavController(getView()).popBackStack();
        }
    }

    @SuppressLint({"NewApi", "LocalSuppress"})
    private void showDataPicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                requireActivity(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    mViewModel.updateDate(year, month, dayOfMonth);
                },
                selectedDate.getYear(),
                selectedDate.getMonthValue() - 1,
                selectedDate.getDayOfMonth()
        );
        dialog.show();
    }
}