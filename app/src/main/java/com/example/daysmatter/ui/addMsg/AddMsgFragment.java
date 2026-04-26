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
    private int currentIconId;
    private int result;

    public static AddMsgFragment newInstance() {
        return new AddMsgFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding=FragmentAddMsgBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle b=getArguments();
        result=b.getInt("result");
        if(result==0){
            init();
        }else{
            dao= CountdownDatabase.getDatabase(requireContext()).messageDao();
            mViewModel=new ViewModelProvider(this).get(AddMsgViewModel.class);
            Message msg=(Message) b.getSerializable("msg_data");
            today=LocalDate.now();
            id=msg.getId();
            selectedDate=LocalDate.parse(msg.getAimdate());
            binding.firstaddmsg.setText(msg.getTitle());
            binding.showDate.setText(msg.getAimdate());
            binding.switchPin.setChecked(msg.isTop());
            binding.showIconCategoryBook.setImageResource(msg.getCategoryIcon());
            binding.showNameCategoryBook.setText(msg.getCategoryName());
        }

        setUpObservers();
        setOnClicked();
        getParentFragmentManager().setFragmentResultListener("category_request", this, (requestKey, bundle) -> {
            int iconId = bundle.getInt("icon_id", -1);
            String name = bundle.getString("category_name");

            // 更新 UI
            binding.showIconCategoryBook.setImageResource(iconId);
            binding.showNameCategoryBook.setText(name);
        });
    }
    @SuppressLint("NewApi")
    private void init(){

        dao= CountdownDatabase.getDatabase(requireContext()).messageDao();
        mViewModel=new ViewModelProvider(this).get(AddMsgViewModel.class);
        //获取本地时间
        today = LocalDate.now();
        selectedDate = LocalDate.now();
        binding.showDate.setText(today.toString());
        binding.showIconCategoryBook.setImageResource(R.drawable.life);
        binding.showNameCategoryBook.setText("生活");
    }
    private void setUpObservers(){
        mViewModel.getDateDisplay().observe(getViewLifecycleOwner(),dateText->{
            binding.showDate.setText(dateText);
        });
        mViewModel.getIconId().observe(getViewLifecycleOwner(),iconId->{
            binding.showIconCategoryBook.setImageResource(iconId);
            currentIconId=iconId;
        });

    }
    //点击事件
    private void setOnClicked() {
            binding.datePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDataPicker();
                }
            });
            binding.insertMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        saveMsg();
                }
            });
            binding.startChooseBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(getView()).navigate(R.id.action_addMsgFragment_to_settingCategoryFragment);
                }
            });
    }
    private void saveMsg(){
        String title = binding.firstaddmsg.getText().toString();
        if (title.isBlank()) {
            Toast.makeText(requireContext(), "标题为空，请输入标题后保存", Toast.LENGTH_LONG).show();
            return;
        }
            Boolean isTop = binding.switchPin.isChecked(); // 读取置顶状态
            @SuppressLint({"NewApi", "LocalSuppress"}) int daysBetween = (int) ChronoUnit.DAYS.between(today, selectedDate);
            LocalDate aimDate = selectedDate;
            if(result==1){
                mViewModel.updateMessage(id,dao,title,isTop,aimDate,currentIconId,binding.showNameCategoryBook.getText().toString());
            }else{
                mViewModel.saveMessage(dao,title,isTop,aimDate,currentIconId,binding.showNameCategoryBook.getText().toString());
            }

        if (getView() != null) {
            Bundle  bundle = new Bundle();
            Message msg=new Message(id,title,daysBetween,aimDate.toString(),isTop,currentIconId,binding.showNameCategoryBook.getText().toString());
            bundle.putSerializable("msg_data",msg);
            bundle.putInt("flag",1);
            bundle.putInt("result",1);
            getParentFragmentManager().setFragmentResult("edit_msg_result", bundle);
            Navigation.findNavController(getView()).popBackStack();
        }

    }
    //时间选择器
    private void showDataPicker() {
        @SuppressLint({"NewApi", "LocalSuppress"}) DatePickerDialog dialog = new DatePickerDialog(
                requireActivity(),
                (view, year, month, dayOfMonth) -> {
                    LocalDate newDate = LocalDate.of(year, month + 1, dayOfMonth);
                    this.selectedDate = newDate;
                    // 将结果交给 ViewModel 处理
                    mViewModel.updateDate(year, month, dayOfMonth);
                },
                selectedDate.getYear(),
                selectedDate.getMonthValue() - 1,
                selectedDate.getDayOfMonth()
        );
        dialog.show();
    }

}